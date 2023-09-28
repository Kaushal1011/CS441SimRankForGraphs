package StatCompute

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{DoubleWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}

import scala.jdk.CollectionConverters.*
import Utilz.*

// Compute Accuracy, BLTR and VPR using a clever map reduce
// I wanted to try out and learn more about how the identity mapper could be used
// Clever reduce function that uses a case class to hold the metrics values
// avoid vars at all costs
// pair programmed with chatgpt on this one to avoid the vars, i am new to scala ;_;
object StatCompute {



  // Define the case class to hold metrics values
  private case class Metrics(ATL: Double = 0.0, RTL: Double = 0.0, WTL: Double = 0.0, GTL: Double = 0.0, BTL: Double = 0.0)

  // Identity mapper that just passes the data through
  private class IdentityMapper extends Mapper[Object, Text, Text, Text] {
    private val logger = Utilz.CreateLogger(classOf[IdentityMapper])
    override def map(key: Object, value: Text, context: Mapper[Object, Text, Text, Text]#Context): Unit = {
      logger.debug(s"Mapper: $value")
      context.write(new Text("metrics"), value)
    }
  }

  // clever reducer that uses a case class to hold the metrics values
  private class MetricsReducer extends Reducer[Text, Text, Text, DoubleWritable] {

    private val logger = Utilz.CreateLogger(classOf[MetricsReducer])
    override def reduce(key: Text, values: java.lang.Iterable[Text], context: Reducer[Text, Text, Text, DoubleWritable]#Context): Unit = {

      logger.debug(s"Reducer: $key, $values")

      // immutable scala magic to fold over the values and update the metrics case class
      val metrics = values.iterator().asScala.foldLeft(Metrics()) { (accumulatedMetrics, currentValue) =>
        val splits = currentValue.toString.split("\t")
        splits(0) match {
          case "ATL" => accumulatedMetrics.copy(ATL = splits(1).toDouble)
          case "RTL" => accumulatedMetrics.copy(RTL = splits(1).toDouble)
          case "WTL" => accumulatedMetrics.copy(WTL = splits(1).toDouble)
          case "GTL" => accumulatedMetrics.copy(GTL = splits(1).toDouble)
          case "BTL" => accumulatedMetrics.copy(BTL = splits(1).toDouble)
          case _ => accumulatedMetrics
        }
      }

      logger.debug(s"Metrics: $metrics")



      // As defined in the homework readme
      val ACC = metrics.ATL / metrics.RTL
      val BLTR = metrics.WTL / metrics.RTL
      val VPR = ((metrics.GTL - metrics.BTL) / (2 * metrics.RTL)) + 0.5

      logger.debug(s"ACC: $ACC, BLTR: $BLTR, VPR: $VPR")

      // final output :phew:
      context.write(new Text("ACC"), new DoubleWritable(ACC))
      context.write(new Text("BLTR"), new DoubleWritable(BLTR))
      context.write(new Text("VPR"), new DoubleWritable(VPR))
    }
  }

  def main(args: Array[String]): Unit = {

    val logger = Utilz.CreateLogger(getClass)

    logger.info(s"Running MetricsComputeJob with args: ${args.mkString(",")}")

    if (args.length != 2) {
      println("Usage: MetricsComputeJob <input> <output>")
      logger.error("Usage: MetricsComputeJob <input> <output>")
      System.exit(-1)
    }

    val conf = new Configuration
    val job = Job.getInstance(conf, "Metrics Compute Job")
    job.setJarByClass(this.getClass)

    job.setMapperClass(classOf[IdentityMapper])
    job.setReducerClass(classOf[MetricsReducer])

    job.setMapOutputKeyClass(classOf[Text])
    job.setMapOutputValueClass(classOf[Text])
    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[DoubleWritable])

    FileInputFormat.addInputPath(job, new Path(args(0)))
    FileOutputFormat.setOutputPath(job, new Path(args(1)))

    System.exit(if (job.waitForCompletion(true))
      {
        logger.info("MetricsComputeJob completed successfully")
        0
      } else {
      logger.error("MetricsComputeJob failed")
      1
      }
    )
  }
}
