package enterprise


import java.nio.{ByteBuffer, ByteOrder}
import java.time.{ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter

import cats.data.NonEmptyList
import cats.implicits._
import cats.effect._
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import enterprise.model.{ArtistGroup, Flow}
import enterprise.repositories.{ArtistGroupRepository, FlowRepository}
import enterprise.validators.ErrorValidation
import enterprise.validators.ArtistGroupFormValidator._
import fs2.text.utf8Decode
import fs2.concurrent.Queue
import fs2._
import org.http4s.{EntityEncoder, HttpRoutes, Response, Status}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebsocketBits._
import org.http4s.circe._
import org.http4s.multipart.{Multipart, Part}
import org.http4s.websocket.{FrameTranscoder, WebsocketBits}

import scala.concurrent.duration._
import fs2.Stream





class WebSocketRoutes[F[_]: Sync] (agRepo: ArtistGroupRepository[F], afRepo: FlowRepository[F])(implicit F: ConcurrentEffect[F], timer: Timer[F])
          extends Http4sDsl[F]  {

  val GROUPS = "groups"
  val API = "api"
  val STREAMED = "streamed"
  val FLOWS = "flows"
  val FLOWS_STREAMED = "flows-streamed"
  val FLOWS_STREAMED_V2 = "flows-streamed-v2"

  val ID_PRM = "id"
  val NAME_PRM = "name"
  val CATEGORY_PRM = "category"
  val OUTSTANDING_PRM = "outstanding"
  val PICTURE_PRM = "picture"
  val DESCRIPTION_PRM = "description"

  // implicit val FlowEncoder: EntityEncoder[F, Flow] = EntityEncoder.encodeBy[F, Flow](`Transfer-Encoding` (TransferCoding.chunked) )(f => Entity.empty)

  final val h: Flow => Chunk[Byte] = (f: Flow) => {
    val flowId: Array[Byte] = ByteBuffer.allocate(8).putLong(f.id).array()
    val flowStartActive: Array[Byte] = ByteBuffer.allocate(8).putLong(f.startActive.toEpochMilli ).array()
    val flowLastActive: Array[Byte] = ByteBuffer.allocate(8).putLong(f.lastActive.toEpochMilli ).array()
    val servicePort: Array[Byte] = ByteBuffer.allocate(4).putInt(f.servicePort ).array()
    val protocol: Array[Byte] = ByteBuffer.allocate(4).putInt(f.protocol ).array()
    Chunk.bytes( flowId ++ flowStartActive ++ flowLastActive ++ servicePort ++ protocol )
  }

  final val g: Flow => WebSocketFrame = (f: Flow) => {
    val bb = ByteBuffer.allocate(32).order(ByteOrder.LITTLE_ENDIAN)
    bb.putLong(f.id)
    bb.putLong(f.startActive.toEpochMilli)
    bb.putLong(f.lastActive.toEpochMilli)
    bb.putInt(f.servicePort)
    bb.putInt(f.protocol)

    val preamble: Array[Byte] = Array(0x82, 0x7E, 0x00, bb.array.length).map(_.toByte)
    val ba = ByteBuffer.allocate(preamble.length + bb.array.length)
    ba.put(preamble)
    ba.put(bb.array)
    decode( ba.array )
  }



  implicit val w1: EntityEncoder[F, Flow] = EntityEncoder.simple[F, Flow]()( h )


  def routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "hello" =>
      Ok("Hello world version 1.")

    case GET -> Root / "info" => {
      val toClient: Stream[F, WebSocketFrame] =
        Stream.awakeEvery[F](1.seconds).map(d => Text(s"Ping! $d"))
      val fromClient: Sink[F, WebSocketFrame] = _.evalMap {
        case Text(t, _) => F.delay(println(t))
        case f => F.delay(println(s"Unknown type: $f"))
      }

      WebSocketBuilder[F].build(toClient, fromClient)
    }

    case GET -> Root / "binary" => {
      val toClient: Stream[F, WebSocketFrame] = {
        val data = Array(0x82, 0x7E, 0x00, 0x03, 0x7B, 0x03, 0x04).map(_.toByte)
        val frame: WebSocketFrame = decode(data)
        Stream.awakeEvery[F](10.seconds).map(d => frame )
      }

      val fromClient: Sink[F, WebSocketFrame] = _.evalMap {
        case Text(t, _) => F.delay(println(t))
        case f => F.delay(println(s"Unknown type: $f"))
      }

      WebSocketBuilder[F].build(toClient, fromClient)
    }

    case GET -> Root / STREAMED / FLOWS => {
      val toClient: Stream[F, WebSocketFrame] = {
        afRepo.getFlowsStreamed(10).map[WebSocketFrame](g)
      }

      val fromClient: Sink[F, WebSocketFrame] = _.evalMap {
        case Text(t, _) => F.delay(println(t))
        case f => F.delay(println(s"Unknown type: $f"))
      }

      WebSocketBuilder[F].build(toClient, fromClient)
    }



    case GET -> Root / "msg" => {
      val toClient: Stream[F, WebSocketFrame] =
        Stream.awakeEvery[F](1.seconds).map(d => Text(s"Ping! $d"))
      val fromClient: Sink[F, WebSocketFrame] = _.evalMap {
        case Text(t, _) => F.delay(println(t))
        case f => F.delay(println(s"Unknown type: $f"))
      }
      WebSocketBuilder[F].build(toClient, fromClient)

    }

    case GET -> Root / "wsecho2" => {
      println("here !!!!!!!")
      val echoReply: Pipe[F, WebSocketFrame, WebSocketFrame] =
        _.collect {
          case Text(msg, _) => Text("You sent the server: " + msg)
          case _ => Text("Something new")
        }
      val queue = Queue
        .unbounded[F, WebSocketFrame]

      queue
        .flatMap { q =>
          val d = q.dequeue.through(echoReply)
          val e = q.enqueue
          WebSocketBuilder[F].build(d, e)
        }
    }

    case GET -> Root / "ping" =>
      // EntityEncoder allows for easy conversion of types to a response body
      Ok("pong")

    case req @ GET -> Root / "ip" =>
      // It's possible to define an EntityEncoder anywhere so you're not limited to built in types
      val json = Json.obj("origin" -> Json.fromString(req.remoteAddr.getOrElse("unknown")))
      Ok(json)

    case GET -> Root / "hello" / name =>
      Ok(Json.obj("message" -> Json.fromString(s"Hello, ${name}")))

    case GET -> Root / API / GROUPS => {
      agRepo.getGroups()
        .flatMap { (groups: List[ArtistGroup]) => {
        //   Response(status = Status.Ok).withBody[Json](groups.asJson)
          val temp: Response[F]  = Response[F](status = Status.Ok)
          val temp2: Response[F] = temp.withEntity[Json](groups.asJson)
          temp2.pure[F]
        }
        }
    }

    case GET -> Root / API / GROUPS / groupId =>
      agRepo.getGroup(groupId)
        .flatMap {
          case Some(group) => Response[F](status = Status.Ok).withEntity(group.asJson).pure[F]
          case None      => F.pure(Response(status = Status.NotFound))
        }

    case req @ POST -> Root / API / GROUPS =>
      req.decodeJson[ArtistGroup]
        .flatMap(agRepo.addGroup)
        .flatMap(gId => Response[F](status = Status.Created).withEntity(gId.asJson).pure[F])

    case req @ PUT -> Root / API / GROUPS =>
      req.decodeJson[ArtistGroup]
        .flatMap(agRepo.updateGroup)
        .flatMap(_ => F.pure(Response(status = Status.Ok)))

    case DELETE -> Root / API / GROUPS / gId =>
      agRepo.deleteGroup(gId)
        .flatMap(_ => F.pure(Response(status = Status.NoContent)))


    case GET -> Root / "streaming" =>
      // It's also easy to stream responses to clients
      Ok(dataStream2(100))


    case req @ PUT -> Root / "form-encoded" =>
      req.decode[Multipart[F]] ( m => {
        val ps: Map[String, F[String]] = m.parts.foldLeft(Map.empty[String, F[String]]) ( (acc: Map[String, F[String]], t: Part[F]) => {
          t.name match {
            case Some(name) =>  {
              val content: F[String] = t.body.through(utf8Decode).through(_.evalMap(s => F.delay[String]{s})).compile.foldMonoid
              acc + (name -> content)
            }
            case None => acc
          }
        })

        val keyData: List[String] = ps.keys.toList
        val effData: F[List[String]] = ps.values.toList traverse identity
        effData.flatMap( data => {
          val prs = Map.empty[String, String] ++ (keyData zip data)
          val res: Either[NonEmptyList[ErrorValidation], ArtistGroup] =
            validateForm(prs.get(ID_PRM), prs.get(NAME_PRM), prs.get(CATEGORY_PRM), prs.get(OUTSTANDING_PRM), prs.get(PICTURE_PRM), prs.get(DESCRIPTION_PRM)).toEither
          res match {
            case Right(ag) => agRepo.updateGroup(ag).flatMap({
              _ => F.pure(Response(status = Status.Ok))
            })
            case Left(errs: NonEmptyList[ErrorValidation]) => Response[F](status = Status.Ok).withEntity(errs.asJson).pure[F]
          }
        })
      })

    case GET -> Root / API / FLOWS => {
      afRepo.getFlows(10)
        .flatMap { (flows: List[Flow]) => Response[F](status = Status.Ok).withEntity[Json](flows.asJson).pure[F]
        }
    }

    case GET -> Root / API / FLOWS_STREAMED => {
      val flowsStreamed: Stream[F, Flow] = afRepo.getFlowsStreamed(10)
      val flows: F[List[Flow]] = flowsStreamed.compile.toList
      flows.flatMap {
        (flows: List[Flow]) => Response[F](status = Status.Ok).withEntity[Json](flows.asJson).pure[F]
      }
    }

    case GET -> Root / API / FLOWS_STREAMED_V2 => {

      val flowsStreamed: Stream[F, Flow] = afRepo.getFlowsStreamed(10)

      // EntityEncoder[F, Stream[F, Flow]].headers.get(`Transfer-Encoding`)
      Ok( flowsStreamed )


    }



  }

  // This is a mock data source, but could be a Process representing results from a database
  def dataStream(n: Int)(implicit timer: Timer[F]): Stream[F, String] = {
    val interval = 100.millis
    val stream: Stream[F, String] = Stream
      .awakeEvery[F](interval)
      .evalMap(_ => timer.clock.realTime(MILLISECONDS))
      .map(time => s"Current system time: $time ms\n")
      .take(n.toLong)

    Stream.emit(s"Starting $interval stream intervals, taking $n results\n\n") ++ stream
  }

  // This is a mock data source, but could be a Process representing results from a database
  def dataStream2(n: Int): Stream[F, String] = {
    val interval = 100.millis
    val stream: Stream[F, String] = Stream
      .awakeEvery[F](interval)
      .map(time => s"Current system time: $time ms\n")
      .take(n.toLong)
    stream
  }

  private def decode(msg: Array[Byte]): WebSocketFrame =
    new FrameTranscoder(false).bufferToFrame(ByteBuffer.wrap(msg))




}







