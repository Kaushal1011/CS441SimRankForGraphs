package ComputeSimRankJob

import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{LongWritable, NullWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.lib.join.CompositeInputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import org.apache.hadoop.conf.Configuration

import scala.jdk.CollectionConverters.*
import Helpers.{ComparableNode, NodeDataParser}
import org.apache.hadoop.io.Writable
import org.apache.hadoop.io.WritableComparable
import Utilz.CreateLogger

import java.io.DataInput
import java.io.DataOutput
import org.apache.hadoop.io.Writable



object ComputeSimRankJob {

  private val logger = CreateLogger(getClass)

  // Generated using chatgpt and good old fashioned experimenting to generate a custom type for str: sim for later use in the reducer
  private class MyCustomWritable extends Writable {
    // Define fields for your custom data
    var similarity: Double = 0
    var nodestr: String = ""


    // Implement the write method to serialize your data
    override def write(out: DataOutput): Unit = {
      out.writeDouble(similarity)
      out.writeUTF(nodestr)

    }

    // Implement the readFields method to deserialize your data
    override def readFields(in: DataInput): Unit = {
      similarity = in.readDouble()
      nodestr = in.readUTF()
    }

    override def toString(): String = {
      nodestr + "=" + similarity
    }
  }

  private class JaccardMapper extends Mapper[LongWritable, Text, Text, MyCustomWritable] {

    private val logger = CreateLogger(classOf[JaccardMapper])

    override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, MyCustomWritable]#Context): Unit = {
      val node1 = NodeDataParser.parseNodeData(value.toString.split("\\|")(0))
      val node2 = NodeDataParser.parseNodeData(value.toString.split("\\|")(1))


      // Calculate Jaccard similarity
      val similarity = node1.SimRankFromJaccardSimilarity(node2)
      val customWritable = new MyCustomWritable()
      customWritable.similarity = similarity
      customWritable.nodestr = node2.toString.trim

      context.write(
        new Text(node1.toString.trim), customWritable)
    }
  }


  private class JaccardReducer extends Reducer[Text, MyCustomWritable, Text, MyCustomWritable] {

    private val logger = CreateLogger(classOf[JaccardReducer])

    override def reduce(key: Text, values: java.lang.Iterable[MyCustomWritable], context: Reducer[Text, MyCustomWritable, Text, MyCustomWritable]#Context): Unit = {
      val node1 = NodeDataParser.parseNodeData(key.toString)
      val node2s = values.asScala.map(x => {
        val node2 = NodeDataParser.parseNodeData(x.nodestr)
        (node2, x.similarity)
      }).toList.sortBy(_._2).reverse.head
//      val sb = node2s._1.toString + "|" + node2s._2.toString

      val customWritable = new MyCustomWritable()
      customWritable.similarity = node2s._2
      customWritable.nodestr = node2s._1.toString.trim

      context.write(key, customWritable)
    }
  }

  def main(args: Array[String]): Unit = {
    val conf = new Configuration()
    val job = Job.getInstance(conf, "ComputeSimRankJob")

    logger.info("Starting ComputeSimRankJob for Nodes")

    job.setJarByClass(classOf[JaccardMapper])


    job.setMapperClass(classOf[JaccardMapper])
    job.setCombinerClass(classOf[JaccardReducer])
    job.setReducerClass(classOf[JaccardReducer])

    // Set the custom writable class as the output value class for the Mapper
    job.setMapOutputValueClass(classOf[MyCustomWritable])
    job.setMapOutputKeyClass(classOf[Text])

    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[Text])

//    job.setNumReduceTasks(8)

    // Set input and output paths
    FileInputFormat.addInputPath(job, new Path(args(0)))
    FileOutputFormat.setOutputPath(job, new Path(args(1)))

    if (job.waitForCompletion(true)) {
      logger.info("ComputeSimRankJob for Nodes completed successfully")
      System.exit(0)
    } else {
      logger.error("ComputeSimRankJob for Nodes failed")
      System.exit(1)
    }
  }
}



