package TLLabellerJob

import java.net.URI
import java.io.FileInputStream
import TLLabellerJobOldBiDirection.TLLabellerCombinedReducer
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{IntWritable, LongWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.{FileInputFormat, MultipleInputs}
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat

import scala.jdk.CollectionConverters.*
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import org.slf4j.{Logger, LoggerFactory}
import Helpers.{NGSYamlParser, NodeDataParser}
import Helpers.NGSYamlParser.ParsedData

import com.typesafe.config.{Config, ConfigFactory}

import Utilz.*

// Labels node/edge as added, removed, modified, matched based on threshold values loaded from config file
// reducer outputs CTL, DTL, WTL, ATL values for each type of processing
object TLLabellerJob {

  def main(args: Array[String]): Unit = {
    if (args.length != 4) {
      println("Usage: Labbeler Mapper Job <input> <output> <yamlPath> <node|edge>")
      System.exit(1)
    }

    val config = ConfigFactory.load("application.conf")

    val inputPath1 = new Path(args(0))
    val outputPath = new Path(args(1))

    val yamlPath = args(2)
    val typeOfProcessing = args(3)


    // create conf based on processing entity type read from text file and give outputs

    val appconfig: Config = ConfigFactory.load("application.conf")

    val nodeModificationThresholdUp = ConfigReader.getConfigEntry[Double](appconfig, "SRFG.NodeMatcher.nodeModificationThresholdUp", 0.0)
    val nodeModificationThresholdDown = ConfigReader.getConfigEntry[Double](appconfig, "SRFG.NodeMatcher.nodeModificationThresholdDown", 0.0)
    val nodeRemovedThreshold = ConfigReader.getConfigEntry[Double](appconfig, "SRFG.NodeMatcher.nodeRemovedThreshold", 0.0)
    val nodeAddedThreshold = ConfigReader.getConfigEntry[Double](appconfig, "SRFG.NodeMatcher.nodeAddedThreshold", 0.0)


    val edgeModificationThresholdUp = ConfigReader.getConfigEntry[Double](appconfig, "SRFG.EdgeMatcher.edgeModificationThresholdUp", 0.0)
    val edgeModificationThresholdDown = ConfigReader.getConfigEntry[Double](appconfig, "SRFG.EdgeMatcher.edgeModificationThresholdDown", 0.0)
    val edgeRemovedThreshold = ConfigReader.getConfigEntry[Double](appconfig, "SRFG.EdgeMatcher.edgeRemovedThreshold", 0.0)
    val edgeAddedThreshold = ConfigReader.getConfigEntry[Double](appconfig, "SRFG.EdgeMatcher.edgeAddedThreshold", 0.0)


    val conf = new org.apache.hadoop.conf.Configuration

    conf.set("typeOfProcessing", typeOfProcessing)

    conf.set("nodeModificationThresholdUp", nodeModificationThresholdUp.toString)
    conf.set("nodeModificationThresholdDown", nodeModificationThresholdDown.toString)
    conf.set("nodeRemovedThreshold", nodeRemovedThreshold.toString)
    conf.set("nodeAddedThreshold", nodeAddedThreshold.toString)

    conf.set("edgeModificationThresholdUp", edgeModificationThresholdUp.toString)
    conf.set("edgeModificationThresholdDown", edgeModificationThresholdDown.toString)
    conf.set("edgeRemovedThreshold", edgeRemovedThreshold.toString)
    conf.set("edgeAddedThreshold", edgeAddedThreshold.toString)


    val job = Job.getInstance(conf, "Multiple Mapper Job")
    job.addCacheFile(new URI(yamlPath))

    job.setJarByClass(classOf[TLLabeller])
    job.setReducerClass(classOf[TLChecker])

    // set the combiner mapper class
    // Set the custom writable class as the output value class for the Mapper
    job.setMapOutputValueClass(classOf[Text])
    job.setMapOutputKeyClass(classOf[Text])

    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[Text])

    // Output key and value classes
    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[IntWritable])

    // Input format for the only mapper
    MultipleInputs.addInputPath(job, inputPath1, classOf[TextInputFormat], classOf[TLLabeller])


    FileOutputFormat.setOutputPath(job, outputPath)

    if (job.waitForCompletion(true)) {
      System.exit(0)
    } else {
      System.exit(1)
    }
  }

}
