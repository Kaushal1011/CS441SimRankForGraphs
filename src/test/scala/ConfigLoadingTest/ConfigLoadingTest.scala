package ConfigLoadingTest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.typesafe.config.{Config, ConfigFactory}
import Utilz.*


class ConfigLoadingTest extends AnyFlatSpec with Matchers {



  given repeatable: Boolean = true
  behavior of "Config Loading"

  it should "load config from file" in {

    val config: Config = ConfigFactory.load("application-test.conf")

    val nodeModificationThresholdUp = ConfigReader.getConfigEntry[Double](config, "SRFG.NodeMatcher.nodeModificationThresholdUp", 0.0)
    val nodeRemovedThreshold = ConfigReader.getConfigEntry[Double](config, "SRFG.NodeMatcher.nodeRemovedThreshold", 0.0)


    val edgeModificationThresholdUp = ConfigReader.getConfigEntry[Double](config, "SRFG.EdgeMatcher.edgeModificationThresholdUp", 0.0)
    val edgeRemovedThreshold = ConfigReader.getConfigEntry[Double](config, "SRFG.EdgeMatcher.edgeRemovedThreshold", 0.0)

    println(s"nodeModificationThresholdUp: $nodeModificationThresholdUp")
    println(s"nodeRemovedThreshold: $nodeRemovedThreshold")
    println(s"edgeModificationThresholdUp: $edgeModificationThresholdUp")
    println(s"edgeRemovedThreshold: $edgeRemovedThreshold")

    nodeModificationThresholdUp should be(0.9)
    nodeRemovedThreshold should be(0.10)


    edgeModificationThresholdUp should be(0.75)
    edgeRemovedThreshold should be(0.10)

  }
}
