package TLLabellerJob

import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{LongWritable, NullWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.lib.join.CompositeInputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import org.apache.hadoop.conf.Configuration

import scala.jdk.CollectionConverters.*
import org.slf4j.{Logger, LoggerFactory}

  // removes duplicates from the input
  private class TLLabellerCombinedReducer extends Reducer[Text, Text, Text, Text] {
    private val logger: Logger = LoggerFactory.getLogger(classOf[TLLabellerCombinedReducer])
    override def reduce(key: Text, values: java.lang.Iterable[Text], context: Reducer[Text, Text, Text, Text]#Context): Unit = {


        values.forEach(value => {
          context.write(key, value)
        })

    }
  }

