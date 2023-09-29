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


/**
 * This job computes the similarity between all nodes in the graph
 * It uses the Jaccard Similarity metric to compute similarity
 * It uses a custom writable to store the similarity and the node string
 * It uses a cyclic reducer to act as a combiner and reducer
 * It uses a custom parser to parse the node data
 * It uses a custom comparable node to compare nodes
 */
object ComputeSimRankJob {

  private val logger = CreateLogger(getClass)

  // Generated using chatgpt and good old fashioned experimenting to generate a custom type for str: sim for later use in the reducer

  /**
   * Custom Writable to store similarity and node string
   * */
  private class NodeSimWritable extends Writable {
    // var can't be avoided since we are using a custom writable from hadoop api
    var similarity: Double = 0
    var nodestr: String = ""


    // writer for writing to file

    /**
     * Write to file
     * @param out DataOutput
     */
    override def write(out: DataOutput): Unit = {
      out.writeDouble(similarity)
      out.writeUTF(nodestr)

    }

    // reader when reading from file
    override def readFields(in: DataInput): Unit = {
      similarity = in.readDouble()
      nodestr = in.readUTF()
    }

    override def toString: String = {
      nodestr + "=" + similarity
    }
  }

  // Mapper that finds similarity of original node(i) - perturbed node(j) for all i and j
  // input file contains crossproduct of set of all nodes in the graph

  /**
   * Mapper that finds similarity of original node(i) - perturbed node(j) for all i and j
   * input file contains crossproduct of set of all nodes in the graph
   */
  private class JaccardMapper extends Mapper[LongWritable, Text, Text, NodeSimWritable] {

    private val logger = CreateLogger(classOf[JaccardMapper])

    /**
     * Map function that finds similarity of original node(i) - perturbed node(j) for all i and j
     * @param key LongWritable
     * @param value Text
     * @param context Mapper[LongWritable, Text, Text, NodeSimWritable]#Context
     */
    override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, NodeSimWritable]#Context): Unit = {
      // parse data using custom parser
      val node1: ComparableNode = NodeDataParser.parseNodeData(value.toString.split("\\|")(0))
      // parse data
      val node2 = NodeDataParser.parseNodeData(value.toString.split("\\|")(1))


      // Calculate jaccard similarity of properties selected for comparison
      val similarity = node1.SimRankFromJaccardSimilarity(node2)
      val customWritable = new NodeSimWritable()
      customWritable.similarity = similarity
      customWritable.nodestr = node2.toString.trim

      logger.debug("In Mapper:"+"Node1: " + node1.toString + " Node2: " + node2.toString + " Similarity: " + similarity)

      // above was forward comparison i.e keys are nodes in graph 1 and values are nodes in graph 2
      // below is reverse comparison i.e keys are nodes in graph 2 and values are nodes in graph 1

      val reverseSimilarity: Double = node2.SimRankFromJaccardSimilarity(node1)
      val customWritable2 = new NodeSimWritable()

      customWritable2.similarity = reverseSimilarity
      customWritable2.nodestr = node1.toString.trim

      // compute similarity in forward direction and flag for later use in other jobs
      context.write(
        new Text(node1.toString.trim + "*" + "F"), customWritable)

      // compute similarity in reverse direction and store in negative amplitude to distinguish from forward direction
      context.write(
        new Text(node2.toString.trim + "*" + "R"), customWritable2)

    }

  }


  // Cyclic Reducer such that it can act as a combiner and reducer
  // Reducer finds the maximum in all the values for a given key
  // Using this as reducer and combiner the pipeline can be seen as analogous to merge sort
  private class JaccardReducer extends Reducer[Text, NodeSimWritable, Text, NodeSimWritable] {

    private val logger = CreateLogger(classOf[JaccardReducer])

    /**
     * Reduce function that finds the maximum in all the values for a given key
     * @param key
     * @param values
     * @param context
     */
    override def reduce(key: Text, values: java.lang.Iterable[NodeSimWritable], context: Reducer[Text, NodeSimWritable, Text, NodeSimWritable]#Context): Unit = {

      // sort all values find value with highest similarity
      val node2s: (ComparableNode, Double) = values.asScala.map(x => {
        val node2 = NodeDataParser.parseNodeData(x.nodestr)
        (node2, x.similarity)
      }).toList.sortBy(_._2).reverse.head


      val customWritable = new NodeSimWritable()
      customWritable.similarity = node2s._2
      customWritable.nodestr = node2s._1.toString.trim

      logger.debug("In Reducer:"+"Node1: " + key.toString + " Node2: " + node2s._1.toString + " Similarity: " + node2s._2)

      context.write(key, customWritable)
    }
  }

  /**
   * Main function
   * @param args Array[String]
   */
  def main(args: Array[String]): Unit = {
    val conf = new Configuration()
    val job = Job.getInstance(conf, "ComputeSimRankJob")

    if (args.length != 2) {
      logger.error("Usage: ComputeSimRankJob <input path> <output path>")
      System.exit(-1)
    }

    logger.info("Starting ComputeSimRankJob for Nodes")

    job.setJarByClass(classOf[JaccardMapper])

    // Set the Mapper, Combiner and Reducer classes
    job.setMapperClass(classOf[JaccardMapper])
    // Throws key value pairs with similarity
    job.setCombinerClass(classOf[JaccardReducer])
    // combines values from different mappers for keys sorts them, merging while sorting
    job.setReducerClass(classOf[JaccardReducer])
    //  last sort mostly redundant but just in case, dont want cry hadoop tears

    job.setMapOutputValueClass(classOf[NodeSimWritable])
    job.setMapOutputKeyClass(classOf[Text])

    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[Text])


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



