package enterprise.model

import java.net.InetAddress
import java.time.Instant

import doobie.util.Get
import io.circe.generic.semiauto._
import cats.syntax.either._
import io.circe.{Decoder, Encoder}


case class Flow(id: Long,
                startActive: Instant,
                lastActive: Instant,
                userName: Option[String],
                clientIp: InetAddress,
                clientGroupList: List[Int],
                clientBytes: Long,
                serverIp: InetAddress,
                serverGroupList: List[Int],
                serverBytes: Long,
                servicePort: Int,
                protocol: Int)

object Flow {

  implicit val flowEncoder: Encoder[Flow] = deriveEncoder[Flow]
  implicit val flowDecoder: Decoder[Flow] = deriveDecoder
  implicit val encodeIp: Encoder[InetAddress] = Encoder.encodeString.contramap[InetAddress] { inet => inet.toString.substring(1) }
  implicit val decodeIp: Decoder[InetAddress] = Decoder.decodeString.emap { str =>
    Either.catchNonFatal( InetAddress.getByName(str)).leftMap(t => s"something happened -> ${t.getMessage}")
  }

  implicit val encodeHgs: Encoder[List[Int]] = Encoder.encodeString.contramap[List[Int]] { hgs => hgs.toString }
  implicit val decodeHgs: Decoder[List[Int]] = Decoder.decodeString.emap { str =>
    Either.catchNonFatal(str.split(",").map(it => it.toInt).toList).leftMap(t => s"something happened -> ${t.getMessage}")
  }

  def convertIp(ip: String): InetAddress = {
    InetAddress.getByName(ip)
  }

  def convertHgs(hgs: String): List[Int] = {
    hgs.substring(1, hgs.length - 1).split(",").map(it => it.trim.toInt).toList
  }

  implicit val hgGet: Get[List[Int]] = Get[String].map(convertHgs)

  implicit val ipGet: Get[InetAddress] = Get[String].map(convertIp)
}





