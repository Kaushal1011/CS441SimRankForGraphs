package NodeParser

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Helpers.NodeDataParser
import Helpers.ComparableEdge
import scala.io.Source
class EdgeParserTest extends AnyFlatSpec with Matchers {

  given repeatable: Boolean = true
  behavior of "NodeParser for Nodes"

  it should "parse an edge stringrep into a comparable edge" in {
    val edgeString = "(0, 13, 0.5666946724261208, List(), List(79, 37, 59, 14, 99, 98, 55, 50), List(-1349935424, 1077067315, 110373521, 2109098941), List(1389696985, 977685876, 944514158, 888249067, -1883113608, -667623957))"
    val edge = NodeDataParser.parseEdgeData(edgeString)
    val expectedEdge = ComparableEdge(0, 13, 0.5666946724261208, List(), List(79, 37, 59, 14, 99, 98, 55, 50), List(-1349935424, 1077067315, 110373521, 2109098941), List(1389696985, 977685876, 944514158, 888249067, -1883113608, -667623957))

    edge.toString shouldBe expectedEdge.toString

  }

  it should "be able to parse in format string rep|string rep which is the output of the mapper job" in {
    val resourcePath= "secondjob.txt"
    try {
      // Use Source.fromFile to open the file and get an iterator over its lines
      val source = Source.fromResource(resourcePath)

      // Get all lines from the file as a List
      val lines = source.getLines.toList

      // Close the source to release resources
      source.close()

      for ((line, lineNumber) <- lines.zipWithIndex) {
        try {
          // Process the 'line' here
          val edges = line.split("\\|")
          //          print(edges.length)
          if (edges.length == 2) {
            val edge1 = NodeDataParser.parseEdgeData(edges(0).trim)
            val edge2 = NodeDataParser.parseEdgeData(edges(1).trim)

          } else {
            // Handle cases where there are not exactly two nodes in a line
            fail(s"Error processing line $lineNumber: Invalid number of nodes")
//            println(edges(0))
          }
        } catch {
          case e: Exception =>
            // Handle exceptions for individual lines here
            fail(s"Error processing line $lineNumber: ${e.getMessage}")
//            println(line)
        }

      }

    } catch {
      case e: Exception =>
        // Handle exceptions related to file access here
        fail(s"Error reading the file: ${e.getMessage}")
    }
  }

}
