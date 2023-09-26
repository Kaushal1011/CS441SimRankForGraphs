package Helpers

class ComparableEdge (val srcId: Int, val dstId: Int, val weight: Double, val propertiesSrc: List[Int], val propertiesDst: List[Int], val children_prop_hash_source: List[Int], val children_prop_hash_destination: List[Int])  {

  def SimRankJaccardSimilarity(other: ComparableEdge): Double = {
    val intersectionSrcProp = this.propertiesSrc.intersect(other.propertiesSrc).size
    val unionSrcProp = this.propertiesSrc.concat(other.propertiesSrc).distinct.size

    val intersectionDstProp = this.propertiesDst.intersect(other.propertiesDst).size
    val unionDstProp = this.propertiesDst.concat(other.propertiesDst).distinct.size

    val intersectionSrcChildren = this.children_prop_hash_source.intersect(other.children_prop_hash_source).size
    val unionSrcChildren = this.children_prop_hash_source.concat(other.children_prop_hash_source).distinct.size

    val intersectionDstChildren = this.children_prop_hash_destination.intersect(other.children_prop_hash_destination).size
    val unionDstChildren = this.children_prop_hash_destination.concat(other.children_prop_hash_destination).distinct.size

    val jaccardSrc = if (unionSrcProp == 0) 0 else intersectionSrcProp.toDouble / unionSrcProp.toDouble
    val jaccardDst = if (unionDstProp == 0) 0 else intersectionDstProp.toDouble / unionDstProp.toDouble
    val jaccardSrcChildren = if (unionSrcChildren == 0) 0 else intersectionSrcChildren.toDouble / unionSrcChildren.toDouble
    val jaccardDstChildren = if (unionDstChildren == 0) 0 else intersectionDstChildren.toDouble / unionDstChildren.toDouble

    (jaccardSrc + jaccardDst + jaccardSrcChildren + jaccardDstChildren) / 4


  }

  def getSrcId: Int = {
    return srcId
  }

  def getDstId: Int = {
    return dstId
  }

  def getWeight: Double = {
    return weight
  }

  def getPropertiesSrc: List[Int] = {
    return propertiesSrc
  }

  def getPropertiesDst: List[Int] = {
    return propertiesDst
  }

  def getChildrenPropHashSource: List[Int] = {
    return children_prop_hash_source
  }

  def getChildrenPropHashDestination: List[Int] = {
    return children_prop_hash_destination
  }

  override def toString: String = {
    s"($srcId, $dstId, $weight, $propertiesSrc, $propertiesDst, $children_prop_hash_source, $children_prop_hash_destination)"
  }


}


