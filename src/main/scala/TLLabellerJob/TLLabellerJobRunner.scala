package TLLabellerJob

import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{Text, LongWritable}
import org.apache.hadoop.mapreduce.lib.input.{FileInputFormat, MultipleInputs}
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}



object TLLabellerJobRunner {
  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      println("Usage: MultipleMapperJob <input1> <input2> <output>")
      System.exit(1)
    }

    val inputPath1 = new Path(args(0))
    val inputPath2 = new Path(args(1))
    val outputPath = new Path(args(2))

    val conf = new org.apache.hadoop.conf.Configuration
    val job = Job.getInstance(conf, "Multiple Mapper Job")

    job.setJarByClass(TLLabellerJobRunner.getClass)

    // set the combiner mapper class
    job.setCombinerClass(classOf[TLLabellerCombinedReducer])

    // Set the reducer class
    job.setReducerClass(classOf[TLLabellerCombinedReducer])

    // Output key and value classes
    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[Text])

    // Input format for the first mapper
    MultipleInputs.addInputPath(job, inputPath1, classOf[TextInputFormat], classOf[TLLabellerForward])

    // Input format for the second mapper
    MultipleInputs.addInputPath(job, inputPath2, classOf[TextInputFormat], classOf[TLLabellerReverse])

    FileOutputFormat.setOutputPath(job, outputPath)

    if (job.waitForCompletion(true)) {
      System.exit(0)
    } else {
      System.exit(1)
    }
  }

}

