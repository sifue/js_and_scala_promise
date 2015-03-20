import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

object MainFuture extends App {
  def double(value: Int): Int = value * 2
  def increment(value: Int): Int = value + 1
  def output(value: Int): Unit  = Console.println(value)

  val future = Future {1}
  (for {
    i1 <- future
    i2 <- Future {increment(i1)}
    i3 <- Future {double(i2)}
  } yield output(i3))
    .onFailure { case t: Throwable =>
      Console.println(t.getMessage)
    }

  Thread.sleep(1000)
}


