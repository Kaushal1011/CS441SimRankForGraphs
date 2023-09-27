package TLLabellerJob

import TLLabellerJobOldBiDirection.TLLabellerCombinedReducer
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.{FileInputFormat, MultipleInputs}
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import org.slf4j.{Logger, LoggerFactory}

object TLLabellerJob {

  class TLLabellerForward extends Mapper[LongWritable, Text, Text, Text] {

    private val logger: Logger = LoggerFactory.getLogger(classOf[TLLabellerForward])

    override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, Text]#Context): Unit = {
      val key = value.toString.split("\t")(0)
      val nodesimrank = value.toString.split("\t")(1)
      val simRank = nodesimrank.split("=")(1).toDouble
      val nodestr = nodesimrank.split("=")(0)

      if (key.split("\\*")(1)=="F") {
        if (simRank < 0.1 && simRank >= 0.0) {
          context.write(new Text("removed"), new Text(key))
        }
        else if (simRank > 0.1 && simRank < 1.0) {
          context.write(new Text("modified"), new Text(key))
        }
        else {
          context.write(new Text("matched"), new Text(key))
        }
      }

       if ((key.split("\\*")(1)=="R") && (simRank>=0.0 && simRank <= 0.1)){
        context.write(new Text("added"), new Text(key))
      }
      else{
        //pass
      }

    }
  }

  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      println("Usage: Labbeler Mapper Job <input> <output> <node|edge>")
      System.exit(1)
    }

    val inputPath1 = new Path(args(0))
    val outputPath = new Path(args(1))
    val processingEntity = args(2)

    // create conf based on processing entity type read from text file and give outputs

    val conf = new org.apache.hadoop.conf.Configuration
    val job = Job.getInstance(conf, "Multiple Mapper Job")

    job.setJarByClass(classOf[TLLabellerForward])

    // set the combiner mapper class

    // Output key and value classes
    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[Text])

    // Input format for the only mapper
    MultipleInputs.addInputPath(job, inputPath1, classOf[TextInputFormat], classOf[TLLabellerForward])


    FileOutputFormat.setOutputPath(job, outputPath)

    if (job.waitForCompletion(true)) {
      System.exit(0)
    } else {
      System.exit(1)
    }
  }

}
