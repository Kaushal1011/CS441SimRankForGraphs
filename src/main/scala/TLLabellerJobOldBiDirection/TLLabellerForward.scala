package TLLabellerJobOldBiDirection

import org.slf4j.{Logger, LoggerFactory}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{LongWritable, NullWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.lib.join.CompositeInputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import org.apache.hadoop.conf.Configuration

  class TLLabellerForward extends Mapper[LongWritable, Text, Text, Text] {

    private val logger: Logger = LoggerFactory.getLogger(classOf[TLLabellerForward])
    override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, Text]#Context): Unit = {
      val key = value.toString.split("\t")(0)
      val nodesimrank = value.toString.split("\t")(1)
      val simRank = nodesimrank.split("=")(1).toDouble
      val nodestr = nodesimrank.split("=")(0)

      if (simRank < 0.1) {
        context.write(new Text("removed"), new Text(key))
      }
      else if (simRank > 0.1 && simRank < 1.0) {
        context.write(new Text("modified"), new Text(key))
      }
      else {
        context.write(new Text("matched"), new Text(key))
      }

    }
  }

