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

import Utilz.*

private class TLLabeller extends Mapper[LongWritable, Text, Text, Text] {

  private val logger: Logger = Utilz.CreateLogger(classOf[TLLabeller])

  /**
   * Mapper for TLLabeller
   * @param key - key
   * @param value - value
   * @param context - context
   */
  override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, Text]#Context): Unit = {

    logger.info("TLLabeller Mapper")

    // split by tabs, tabs ftw ?
    val key = value.toString.split("\t")(0)
    val nodesimrank = value.toString.split("\t")(1)

    val simRank = nodesimrank.split("=")(1).toDouble
    val nodestr = nodesimrank.split("=")(0)

    val typeOfProcessing = context.getConfiguration.get("typeOfProcessing")

    val prefix = if (typeOfProcessing == "node") "node" else "edge"

    val modifiedThresholdUp = context.getConfiguration.get(s"${prefix}ModificationThresholdUp").toDouble
    val modifiedThresholdDown = context.getConfiguration.get(s"${prefix}ModificationThresholdDown").toDouble
    val removedThreshold = context.getConfiguration.get(s"${prefix}RemovedThreshold").toDouble
    val addedThreshold = context.getConfiguration.get(s"${prefix}AddedThreshold").toDouble


    // process forward relationships
    // Match is when simrank is high
    // modified is when simrank is moderate
    // removed is when simrank is low
    if (key.split("\\*")(1) == "F") {
      if (simRank < removedThreshold && simRank >= 0.0) {
        logger.info("Mapper Wrote " + key + "removed")
        context.write(new Text("removed"), new Text(key))
      }
      else if (simRank > modifiedThresholdDown && simRank < modifiedThresholdUp) {
        logger.info("Mapper Wrote " + key + "modified")
        context.write(new Text("modified"), new Text(key))
      }
      else {
        logger.info("Mapper Wrote " + key + "matched")
        context.write(new Text("matched"), new Text(key))
      }
    }

    // process reverse relationships to find added relationships (node/edge in perturbed is added if it has
    // no TL in original)
    if ((key.split("\\*")(1) == "R") && (simRank >= 0.0 && simRank <= addedThreshold)){
      logger.info("Mapper Wrote " + key + "added")
      context.write(new Text("added"), new Text(key))
    }
    else {
      //pass
    }

  }
}
