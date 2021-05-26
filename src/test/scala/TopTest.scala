import chisel3._
import org.scalatest._
import chiseltest._
import chiseltest.ChiselScalatestTester
import chiseltest.internal.VerilatorBackendAnnotation
import chiseltest.experimental.TestOptionBuilder._
import org.scalatest.FreeSpec

class TopTest extends FreeSpec with ChiselScalatestTester {
  def getFile: Option[String] = {
    if (scalaTestContext.value.get.configMap.contains("memFile")) {
      Some(scalaTestContext.value.get.configMap("memFile").toString)
    } else {
      None
    }
  }
  "should just work" in {
    val programFile = getFile
    test(new Top(programFile)).withAnnotations(Seq(VerilatorBackendAnnotation)) {c =>
      c.clock.setTimeout(0)
      c.clock.step(8000)
    }
  }
}