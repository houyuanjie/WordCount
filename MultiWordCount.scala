import ox.*
import ox.flow.*

import java.nio.file.*
import java.util.stream.*

import scala.collection.mutable
import scala.jdk.CollectionConverters.*

@main
def MultiWordCount(): Unit =
  val start = System.currentTimeMillis()

  val cores = Runtime.getRuntime().availableProcessors()

  val path = Paths.get("data")
  val files = Files
    .list(path)
    .filter: file =>
      Files.isRegularFile(file) && file.getFileName().toString().endsWith(".txt")
    .toList()
    .asScala

  val map = files
    .mapPar(cores): file =>
      Flow
        .fromFile(file)
        .decodeStringUtf8
        .mapConcat(_.split("\\s+"))
        .map(_.filter(_.isLetterOrDigit).toLowerCase())
        .filter(_.nonEmpty)
        .runFold(new mutable.HashMap[String, Int]): (localMap, word) =>
          localMap(word) = localMap.getOrElse(word, 0) + 1
          localMap
    .fold(new mutable.HashMap[String, Int]): (sumMap, localMap) =>
      localMap.foreach: (word, n) =>
        sumMap(word) = sumMap.getOrElse(word, 0) + n
      sumMap

  println(s"Map count: ${map.size}")

  val vec = map.toVector.sortBy(_._2).reverse
  for i <- 0 until 10 do
    val (word, n) = vec(i)
    println(f"${i}%2d| ${word}%10s: ${n}%10d")

  val elapsed = System.currentTimeMillis() - start
  println(s"Time elapsed: ${elapsed} (ms).")
