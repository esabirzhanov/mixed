package enterprise.scripts

import cats.effect.{ExitCode, IO, IOApp}
import doobie.free.connection.ConnectionIO
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContext
import doobie._

import enterprise.model.Flow

import doobie.implicits._
import cats.implicits._


object DoobieExamples extends IOApp  {

  import Flow.ipGet
  import Flow.hgGet


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
    query(10).evalMap(c => FC.delay(println("~> " + s"$c"))).compile.drain


  // Construct an action to find countries where more than `pct` of the population speaks `lang`.
  // The result is a fs2.Stream that can be further manipulated by the caller.
  def speakerQuery(pct: Double) =  sql"SELECT service_port, client_ip_address FROM flows WHERE flow_id > $pct".query[(Int, String)].stream

  def query(pct: Double) = sql"SELECT flow_id, start_active, last_active, username, client_ip_address, client_group_list, client_bytes, server_ip_address, server_group_list, server_bytes, service_port, protocol FROM flows WHERE flow_id > $pct".query[Flow].stream


}
