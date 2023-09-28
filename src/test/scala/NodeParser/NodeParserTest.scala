package NodeParser

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Helpers.NodeDataParser
import Helpers.ComparableNode
import scala.io.Source
class NodeParserTest extends AnyFlatSpec with Matchers {

  given repeatable: Boolean= true
  behavior of "NodeParser for Nodes"

  it should "be able to parse string rep and make a comparable node class object" in {
    val stringToParse = "(0, 0, 2, List(-1349935424, 1077067315, 110373521, 2109098941), List())"
    val parsedNode = NodeDataParser.parseNodeData(stringToParse)
    parsedNode.toString shouldBe ComparableNode(0,0,2,List(-1349935424, 1077067315, 110373521, 2109098941),List()).toString

  }

  it should "be able to parse in format string rep|string rep which is the output of mapper job" in {

    val resourcePath= "firstjob.txt"
    try{
      val source = Source.fromResource(resourcePath)

      // Get all lines from the file as a List
      val lines = source.getLines.toList

      // Close the source to release resources
      source.close()

      for ((line, lineNumber) <- lines.zipWithIndex) {
        try {
          // Process the 'line' here
          val nodes = line.split("\\|")
          if (nodes.length == 2) {
            val node1 = NodeDataParser.parseNodeData(nodes(0))
            val node2 = NodeDataParser.parseNodeData(nodes(1))

          } else {
            // Handle cases where there are not exactly two nodes in a line
            fail(s"Error processing line $lineNumber: Invalid number of nodes")
          }
        } catch {
          case e: Exception =>
            // Handle exceptions for individual lines here
            fail(s"Error processing line $lineNumber: ${e.getMessage}")

        }
      }

    }
    catch
    {
      case e: Exception =>
        // Handle exceptions related to file access here
        fail(s"Error reading the file: ${e.getMessage}")
    }


  }

}
