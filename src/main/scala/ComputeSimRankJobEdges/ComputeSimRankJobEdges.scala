package ComputeSimRankJobEdges

import ComputeSimRankJob.ComputeSimRankJob.getClass
import Helpers.{ComparableEdge, NodeDataParser}
import Utilz.CreateLogger
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.*
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.join.CompositeInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import org.slf4j.{Logger, LoggerFactory}

import java.io.{DataInput, DataOutput}
import scala.jdk.CollectionConverters.*

// File is similar to ComputeSimRankJob, but it is used to calculate the similarity between edges
// Always will be similar -> Could be merged into one file and handled by a parameter
// For explanation and comments see ComputeSimRankJob
object ComputeSimRankJobEdges {

  //
  private val logger = CreateLogger(getClass)
  private class EdgeSimWritable extends Writable {
    // add vals to bracket in constructor ?

    // var can't be avoided since we are using a custom writable from hadoop api
    // Better explaination: var cannot be avoided because method
    // readFields() is called by hadoop api and it needs to set the values of the variables
    // readFields() is being used by the reducer to read the values from the file that gets written by the mapper
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

    override def toString: String = {
      edgestr + "=" + similarity
    }
  }

  private class JaccardMapper4Edge extends Mapper[LongWritable, Text, Text, EdgeSimWritable] {

    private val logger = CreateLogger(classOf[JaccardMapper4Edge])

    override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, EdgeSimWritable]#Context): Unit = {
      // custom parsers (lets go chatgpt goes brr for regex)
      val edge1: ComparableEdge = NodeDataParser.parseEdgeData(value.toString.split("\\|")(0))
      val edge2: ComparableEdge = NodeDataParser.parseEdgeData(value.toString.split("\\|")(1))


      // Calculate Jaccard similarity
      val similarity = edge1.SimRankJaccardSimilarity(edge2)
      val customWritable = new EdgeSimWritable()
      customWritable.similarity = similarity
      customWritable.edgestr = edge2.toString.trim

      logger.debug("In Mapper:"+"Edge1: " + edge1.toString + " Edge2: " + edge2.toString + " Similarity: " + similarity)

      val reverseSimilarity = edge2.SimRankJaccardSimilarity(edge1)
      val customWritable2 = new EdgeSimWritable()

      customWritable2.similarity = reverseSimilarity
      customWritable2.edgestr = edge1.toString.trim

      context.write(
        new Text(edge1.toString.trim + "*" + "F"), customWritable)

      context.write(
        new Text(edge2.toString.trim + "*" + "R"), customWritable2)

    }
  }

  private class JaccardReducer4Edge extends Reducer[Text, EdgeSimWritable, Text, EdgeSimWritable] {

    private val logger = CreateLogger(classOf[JaccardReducer4Edge])

    override def reduce(key: Text, values: java.lang.Iterable[EdgeSimWritable], context: Reducer[Text, EdgeSimWritable, Text, EdgeSimWritable]#Context): Unit = {

      val topEdge = values.asScala.map( x => {
        val edge2 = NodeDataParser.parseEdgeData(x.edgestr)
        (edge2, x.similarity)
      }).toList.sortBy(_._2).reverse.head

      val customWritable = new EdgeSimWritable()
      customWritable.similarity = topEdge._2
      customWritable.edgestr = topEdge._1.toString.trim

      logger.debug("In Reducer:"+"Edge1: " + key.toString + " Edge2: " + topEdge._1.toString + " Similarity: " + topEdge._2)


      context.write(key,customWritable)

    }
  }

  def main(args: Array[String]): Unit = {

    logger.info("Starting ComputeSimRankJobEdges")

    if (args.length != 2) {
      logger.error("Usage: ComputeSimRankJobEdges <input path> <output path>")
      System.exit(-1)
    }

    val conf = new Configuration()
    val job = Job.getInstance(conf, "ComputeSimRankJob")

    job.setJarByClass(classOf[JaccardMapper4Edge])

    // merging sort pattern
    job.setMapperClass(classOf[JaccardMapper4Edge])
    job.setCombinerClass(classOf[JaccardReducer4Edge])
    job.setReducerClass(classOf[JaccardReducer4Edge])


    job.setMapOutputValueClass(classOf[EdgeSimWritable])
    job.setMapOutputKeyClass(classOf[Text])

    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[Text])


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
