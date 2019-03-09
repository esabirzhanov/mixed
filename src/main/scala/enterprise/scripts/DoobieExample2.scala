package enterprise.scripts

import cats.effect.{ExitCode, IO, IOApp}
import doobie.FC
import doobie.free.connection.ConnectionIO
import doobie.util.transactor.Transactor
import enterprise.model.Flow

import scala.concurrent.ExecutionContext
import cats.implicits._

import doobie._, doobie.implicits._


object DoobieExample2 extends IOApp {

  import Flow.ipGet
  import Flow.hgGet


  implicit val cs = IO.contextShift(ExecutionContext.global)

  import Fragments.{ in, whereAndOpt }


  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:mixed", "esabirzh", ""
  )

  def run(args:List[String]): IO[ExitCode] = prog.as(ExitCode.Success)

  /* Construct a Query with some optional filter conditions and a configurable LIMIT. */
  def select(name: Option[String], port: Option[Int], ids: List[Long], limit: Long)= {

    /* Three optional filter conditions. */
    val f1 = name.map(s => fr"username LIKE $s")
    val f2 = port.map(n => fr"service_port > $n")
    val f3 = ids.toNel.map(cs => in(fr"flow_id", cs))

    // Our final query
    val q: Fragment =
      fr"SELECT * FROM flows" ++
        whereAndOpt(f1, f2, f3) ++
        fr"LIMIT $limit"


    q.query[Flow]

  }

  val prog =  {
    val y = xa.yolo
    import y._

    val q = select(Some("joegibso"), None, List(4636, 4666), 20)
    q.check *> q.quick

  }
}
