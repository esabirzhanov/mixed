package enterprise.repositories

import cats.effect.{Effect, IO}

import doobie.implicits._

import doobie.util.transactor.Transactor
import enterprise.model.Flow
import fs2._
import Flow._

import scala.concurrent.ExecutionContext

class FlowRepository [F[_]](xa: Transactor.Aux[F, Unit])(implicit F: Effect[F]) {
  implicit val cs = IO.contextShift(ExecutionContext.global)

  def query(pct: Double) = sql"SELECT flow_id, start_active, last_active, username, client_ip_address, client_group_list, client_bytes, server_ip_address, server_group_list, server_bytes, service_port, protocol FROM flows WHERE flow_id > $pct".query[Flow]

  def getFlowsStreamed(pct: Double): Stream[F, Flow] = query(pct).stream.transact(xa)

  def getFlows(pct: Double): F[List[Flow]] = query(pct).to[List].transact(xa)
}

object FlowRepository {
  def initialise[F[_]](xa: Transactor.Aux[F, Unit])(implicit m: Effect[F]): F[FlowRepository[F]] = m.pure{ new FlowRepository[F](xa) }
}
