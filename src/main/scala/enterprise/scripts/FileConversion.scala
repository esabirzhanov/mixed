package enterprise.scripts

import java.net.InetAddress

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import fs2.{Stream, io, text}
import java.nio.file.Paths
import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

object FileConversion extends IOApp {

  private val blockingExecutionContext =
    Resource.make(IO(ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2))))(ec => IO(ec.shutdown()))

  /* all database fields are enumerated here in required order */
  private val dbFields: List[String] = List(
    "flow_id",
    "start_active_usec",
    "last_active_usec",
    "username",
    "client_ip",
    "client_group_list",
    "client_num_bytes",
    "server_ip",
    "server_group_list",
    "server_num_bytes",
    "service_port",
    "protocol"
  )
  private val hexArray: Array[Char] = "0123456789abcdef".toCharArray

  def converter(fileName: String)= Stream.resource(blockingExecutionContext).flatMap { blockingEC =>

    def convert(line: String): String =  {
      val pairs: Array[String] = line.split('|')
      val mapping = pairs.foldLeft(Map.empty[String, String])( (acc, entry) => {
        val pair: Array[String] = entry.split("=")
        val key = pair(0)
        val value = pair(1)
        acc + (key -> value)
      })

      val result = dbFields.foldLeft("")((acc, entry) => {
        val res = mapping.getOrElse(entry, "null")
        acc.concat(String.format("%s\t", res))
      } )
      result.slice(0, result.length - 1)
    }

    def convertIp(rawIp: String): String = {
      val ip = InetAddress.getByName(rawIp)
      val bytes = ip.getAddress
      /* it is simplified ip consists of 4 bytes */
      "0x00000000000000000000ffff" concat(toRawHex(bytes))
    }

    def toRawHex(bytes: Array[Byte]): String = {
      val hexData = bytes.foldLeft(List[Char]()) {(acc, byte) => {
        val value: Int = byte & 0xFF
        hexArray(value & 0x0F) :: hexArray(value >>> 4) :: acc
      }}
      hexData.reverse.mkString
    }


    io.file.readAll[IO](Paths.get(s"input-data/${fileName}"), blockingEC, 4096)
      .through(text.utf8Decode)
      .through(text.lines)
      .filter(s => !s.trim.isEmpty && !s.startsWith("//"))
      .map(line => convert(line))
      .intersperse("\n")
      .through(text.utf8Encode)
      .through(io.file.writeAll(Paths.get(s"output-data/${fileName}"), blockingEC))
  }




  @Override
  def run(args: List[String]): IO[ExitCode] = converter("message.log").compile.drain.as(ExitCode.Success)
}




