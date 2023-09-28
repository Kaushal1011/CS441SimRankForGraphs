package SumJobForStats

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.{FileInputFormat, MultipleInputs, TextInputFormat}
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import scala.jdk.CollectionConverters.*

// File same as the TLAggregatorJob, but feeds output from two different mappers into the reducer
// combines TL counts for nodes and edges :)
object SumJobForStatsCombineJob {

  private class BiTLMapper extends Mapper[Object, Text, Text, IntWritable] {

    override def map(key: Object, value: Text, context: Mapper[Object, Text, Text, IntWritable]#Context): Unit = {
      val splits = value.toString.split("\t")
      val tlType = splits(0)
      val count = splits(1).toInt

      context.write(new Text(tlType), new IntWritable(count))

      if (Set("ATL", "DTL").contains(tlType)) {
        context.write(new Text("GTL"), new IntWritable(count))
        context.write(new Text("RTL"), new IntWritable(count))
      } else if (Set("CTL", "WTL").contains(tlType)) {
        context.write(new Text("BTL"), new IntWritable(count))
        context.write(new Text("RTL"), new IntWritable(count))
      }
    }
  }

  private class BiTLReducer extends Reducer[Text, IntWritable, Text, IntWritable] {
    override def reduce(key: Text, values: java.lang.Iterable[IntWritable], context: Reducer[Text, IntWritable, Text, IntWritable]#Context): Unit = {
      val sum = values.asScala.foldLeft(0)(_ + _.get())
      context.write(key, new IntWritable(sum))
    }
  }

  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      println("Usage: TLAggregatorBiCombineJob <input1> <input2> <output>")
      System.exit(-1)
    }

    val conf = new Configuration
    val job = Job.getInstance(conf, "TL Aggregator Bi-Combine Job")
    job.setJarByClass(this.getClass)

    // Set up multiple inputs
    MultipleInputs.addInputPath(job, new Path(args(0)), classOf[TextInputFormat], classOf[BiTLMapper])
    MultipleInputs.addInputPath(job, new Path(args(1)), classOf[TextInputFormat], classOf[BiTLMapper])

    job.setCombinerClass(classOf[BiTLReducer])
    job.setReducerClass(classOf[BiTLReducer])

    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[IntWritable])

    FileOutputFormat.setOutputPath(job, new Path(args(2)))

    System.exit(if (job.waitForCompletion(true)) 0 else 1)
  }
}
