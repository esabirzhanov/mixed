package cisco.repositories

import java.util.UUID

import cats.data.OptionT
import cats.effect.Effect
import cats.implicits._
import cisco.model.ArtistGroup

import scala.collection.mutable.ListBuffer

final case class ArtistGroupRepository [F[_]](private val groups: ListBuffer[ArtistGroup])(implicit F: Effect[F])  {

  val makeId: F[String] = F.delay[String] { UUID.randomUUID().toString  }

  def getGroups(): F[List[ArtistGroup]] = F.delay[List[ArtistGroup]] { groups.result() }

  def getGroup(id: String): F[Option[ArtistGroup]] = F.delay { groups.find(_.id == id) }

  def addGroup(group: ArtistGroup): F[String] = for {
    uuid <- makeId
    _ <- F.delay { groups += group.copy(id = uuid) }
  } yield uuid


  private def doUpdateGroup(group: ArtistGroup): OptionT[F, Unit] = for {
    cg <- OptionT(getGroup(group.id))
    _ <- OptionT.liftF(F.delay(groups -= cg))
    _ <- OptionT.liftF(F.delay(groups += group))
  } yield ()

  def updateGroup(group: ArtistGroup): F[Option[Unit]] = doUpdateGroup(group).value

  def deleteGroup(gId: String): F[Unit] =
    F.delay { groups.find(_.id == gId).foreach(g => groups -= g) }
}

object ArtistGroupRepository {
  def initialise[F[_]](lb: ListBuffer[ArtistGroup])(implicit m: Effect[F]): F[ArtistGroupRepository[F]] = m.pure{new ArtistGroupRepository[F](lb)}
}