import chisel3._

class Hello extends Module {
  val io = IO(new Bundle {
    val in = Input(Bool())
    val out = Output(Bool())
  })

  io.out := io.in
}