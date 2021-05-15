import chisel3._
import org.scalatest._
import chiseltest._
import chiseltest.ChiselScalatestTester
import chiseltest.internal.VerilatorBackendAnnotation
import chiseltest.experimental.TestOptionBuilder._
import org.scalatest.FreeSpec

class HelloTest extends FreeSpec with ChiselScalatestTester {
  "should just work" in {
    test(new Hello).withAnnotations(Seq(VerilatorBackendAnnotation)) {c =>
      c.io.in.poke(true.B)
      c.io.out.expect(true.B)
    }
  }
}