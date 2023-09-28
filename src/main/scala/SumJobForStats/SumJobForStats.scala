package SumJobForStats

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{IntWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import scala.jdk.CollectionConverters.*
import Utilz.*

// Aggregates CTL WTL ATL DTL calculates sums also finds GTL BTL and RTL
object SumJobForStats {

  // Mapper class
  private class TLMapper extends Mapper[Object, Text, Text, IntWritable] {

    override def map(key: Object, value: Text, context: Mapper[Object, Text, Text, IntWritable]#Context): Unit = {
      val splits = value.toString.split("\t")
      val tlType = splits(0)
      val count = splits(1).toInt

      context.write(new Text(tlType), new IntWritable(count))

      // Creating new keys for aggregate counts
      // clever aggregation to use this compute GTL as sum of ATL and DTL and BTL as sum of CTL and WTL and RTL as sum of GTL and BTL
      if (Set("ATL", "DTL").contains(tlType)) {
        context.write(new Text("GTL"), new IntWritable(count))
        context.write(new Text("RTL"), new IntWritable(count))
      } else if (Set("CTL", "WTL").contains(tlType)) {
        context.write(new Text("BTL"), new IntWritable(count))
        context.write(new Text("RTL"), new IntWritable(count))
      }
    }
  }

  // Reducer class: sums up the counts for each key
  // Similar to the word count example
  // used as combiner and reducer to reduce the number of records sent to reducer
  // merge sum pattern
  class TLReducer extends Reducer[Text, IntWritable, Text, IntWritable] {
    override def reduce(key: Text, values: java.lang.Iterable[IntWritable], context: Reducer[Text, IntWritable, Text, IntWritable]#Context): Unit = {
      val sum = values.asScala.foldLeft(0)(_ + _.get())
      context.write(key, new IntWritable(sum))
    }
  }

  def main(args: Array[String]): Unit = {

    val logger = Utilz.CreateLogger(getClass)

    if args.length != 2 then
      logger.error("Usage: TLAggregatorJob <input path> <output path>")
      System.exit(-1)

    logger.info("Starting TL Aggregator Job")

    val conf = new Configuration
    val job = Job.getInstance(conf, "TL Aggregator Job")
    job.setJarByClass(this.getClass)

    // Magic happens here
    job.setMapperClass(classOf[TLMapper])
    job.setCombinerClass(classOf[TLReducer])
    job.setReducerClass(classOf[TLReducer])

    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[IntWritable])
    FileInputFormat.addInputPath(job, new Path(args(0)))
    FileOutputFormat.setOutputPath(job, new Path(args(1)))
    System.exit(
      if (job.waitForCompletion(true)) {
        logger.info("Job completed successfully")
        0
      }
      else{
        logger.error("Job failed")
        1
      }
    )
  }
}
