package Helpers

import org.yaml.snakeyaml.Yaml
import java.io.{File, FileInputStream}
import scala.jdk.CollectionConverters.*
import scala.util.Using


// Yet another chatgpt generated file that requied a few hours of debugging to get it to work D:
object NGSYamlParser {
  case class ParsedData(
                         addedNodes: List[Int] = Nil,
                         nodesModified: List[Int] = Nil,
                         removedNodes: List[Int] = Nil,
                         modifiedEdges: List[(Int, Int)] = Nil,
                         addedEdges: List[(Int, Int)] = Nil,
                         removedEdges: List[(Int, Int)] = Nil
                       )

  def parseInputFile(filePath: String): ParsedData = {
    // replace all tabs with spaces in the input file
    val fileContents = Using(scala.io.Source.fromFile(filePath)){
      source => source.getLines().mkString("\n").replaceAll("\t", "  ")
    }
    // write the file back to the same location
    Using(new java.io.PrintWriter(filePath)) {
      writer => writer.write(fileContents.get)
    }

    val yaml = new Yaml()
    val inputStream = new FileInputStream(new File(filePath))

    val yamlObject = yaml.load(inputStream).asInstanceOf[java.util.Map[String, Any]]

    val nodes = yamlObject.get("Nodes").asInstanceOf[java.util.Map[String, Any]]
    val addedNodes = nodes.get("Added").asInstanceOf[java.util.Map[Int, Int]].values().asScala.toList
    val nodesModified = nodes.get("Modified").asInstanceOf[java.util.List[Int]].asScala.toList
    val nodesRemoved = nodes.get("Removed").asInstanceOf[java.util.List[Int]].asScala.toList

    val edges = yamlObject.get("Edges").asInstanceOf[java.util.Map[String, Any]]
    val modifiedEdges = edges.get("Modified").asInstanceOf[java.util.Map[Int, Int]].asScala.toList.map {
      case (source, target) => {
        if source < target then (source, target) else (target, source)
      }
    }
    val addedEdges = edges.get("Added").asInstanceOf[java.util.Map[Int, Int]].asScala.toList.map {
      case (source, target) => {
        if source < target then (source, target) else (target, source)
      }
    }

    val removedEdges = edges.get("Removed").asInstanceOf[java.util.Map[Int, Int]].asScala.toList.map {
      case (source, target) => {
        if source < target then (source, target) else (target, source)
      }
    }


    ParsedData(addedNodes, nodesModified, nodesRemoved,modifiedEdges,addedEdges, removedEdges)
  }

  def parseEdge(edge: String): (Int, Int) = {
    val Array(source, target) = edge.split(":").map(_.trim.toInt)
    (source, target)
  }

  def main(args: Array[String]): Unit = {
    val filePath = "./check.yaml" // Replace with the actual file path


    val parsedData = parseInputFile(filePath)

    println("Added Nodes: " + parsedData.addedNodes)
    println("Nodes Modified: " + parsedData.nodesModified)
    println("Nodes Removed: " + parsedData.removedNodes)
    println("Modified Edges: " + parsedData.modifiedEdges)
    println("Added Edges: " + parsedData.addedEdges)
    println("Removed Edges: " + parsedData.removedEdges)
  }
}
