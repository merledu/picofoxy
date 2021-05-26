import chisel3._
import buraq_mini.core.Core
import caravan.bus.common.{AddressMap, BusDecoder, Switch1toN}
import caravan.bus.wishbone.{Peripherals, WBRequest, WBResponse, WishboneConfig, WishboneDevice, WishboneErr, WishboneHost, WishboneMaster, WishboneSlave}
import jigsaw.rams.fpga.BlockRam
import jigsaw.peripherals.gpio._

class Top(programFile: Option[String]) extends Module {
  val io = IO(new Bundle {
    val gpio_i = Input(UInt(32.W))
    val gpio_o = Output(UInt(32.W))
    val gpio_en_o = Output(UInt(32.W))
    val gpio_intr_o = Output(UInt(32.W))
  })

  implicit val config: WishboneConfig = WishboneConfig(32, 32)
  val wb_imem_host = Module(new WishboneHost())
  val wb_imem_slave = Module(new WishboneDevice())
  val wb_dmem_host = Module(new WishboneHost())
  val wb_dmem_slave = Module(new WishboneDevice())
  val wb_gpio_slave = Module(new WishboneDevice())
  val imem = Module(BlockRam.createNonMaskableRAM(programFile, bus=config, rows=1024))
  val dmem = Module(BlockRam.createMaskableRAM(bus=config, rows=1024))
  val gpio = Module(new Gpio(new WBRequest(), new WBResponse()))
  val wbErr = Module(new WishboneErr())
  val core = Module(new Core())

  val addressMap = new AddressMap
  addressMap.addDevice(Peripherals.DCCM, "h40000000".U(32.W), "h00000FFF".U(32.W), wb_dmem_slave)
  addressMap.addDevice(Peripherals.GPIO, "h40001000".U(32.W), "h00000FFF".U(32.W), wb_gpio_slave)
  val devices = addressMap.getDevices

  val switch = Module(new Switch1toN(new WishboneMaster(), new WishboneSlave(), devices.size))

  // WB <-> Core (fetch)
  wb_imem_host.io.reqIn <> core.io.imemReq
  core.io.imemRsp <> wb_imem_host.io.rspOut
  wb_imem_slave.io.reqOut <> imem.io.req
  wb_imem_slave.io.rspIn <> imem.io.rsp

  // WB <-> WB (fetch)
  wb_imem_host.io.wbMasterTransmitter <> wb_imem_slave.io.wbMasterReceiver
  wb_imem_slave.io.wbSlaveTransmitter <> wb_imem_host.io.wbSlaveReceiver

  // WB <-> Core (memory)
  wb_dmem_host.io.reqIn <> core.io.dmemReq
  core.io.dmemRsp <> wb_dmem_host.io.rspOut
  wb_dmem_slave.io.reqOut <> dmem.io.req
  wb_dmem_slave.io.rspIn <> dmem.io.rsp


  // Switch connection
  switch.io.hostIn <> wb_dmem_host.io.wbMasterTransmitter
  switch.io.hostOut <> wb_dmem_host.io.wbSlaveReceiver
  for (i <- 0 until devices.size) {
    switch.io.devIn(devices(i)._2.litValue().toInt) <> devices(i)._1.asInstanceOf[WishboneDevice].io.wbSlaveTransmitter
    switch.io.devOut(devices(i)._2.litValue().toInt) <> devices(i)._1.asInstanceOf[WishboneDevice].io.wbMasterReceiver
  }
  switch.io.devIn(devices.size) <> wbErr.io.wbSlaveTransmitter
  switch.io.devOut(devices.size) <> wbErr.io.wbMasterReceiver
  switch.io.devSel := BusDecoder.decode(wb_dmem_host.io.wbMasterTransmitter.bits.adr, addressMap)


  wb_gpio_slave.io.reqOut <> gpio.io.req
  wb_gpio_slave.io.rspIn <> gpio.io.rsp


  gpio.io.cio_gpio_i := io.gpio_i
  io.gpio_o := gpio.io.cio_gpio_o
  io.gpio_en_o := gpio.io.cio_gpio_en_o
  io.gpio_intr_o := gpio.io.intr_gpio_o



  core.io.stall_core_i := false.B
  core.io.irq_external_i := false.B


}
