package TLLabellerJob.TLLabellerJobOldBiDirection

import org.slf4j.{Logger, LoggerFactory}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{LongWritable, NullWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.lib.join.CompositeInputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import org.apache.hadoop.conf.Configuration


  class TLLabellerReverse extends Mapper[LongWritable, Text, Text, Text] {

    private val logger: Logger = LoggerFactory.getLogger(classOf[TLLabellerCombinedReducer])
    override def map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, Text]#Context): Unit = {
      val key = value.toString.split("\t")(0)
      val nodesimrank = value.toString.split("\t")(1)
      val simRank = nodesimrank.split("=")(1).toDouble
      val nodestr = nodesimrank.split("=")(0)

      if (simRank < 0.1) {
        context.write(new Text("added"), new Text(key))
      }

    }
  }

