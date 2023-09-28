package NGSYamlParser

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Helpers.NodeDataParser
import Helpers.ComparableNode
import scala.io.Source
import Helpers.NGSYamlParser
import Helpers.NGSYamlParser.ParsedData
class NGSYamlParse extends AnyFlatSpec with Matchers {

  behavior of "NGSYamlParser: Should parse yaml file and return different lists of nodes and edges"

  it should "parse yaml file and return different lists of nodes and edges" in {

    val resourceUrl = getClass.getClassLoader.getResource("check.yaml")

    // Ensure that the resource was found
    if (resourceUrl == null) {
      fail("Resource not found")
    }

    val filePath = resourceUrl.getPath
    val parsedData: ParsedData = NGSYamlParser.parseYaml(filePath)

    parsedData.nodesModified.size shouldBe 3
    parsedData.removedNodes.size shouldBe 6
    parsedData.addedNodes.size shouldBe 8

  }
}
