package Helpers

import scala.io.Source
import Helpers.ComparableNode

// This is a parser for the output of the graph
// it used to deserialize the string representation of the node that the preprocessing script outputs
// see tests
object NodeDataParser {

  // A good use of ChatGPT
  // All the regex patterns defined here
  // were generated using ChatGPT and some good prompt engineering  :)
  def parseNodeData(input: String): ComparableNode = {

    // pattern is id int, incoming int, outgoing int, childrenhash list int, props list int
    val pattern = "\\((-?\\d+), (-?\\d+), (-?\\d+), List\\(([^)]*)\\), List\\(([^)]*)\\)\\)\\s*".r

    input match {
      case pattern(id, incoming, outgoing, childrenHash, properties) =>
        val idInt = id.toInt
        val incomingInt = incoming.toInt
        val outgoingInt = outgoing.toInt
        val childrenHashList = parseList(childrenHash)
        val propertiesList = parseList(properties)

        new ComparableNode(idInt, incomingInt, outgoingInt, childrenHashList, propertiesList)
      case _ =>
        throw new IllegalArgumentException("Invalid input format")
    }
  }

  // more chatgpt for edge parsing
  // fix a format
  // let the ai pair programmer do the regex for you
  // parse using brackets, number, commas and List identifier (the human should understand what the ai said and fix it)
  def parseEdgeData(input: String): ComparableEdge = {
    // Updated pattern to handle empty and non-empty lists
    val pattern = "\\((-?\\d+), (-?\\d+), (-?\\d+(?:\\.\\d+)?(?:[Ee][-+]?\\d+)?), List\\(([^)]*)\\), List\\(([^)]*)\\), List\\(([^)]*)\\), List\\(([^)]*)\\)\\)\\s*".r

    input match {
      case pattern(srcId, dstId, cost, propssrc, propsdst, childrenhashsrc, childrenhashdst) =>
        val srcIdInt = srcId.toInt
        val dstIdInt = dstId.toInt
        val costDouble = cost.toDouble
        val propssrcList = parseList(propssrc)
        val propsdstList = parseList(propsdst)
        val childrenhashsrcList = parseList(childrenhashsrc)
        val childrenhashdstList = parseList(childrenhashdst)

        new ComparableEdge(srcIdInt, dstIdInt, costDouble, propssrcList, propsdstList, childrenhashsrcList, childrenhashdstList)
      case _ =>
        throw new IllegalArgumentException("Invalid input format > Supplied Input:  " + input)
    }
  }


  private def parseList(listStr: String): List[Int] = {
    if (listStr.isEmpty || listStr == "List()") {
      List.empty
    } else {
      val elements = listStr.stripPrefix("List(").stripSuffix(")").split(",").map(_.trim.toInt)
      elements.toList
    }
  }


}