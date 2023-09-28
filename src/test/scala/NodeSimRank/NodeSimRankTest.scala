package NodeSimRank

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Helpers.NodeDataParser
import Helpers.ComparableEdge
import scala.io.Source
import Helpers.{NodeDataParser, ComparableNode}
class NodeSimRankTest extends AnyFlatSpec with Matchers {

  given repeatable: Boolean = true
  behavior of "NodeSimRank"

  it should "return the correct simrank for a given node" in {

    val node1 = ComparableNode(0,0,2,List(-1349935424, 1077067315, 110373521, 2109098941),List())
    val node2 = ComparableNode(0,1,2,List(-1349935424, 1077067315, 110373521, 2109098941),List())

    val simRank = node1.SimRankFromJaccardSimilarity(node2)

    simRank shouldBe 1.0
  }

  it should "return the correct simrank for a given node less than 1" in {

    val node1 = ComparableNode(0, 0, 2, List(-1349935424, 1077067315, 110353521, 2109098941), List())
    val node2 = ComparableNode(0, 1, 2, List(-1349935424, 1077067315, 110373521, 2109098941), List())

    val simRank = node1.SimRankFromJaccardSimilarity(node2)

    simRank shouldBe < (1.0)
  }

}
