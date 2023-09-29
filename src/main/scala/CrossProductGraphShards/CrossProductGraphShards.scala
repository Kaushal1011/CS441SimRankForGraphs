package CrossProductGraphShards

import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{Text, NullWritable, LongWritable}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.lib.join.CompositeInputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import org.apache.hadoop.conf.Configuration
import scala.jdk.CollectionConverters.*
import Utilz.CreateLogger

import Helpers.{ComparableNode, NodeDataParser}

// Only a mapper job, out input file contains data in format
// there are two group of nodes in original and perturbed graph
// Shards for original graph (G1) \t Shards for perturbed graph (GG1)
// Orignal has shard G1-20 and perturbed has shard GG1-20
// input has
// group of 5 % nodes from original graph (G1) \t group of 5 % nodes from perturbed graph (GG1)
// group of 5 % nodes from original graph (G1) \t group of 5 % nodes from perturbed graph (GG2)
// group of 5 % nodes from original graph (G2) \t group of 5 % nodes from perturbed graph (GG1)
// group of 5 % nodes from original graph (G2) \t group of 5 % nodes from perturbed graph (GG2)
// ....
class crossMapper extends Mapper[LongWritable, Text, Text, Text] {

  val logger = CreateLogger(classOf[crossMapper])

  logger.info("CrossProductGraphShards Mapper Started")

  /**
   * Mapper function for CrossProductGraphShards
   * @param key - line number
   * @param value - line from input file
   * @param context - context object
   */
  override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, Text]#Context): Unit = {

    val line = value.toString
    val tokens = line.split("\\|")
    val originalNodes = tokens(0).split("\t")
    val perturbedNodes = tokens(1).split("\t")
    logger.info("Original Nodes: " + originalNodes.mkString(","))
    logger.info("Perturbed Nodes: " + perturbedNodes.mkString(","))
    logger.info("Crossproduct will be taken for these nodes, which are part of a 5% shard of original graph and perturbed graph")
    // cross product of original nodes from shard and perturbed nodes from shard
    originalNodes.foreach(originalNode => {
      perturbedNodes.foreach(perturbedNode => {
        context.write(new Text(originalNode), new Text(perturbedNode))
      })
    })
  }
}

object CrossProductGraphShards {

  val logger = CreateLogger(getClass)

  /**
   * Main function for CrossProductGraphShards
   * @param args - input and output paths
   */
  def main(args: Array[String]): Unit = {

    logger.info("CrossProductGraphShards Job Runner Initiated" )

    val conf = new Configuration()

    // set seperator to | for easy splitting of data not tabs and spaces debate
    conf.set("mapreduce.output.textoutputformat.separator", "|")

    logger.debug("Seperator set to |")

    val job = Job.getInstance(conf, "CrossProductGraphShards")
    job.setJarByClass(classOf[crossMapper])
    job.setMapperClass(classOf[crossMapper])

    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[Text])

    // Set your input and output paths here
    FileInputFormat.setInputPaths(job, new Path(args(0)))
    FileOutputFormat.setOutputPath(job, new Path(args(1)))

    if (job.waitForCompletion(true)) {
      logger.info("CrossProductGraphShards Job Completed Successfully")
      System.exit(0)
    } else {
      logger.error("CrossProductGraphShards Job Failed")
      System.exit(1)
    }
  }

}


