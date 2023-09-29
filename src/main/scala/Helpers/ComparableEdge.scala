package Helpers

// see tests for examples of how to use this class
// and how it works

/**
 * This class is used to compare edges in the graph
 * @param srcId source node id
 * @param dstId destination node id
 * @param weight weight of the edge
 * @param propertiesSrc properties of the source node
 * @param propertiesDst properties of the destination node
 * @param children_prop_hash_source children properties of the source node
 * @param children_prop_hash_destination children properties of the destination node
 */
class ComparableEdge (val srcId: Int, val dstId: Int, val weight: Double, val propertiesSrc: List[Int], val propertiesDst: List[Int], val children_prop_hash_source: List[Int], val children_prop_hash_destination: List[Int])  {

  /**
   * This function is used to compare two edges based on their properties
   * @param other the other edge to compare to
   * @return
   */
  def SimRankJaccardSimilarity(other: ComparableEdge): Double = {
    val intersectionSrcProp = this.propertiesSrc.intersect(other.propertiesSrc).size
    val unionSrcProp = this.propertiesSrc.concat(other.propertiesSrc).distinct.size

    val intersectionDstProp = this.propertiesDst.intersect(other.propertiesDst).size
    val unionDstProp = this.propertiesDst.concat(other.propertiesDst).distinct.size

    val intersectionSrcChildren = this.children_prop_hash_source.intersect(other.children_prop_hash_source).size
    val unionSrcChildren = this.children_prop_hash_source.concat(other.children_prop_hash_source).distinct.size

    val intersectionDstChildren = this.children_prop_hash_destination.intersect(other.children_prop_hash_destination).size
    val unionDstChildren = this.children_prop_hash_destination.concat(other.children_prop_hash_destination).distinct.size

    // similarity of src properties of both edges
    val jaccardSrc: Double = if (unionSrcProp == 0) 0 else intersectionSrcProp.toDouble / unionSrcProp.toDouble

    // similarity of dst properties of both edges
    val jaccardDst: Double = if (unionDstProp == 0) 0 else intersectionDstProp.toDouble / unionDstProp.toDouble

    // similarity of src childer properties. Children properites is an array that contains the hash of the properties of the children of the node
    val jaccardSrcChildren: Double = if (unionSrcChildren == 0) 0 else intersectionSrcChildren.toDouble / unionSrcChildren.toDouble

    // similarity of src childer properties. Children properites is an array that contains the hash of the properties of the children of the node
    val jaccardDstChildren:Double = if (unionDstChildren == 0) 0 else intersectionDstChildren.toDouble / unionDstChildren.toDouble

    // denominator is 4 if no union is 0, 3 if only one union is 0, 2 if two unions are 0, and 1 if three unions are 0
    val denominator: Int = Seq(unionSrcProp, unionDstProp, unionSrcChildren, unionDstChildren).count(_ > 0)

    val result = if (denominator == 0) {
      0.0 // Or another default value to return when denominator is zero
    } else {
      // change weight here later when tweaking sim rank
      (jaccardSrc + jaccardDst + jaccardSrcChildren + jaccardDstChildren) / denominator
    }

    result

  }

  def getSrcId: Int = {
    srcId
  }

  def getIdentifier: (Int, Int) = {
    if (srcId < dstId ) (srcId, dstId) else (dstId, srcId)
  }

  def getDstId: Int = {
    dstId
  }

  def getWeight: Double = {
    weight
  }

  def getPropertiesSrc: List[Int] = {
    propertiesSrc
  }

  def getPropertiesDst: List[Int] = {
    propertiesDst
  }

  def getChildrenPropHashSource: List[Int] = {
    children_prop_hash_source
  }

  def getChildrenPropHashDestination: List[Int] = {
    children_prop_hash_destination
  }

  /**
   * function that string encodes the ComparableEdge
   * @return string encoding of the ComparableEdge
   */
  override def toString: String = {
    s"($srcId, $dstId, $weight, $propertiesSrc, $propertiesDst, $children_prop_hash_source, $children_prop_hash_destination)"
  }


}


