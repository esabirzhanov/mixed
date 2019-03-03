package enterprise.scripts



import cats.effect.{ExitCode, IO, IOApp}

import scala.concurrent.Future
import cats.implicits._

object AsyncExample extends IOApp {

  import scala.concurrent.ExecutionContext.Implicits.global

  val apiCall: Future[String] = Future.successful{
    Thread.sleep(5000)
    "I come from the Future!"}


  val ioa: IO[String] =
    IO async[String] { (cb: Either[Throwable, String] => Unit) =>
      import scala.util.{Failure, Success}

      println("1!!!!!")
      apiCall.onComplete {
        case Success(value) => {
          println("2!!!!!!")
          cb(Right(value))
        }
        case Failure(error) => cb(Left(error))
      }
    }

  def run(args: List[String]): IO[ExitCode] = {
    ioa.as(ExitCode.Success)
  }

}
