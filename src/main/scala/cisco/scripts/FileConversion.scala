package cisco.scripts

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import fs2.{io, text, Stream}
import java.nio.file.Paths
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object FileConversion extends IOApp {

  private val blockingExecutionContext =
    Resource.make(IO(ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2))))(ec => IO(ec.shutdown()))

  def converter(fileName: String): Stream[IO, Unit] = Stream.resource(blockingExecutionContext).flatMap { blockingEC =>
    def toDatabase(f: String): String =  {


    //  val pairs: Array[String] = f.split("|")
    //  val flowId = pairs(0).splitAt('=')(1)
   //   val startActive = pairs(1).splitAt('=')(1)





      f
    }

    io.file.readAll[IO](Paths.get(s"input-data/${fileName}"), blockingEC, 4096)
      .through(text.utf8Decode)
      .through(text.lines)
      .filter(s => !s.trim.isEmpty && !s.startsWith("//"))
      .map(line => toDatabase(line))
      .intersperse("\n")
      .through(text.utf8Encode)
      .through(io.file.writeAll(Paths.get(s"output-data/${fileName}"), blockingEC))
  }

  @Override
  def run(args: List[String]): IO[ExitCode] = converter("message3.log").compile.drain.as(ExitCode.Success)
}




