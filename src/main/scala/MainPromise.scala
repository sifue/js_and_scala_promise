import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

object MainPromise extends App {
  def output(value: Int): Unit  = Console.println(value)

  val promise = Promise[Int]
  Future {
    promise.trySuccess(0)
  }
  Future {
    Thread.sleep(100)
    promise.trySuccess(1)
  }
  promise.future.map(output) // 先に終了する0が出力される

  Thread.sleep(1000)
}


