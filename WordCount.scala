import java.nio.file.*

import scala.collection.mutable

@main
def WordCount(): Unit =
  val start = System.currentTimeMillis()

  val path = Paths.get("data")
  val map  = new mutable.HashMap[String, Int]

  Files
    .list(path)
    .filter: file =>
      Files.isRegularFile(file) && file.getFileName().toString().endsWith(".txt")
    .forEach: file =>
      val text = Files.readString(file)
      text
        .split("\\s+")
        .map: word =>
          word.filter(_.isLetterOrDigit).toLowerCase()
        .filter(_.nonEmpty)
        .foreach: word =>
          map(word) = map.getOrElse(word, 0) + 1

  println(s"Map count: ${map.size}")

  val vec = map.toVector.sortBy(_._2).reverse
  for i <- 0 until 10 do
    val (word, n) = vec(i)
    println(f"${i}%2d| ${word}%10s: ${n}%10d")

  val elapsed = System.currentTimeMillis() - start
  println(s"Time elapsed: ${elapsed} (ms).")
