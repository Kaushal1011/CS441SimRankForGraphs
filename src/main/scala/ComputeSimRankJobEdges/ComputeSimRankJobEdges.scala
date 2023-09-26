package ComputeSimRankJobEdges

import ComputeSimRankJob.ComputeSimRankJob.getClass
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{LongWritable, NullWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.lib.join.CompositeInputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import org.apache.hadoop.conf.Configuration
import Utilz.CreateLogger

import scala.jdk.CollectionConverters.*
import Helpers.{ComparableEdge, NodeDataParser}
import org.apache.hadoop.io.Writable
import org.apache.hadoop.io.WritableComparable
import org.slf4j.{Logger, LoggerFactory}

import java.io.DataInput
import java.io.DataOutput
import org.apache.hadoop.io.Writable
object ComputeSimRankJobEdges {

  private val logger = CreateLogger(getClass)
  class MyCustomWritable4Edges extends Writable {
    // Define fields for your custom data
    var similarity: Double = 0
    var edgestr: String = ""


    // Implement the write method to serialize your data
    override def write(out: DataOutput): Unit = {
      out.writeDouble(similarity)
      out.writeUTF(edgestr)

    }

    // Implement the readFields method to deserialize your data
    override def readFields(in: DataInput): Unit = {
      similarity = in.readDouble()
      edgestr = in.readUTF()
    }

    override def toString(): String = {
      edgestr + "=" + similarity
    }
  }

  private class JaccardMapper4Edge extends Mapper[LongWritable, Text, Text, MyCustomWritable4Edges] {

    private val logger = CreateLogger(classOf[JaccardMapper4Edge])

    override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, MyCustomWritable4Edges]#Context): Unit = {
      val edge1 = NodeDataParser.parseEdgeData(value.toString.split("\\|")(0))
      val edge2 = NodeDataParser.parseEdgeData(value.toString.split("\\|")(1))


      // Calculate Jaccard similarity
      val similarity = edge1.SimRankJaccardSimilarity(edge2)
      val customWritable = new MyCustomWritable4Edges()
      customWritable.similarity = similarity
      customWritable.edgestr = edge2.toString.trim

      context.write(
        new Text(edge1.toString.trim), customWritable)
    }
  }

  private class JaccardReducer4Edge extends Reducer[Text, MyCustomWritable4Edges, Text, MyCustomWritable4Edges] {

    private val logger = CreateLogger(classOf[JaccardReducer4Edge])

    override def reduce(key: Text, values: java.lang.Iterable[MyCustomWritable4Edges], context: Reducer[Text, MyCustomWritable4Edges, Text, MyCustomWritable4Edges]#Context): Unit = {

      val topEdge = values.asScala.map( x => {
        val edge2 = NodeDataParser.parseEdgeData(x.edgestr)
        (edge2, x.similarity)
      }).toList.sortBy(_._2).reverse.head

      val customWritable = new MyCustomWritable4Edges()
      customWritable.similarity = topEdge._2
      customWritable.edgestr = topEdge._1.toString.trim

      context.write(key,customWritable)

    }
  }

  def main(args: Array[String]): Unit = {

    logger.info("Starting ComputeSimRankJobEdges")

    val conf = new Configuration()
    val job = Job.getInstance(conf, "ComputeSimRankJob")

    job.setJarByClass(classOf[JaccardMapper4Edge])


    job.setMapperClass(classOf[JaccardMapper4Edge])
    job.setCombinerClass(classOf[JaccardReducer4Edge])
    job.setReducerClass(classOf[JaccardReducer4Edge])

    // Set the custom writable class as the output value class for the Mapper
    job.setMapOutputValueClass(classOf[MyCustomWritable4Edges])
    job.setMapOutputKeyClass(classOf[Text])

    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[Text])

    //    job.setNumReduceTasks(8)

    // Set input and output paths
    FileInputFormat.addInputPath(job, new Path(args(0)))
    FileOutputFormat.setOutputPath(job, new Path(args(1)))

    if (job.waitForCompletion(true)) {
      logger.info("ComputeSimRankJobEdges finished successfully")
      System.exit(0)
    } else {
      logger.error("ComputeSimRankJobEdges failed")
      System.exit(1)
    }
  }


  
  
}
