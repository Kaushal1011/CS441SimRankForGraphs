package EdgeSimRank

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Helpers.NodeDataParser
import Helpers.ComparableEdge
import scala.io.Source
import Helpers.{NodeDataParser, ComparableEdge}
class EdgeSimRankTest extends AnyFlatSpec with Matchers {

  given repeatable: Boolean = true
  behavior of "EdgeSimRank"

  it should "compute sim rank of comparable edges" in {
    val edge1 = ComparableEdge(0, 13, 0.5666946724261208, List(), List(79, 37, 59, 14, 99, 98, 55, 50), List(-1349935424, 1077067315, 110373521, 2109098941), List(1389696985, 977685876, 944514158, 888249067, -1883113608, -667623957))
    val edge2 = ComparableEdge(0, 13, 0.5666946724261208, List(), List(79, 37, 59, 14, 99, 98, 55, 50), List(-1349935424, 1077067315, 110373521, 2109098943), List(1389696985, 977685876, 944514158, 888249067, -1883113608, -667623958))
    val simrank = edge1.SimRankJaccardSimilarity(edge2)

    simrank shouldBe <= (1.0)

    simrank shouldBe >= (0.75)

  }

  it should "compute simrank when only 1 prop is present" in {

    val edge1 = ComparableEdge(0, 13, 0.5666946724261208, List(), List(79, 37, 59, 14, 99, 98, 55, 50), List(), List())
    val edge2 = ComparableEdge(0, 13, 0.5666946724261208, List(), List(79, 37, 59, 14, 99, 98, 55, 50), List(), List())
    val simrank = edge1.SimRankJaccardSimilarity(edge2)

    simrank shouldEqual(1.0)

  }


}
