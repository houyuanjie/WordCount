import ox.*
import ox.flow.*

import java.nio.file.*
import java.util.stream.*

import scala.collection.mutable
import scala.jdk.CollectionConverters.*

@main
def MultiWordCount(): Unit =
  val start = System.currentTimeMillis()

  val path  = Paths.get("data")
  val map   = new mutable.HashMap[String, Int]
  val cores = Runtime.getRuntime().availableProcessors()

  Files
    .list(path)
    .filter: file =>
      Files.isRegularFile(file) && file.getFileName().toString().endsWith(".txt")
    .collect(Collectors.toList())
    .asScala
    .mapPar(cores): file =>
      Flow
        .fromFile(file)
        .linesUtf8
        .mapConcat: line =>
          line
            .split("\\s+")
            .map: word =>
              word.filter(_.isLetterOrDigit).toLowerCase()
            .filter(_.nonEmpty)
        .runFold(new mutable.HashMap[String, Int]): (local, word) =>
          local(word) = local.getOrElse(word, 0) + 1
          local
    .foreach: local =>
      local.foreach: (word, n) =>
        map(word) = map.getOrElse(word, 0) + n

  println(s"Map count: ${map.size}")

  val vec = map.toVector.sortBy(_._2).reverse
  for i <- 0 until 10 do
    val (word, n) = vec(i)
    println(f"${i}%2d| ${word}%10s: ${n}%10d")

  val elapsed = System.currentTimeMillis() - start
  println(s"Time elapsed: ${elapsed} (ms).")
