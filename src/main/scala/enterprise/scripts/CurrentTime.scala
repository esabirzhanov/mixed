package enterprise.scripts

object CurrentTime extends App {
  println(System.currentTimeMillis())

  import java.time.Instant

  val current = Instant.ofEpochMilli(System.currentTimeMillis())
  println(current)

  val future = Instant.ofEpochMilli(9007199254740991L)

  val maxInt = Integer.MAX_VALUE
  println(maxInt)

  println(future)
}
