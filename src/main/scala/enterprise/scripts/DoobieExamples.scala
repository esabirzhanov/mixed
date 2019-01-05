package enterprise.scripts

import cats.effect.{ExitCode, IO, IOApp}
import doobie.free.connection.ConnectionIO
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContext
import doobie._
import doobie.implicits._
import cats.implicits._


object DoobieExamples extends IOApp  {

  final case class CountryCode(code: Option[String])

  // We need a ContextShift[IO] before we can construct a Transactor[IO]. The passed ExecutionContext
  // is where nonblocking operations will be executed.
  implicit val cs = IO.contextShift(ExecutionContext.global)

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:mixed", "esabirzh", ""
  )

  // Entry point for SafeApp
  def run(args: List[String]): IO[ExitCode] = {
    example.transact(xa).as(ExitCode.Success)

  }

  // An example action. Streams results to stdout
  lazy val example: ConnectionIO[Unit] =
    speakerQuery(10).evalMap(c => FC.delay(println("~> " + s"$c"))).compile.drain


  // Construct an action to find countries where more than `pct` of the population speaks `lang`.
  // The result is a fs2.Stream that can be further manipulated by the caller.
  def speakerQuery(pct: Double) =  sql"SELECT service_port FROM flows WHERE flow_id > $pct".query[Int].stream

}
