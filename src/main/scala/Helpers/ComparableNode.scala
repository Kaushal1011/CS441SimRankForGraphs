package Helpers

// see tests on how to use and examples of running

/**
 * This class is used to compare nodes in the graph
 * @param id - id of the node
 * @param incoming_nodes_len - number of incoming nodes
 * @param outgoing_node_len - number of outgoing nodes
 * @param children_props_hash - hash of children properties
 * @param properties - properties of the node
 * @param graphType - type of the graph (original or reversed)
 */
class ComparableNode (val id:Int, val incoming_nodes_len: Int, val outgoing_node_len: Int, val children_props_hash:List[Int], val properties:List[Int], val graphType:String = "original"){

  /**
   * This function is used to calculate the similarity between two nodes
   * @param other - the other node to compare with
   * @return
   */
  def SimRankFromJaccardSimilarity(other: ComparableNode): Double = {

    // similarity of children properties
    val intersectionChildProp = children_props_hash.intersect(other.children_props_hash)
    val unionChildProp = children_props_hash.concat(other.children_props_hash).distinct

    // similarity of properties
    val intersectionProps = properties.intersect(other.properties)
    val unionProps = properties.concat(other.properties).distinct

    // Jaccard similarity for child properties
    val jaccardSimilarityChildProp =
      if (unionChildProp.isEmpty) 0.0
      else intersectionChildProp.size.toDouble / unionChildProp.size.toDouble

    // Jaccard similarity for properties
    val jaccardSimilarityProps =
      if (unionProps.isEmpty) 0.0
      else intersectionProps.size.toDouble / unionProps.size.toDouble

    // denominator logic: Number of non-zero unions
    val denominator: Int = Seq(unionChildProp.size, unionProps.size).count(_ > 0)

    val simScore =
      if (denominator == 0) 0.0
      else (jaccardSimilarityChildProp + jaccardSimilarityProps) / denominator

    simScore
  }

  def get_id():Int = id

  def get_number_of_incoming_nodes():Int = incoming_nodes_len

  def get_number_of_outgoing_nodes():Int = outgoing_node_len
  def get_children_props_hash():List[Int] = children_props_hash
  def get_properties():List[Int] = properties

  /**
   * This function string encodes the node
   * @return - string encoding of the node
   */
  override def toString: String = s"($id, $incoming_nodes_len, $outgoing_node_len, $children_props_hash, $properties)"
}