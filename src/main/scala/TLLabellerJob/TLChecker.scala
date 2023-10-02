package TLLabellerJob


import java.net.URI
import java.io.FileInputStream
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{IntWritable, LongWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.{FileInputFormat, MultipleInputs}
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat

import scala.jdk.CollectionConverters.*
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import org.slf4j.{Logger, LoggerFactory}
import Helpers.{NGSYamlParser, NodeDataParser}
import Helpers.NGSYamlParser.ParsedData

import Utilz.*

// new to scala
// did some chatgpting to load file from cache but turned out unexplainable
// used lazy loader to load ngs yaml only once
// uses setup step to get truth yaml
// had to use one var here :(

/**
 * This is the reducer that finds CTL, DTL, WTL, ATL values for each type of processing
 */
private class TLChecker extends Reducer[Text, Text, Text, IntWritable] {

  private val logger: Logger = Utilz.CreateLogger(classOf[TLChecker])

  private class Lazy[T](computation: => T) {
    lazy val value: T = computation
  }

  private var contextOption: Option[Reducer[Text, Text, Text, IntWritable]#Context] = None

  private lazy val parsedDataLazy: Lazy[ParsedData] = new Lazy({
    contextOption match {
      case Some(ctx) =>
        val localFiles = ctx.getCacheFiles
        NGSYamlParser.parseYaml(localFiles(0).toString)
      case None => throw new RuntimeException("Context not set up!")
    }
  })

  override def setup(context: Reducer[Text, Text, Text, IntWritable]#Context): Unit = {
    contextOption = Some(context)
    logger.info("TLChecker Reducer Setup")
  }

  // reducer that finds CTL, DTL, WTL, ATL values for each type of processing
  // brain had almost stopped working figuring out how to do this
  // as the reducer gets keys added modified, removed and matched
  // essentially all nodes in added, modified and removed are tracebility links that should be discarded
  // a true negative of TLs
  // matched are the one that are TLS
  // true positive of TLs
  // we only have added, modified and removed truth values in yaml
  // a correct tl (node or edge) is one that doesnt exist in any of the added, modified and removed sets
  // matched truth values  = Full truth - (added + modified + removed) or (elements not in the union of added, modified and removed sets)
  // The computation uses the above logic to find CTL, DTL, WTL, ATL values

  /**
   * This is the reducer that finds CTL, DTL, WTL, ATL values for each type of processing
   * @param key key
   * @param values values
   * @param context context
   */
  override def reduce(key: Text, values: java.lang.Iterable[Text], context: Reducer[Text, Text, Text, IntWritable]#Context): Unit = {

    val parsedData = parsedDataLazy.value

    val typeOfProcessing = context.getConfiguration.get("typeOfProcessing")

    logger.info("TLChecker Reducer")
    logger.info("Processing " + typeOfProcessing + " type of processing")

    if (typeOfProcessing == "node") {

      val addedNodesTrue = parsedData.addedNodes.toSet
      val removedNodesTrue = parsedData.removedNodes.toSet
      val modifiedNodesTrue = parsedData.nodesModified.toSet

      // TLs that are discarded by NGS perturbation. These is the truth value for TLs that should be discarded
      val trueDiscardedTLs = addedNodesTrue.union(removedNodesTrue).union(modifiedNodesTrue)

      if (key.toString == "added") {
        val addedonlyIds: Set[Int] = values.asScala.map(value => NodeDataParser.parseNodeData(value.toString.split("\\*")(0)).get_id()).toSet

        // CTL is the number of correct TLs that are mistakenly discarded by your algorithm
        // any true matched node that is in added output is a correct TL that is mistakenly discarded by your algorithm
        // true matched nodes are the one not in union of added, modified and removed sets
        val CTL = addedonlyIds.diff(trueDiscardedTLs).size

        // the number of correctly discarded TLs
        // addedOnlyIds is list of discared TLs
        // trueDiscardedTLs is the truth value for discarded TLs
        // intersect gives the number of correctly discarded TLs
        val DTL = addedonlyIds.intersect(trueDiscardedTLs).size

        logger.info("CTL " + CTL)
        logger.info("DTL " + DTL)

        context.write(new Text("CTL"), new IntWritable(CTL))
        context.write(new Text("DTL"), new IntWritable(DTL))

      }
      if (key.toString == "removed") {
        val removedonlyIds: Set[Int] = values.asScala.map(value => NodeDataParser.parseNodeData(value.toString.split("\\*")(0)).get_id()).toSet

        // CTL is the number of correct TLs that are mistakenly discarded by your algorithm
        // any true matched node that is in removed output is a correct TL that is mistakenly discarded by your algorithm
        // true matched nodes are the one not in union of added, modified and removed sets
        val CTL = removedonlyIds.diff(trueDiscardedTLs).size

        // the number of correctly discarded TLs
        // removedOnlyIds is list of discared TLs
        // trueDiscardedTLs is the truth value for discarded TLs
        // intersect gives the number of correctly discarded TLs
        val DTL = removedonlyIds.intersect(trueDiscardedTLs).size

        logger.info("CTL " + CTL)
        logger.info("DTL " + DTL)

        context.write(new Text("CTL"), new IntWritable(CTL))
        context.write(new Text("DTL"), new IntWritable(DTL))

      }
      if (key.toString == "modified") {
        val modifiedonlyIds = values.asScala.map(value => NodeDataParser.parseNodeData(value.toString.split("\\*")(0)).get_id()).toSet

        // CTL is the number of correct TLs that are mistakenly discarded by your algorithm
        // any true matched node that is in modified output is a correct TL that is mistakenly discarded by your algorithm
        // true matched nodes are the one not in union of added, modified and removed sets
        val CTL = modifiedonlyIds.diff(trueDiscardedTLs).size

        // the number of correctly discarded TLs
        // modifiedonlyIds is list of discared TLs
        // trueDiscardedTLs is the truth value for discarded TLs
        // intersect gives the number of correctly discarded TLs
        val DTL = modifiedonlyIds.intersect(trueDiscardedTLs).size

        logger.info("CTL " + CTL)
        logger.info("DTL " + DTL)

        context.write(new Text("CTL"), new IntWritable(CTL))
        context.write(new Text("DTL"), new IntWritable(DTL))

      }
      if (key.toString == "matched") {
        val matchedonlyIds = values.asScala.map(value => NodeDataParser.parseNodeData(value.toString.split("\\*")(0)).get_id()).toSet

        // WTL is the number of wrong TLs that the your algorithm accepts
        // any matched node in the list of truediscarded TLs is a wrong TL that your algorithm accepts
        // matchedonlyIds is the list of matched nodes
        val WTL = matchedonlyIds.intersect(trueDiscardedTLs).size

        // ATL is the number of correctly accepted TLs,
        // ATL is when a node is in matched output and is present in the true matched set
        // true matched set is the one not in union of (added, modified and removed sets)
        val ATL = matchedonlyIds.diff(trueDiscardedTLs).size

        logger.info("WTL " + WTL)
        logger.info("ATL " + ATL)

        context.write(new Text("WTL"), new IntWritable(WTL))
        context.write(new Text("ATL"), new IntWritable(ATL))

      }
    }
    else {

      // logic for edges stays the same as nodes in determining CTL, DTL, WTL, ATL values

      val addedEdgesTrue = parsedData.addedEdges.toSet
      val removedEdgesTrue = parsedData.removedEdges.toSet
      val modifiedEdgesTrue = parsedData.modifiedEdges.toSet

      val trueDiscardedTLs = addedEdgesTrue.union(removedEdgesTrue).union(modifiedEdgesTrue)

      if (key.toString == "added") {
        val addedEdgesonlyIds = values.asScala.map(value => NodeDataParser.parseEdgeData(value.toString.split("\\*")(0)).getIdentifier).toSet

        val CTL = addedEdgesonlyIds.diff(trueDiscardedTLs).size

        val DTL = addedEdgesonlyIds.intersect(trueDiscardedTLs).size

        logger.info("CTL " + CTL)
        logger.info("DTL " + DTL)

        context.write(new Text("CTL"), new IntWritable(CTL))
        context.write(new Text("DTL"), new IntWritable(DTL))


      }
      if (key.toString == "removed") {
        val removedEdgesonlyIds = values.asScala.map(value => NodeDataParser.parseEdgeData(value.toString.split("\\*")(0)).getIdentifier).toSet

        val CTL = removedEdgesonlyIds.diff(trueDiscardedTLs).size

        val DTL = removedEdgesonlyIds.intersect(trueDiscardedTLs).size

        logger.info("CTL " + CTL)
        logger.info("DTL " + DTL)

        context.write(new Text("CTL"), new IntWritable(CTL))
        context.write(new Text("DTL"), new IntWritable(DTL))


      }
      if (key.toString == "modified") {
        val modifiedEdgesonlyIds = values.asScala.map(value => NodeDataParser.parseEdgeData(value.toString.split("\\*")(0)).getIdentifier).toSet

        val CTL = modifiedEdgesonlyIds.diff(trueDiscardedTLs).size

        val DTL = modifiedEdgesonlyIds.intersect(trueDiscardedTLs).size

        logger.info("CTL " + CTL)
        logger.info("DTL " + DTL)

        context.write(new Text("CTL"), new IntWritable(CTL))
        context.write(new Text("DTL"), new IntWritable(DTL))

      }
      if (key.toString == "matched") {
        val matchedEdgesonlyIds = values.asScala.map(value => NodeDataParser.parseEdgeData(value.toString.split("\\*")(0)).getIdentifier).toSet

        val WTL = matchedEdgesonlyIds.intersect(trueDiscardedTLs).size

        val ATL = matchedEdgesonlyIds.diff(trueDiscardedTLs).size

        logger.info("WTL " + WTL)
        logger.info("ATL " + ATL)

        context.write(new Text("WTL"), new IntWritable(WTL))
        context.write(new Text("ATL"), new IntWritable(ATL))

      }
    }

  }
}