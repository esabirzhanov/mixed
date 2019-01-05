package enterprise.scripts

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import fs2.{io, text, Stream}
import java.nio.file.Paths
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object FileConversion extends IOApp {

  private val blockingExecutionContext =
    Resource.make(IO(ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2))))(ec => IO(ec.shutdown()))

  def converter(fileName: String)= Stream.resource(blockingExecutionContext).flatMap { blockingEC =>
    def convert(data: String): String =  {
      val pairs: Array[String] = data.split('|')
      val flowId = pairs(0).split('=')(1)
      val startActive = pairs(1).split('=')(1)
      val lastActive = pairs(2).split('=')(1)
      val servicePort = pairs(3).split('=')(1)
      val protocol = pairs(4).split('=')(1)
      val output = String.format("%s\t%s\t%s\t%s\t%s",
        flowId,
        startActive, lastActive,
        servicePort, protocol)
      output
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
  def run(args: List[String]): IO[ExitCode] = converter("message3.log").compile.drain.as(ExitCode.Success)
}




