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

// This is an old file that I was using to compute reverse cross jobs
// I figured out a better way to do this and not need an extra job for it
// but keeping this here for reference
class crossMapperReverse extends Mapper[LongWritable, Text, Text, Text] {

  val logger = CreateLogger(classOf[crossMapperReverse])
  override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, Text]#Context): Unit = {

    logger.info("crossMapperReverse Started")

    val line = value.toString
    val tokens = line.split("\\|")
    val originalNodes = tokens(0).split("\t")
    val perturbedNodes = tokens(1).split("\t")
    // cross product of original and perturbed nodes

    logger.info("crossMapperReverse product cross product B->A")

    perturbedNodes.foreach(perturbedNode => {
      originalNodes.foreach(originalNode=> {
        context.write(new Text(perturbedNode), new Text(originalNode))
      })
    })
  }
}

object CrossProductGraphShardsReverse {
  val logger = CreateLogger(getClass)
  def main(args: Array[String]): Unit = {

    logger.info("CrossProductGraphShardsReverse Started")

    val conf = new Configuration()
    conf.set("mapreduce.output.textoutputformat.separator", "|")

    logger.debug("CrossProductReverse seperator set to |")


    val job = Job.getInstance(conf, "CrossProductGraphShards")
    job.setJarByClass(classOf[crossMapperReverse])
    job.setMapperClass(classOf[crossMapperReverse])

    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[Text])

    // Set your input and output paths here
    FileInputFormat.setInputPaths(job, new Path(args(0)))
    FileOutputFormat.setOutputPath(job, new Path(args(1)))

    if (job.waitForCompletion(true)) {
      logger.info("CrossProductGraphShardsReverse Completed")
      System.exit(0)
    } else {
      logger.error("CrossProductGraphShardsReverse Failed")
      System.exit(1)
    }
  }
}
