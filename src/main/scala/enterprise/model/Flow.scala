package enterprise.model


import java.time.Instant

import io.circe._
import io.circe.generic.semiauto._

case class Flow(id: Long,
                startActive: Instant,
                lastActive: Instant,
                servicePort: Int,
                protocol: Int)
object Flow {
  implicit val flowEncoder: Encoder[Flow] = deriveEncoder[Flow]
  implicit val flowDecoder: Decoder[Flow] = deriveDecoder
}





