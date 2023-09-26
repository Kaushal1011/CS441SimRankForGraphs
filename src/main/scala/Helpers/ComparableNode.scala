package Helpers

class ComparableNode (val id:Int, val incoming_nodes_len: Int, val outgoing_node_len: Int, val children_props_hash:List[Int], val properties:List[Int], val graphType:String = "original"){

  def SimRankFromJaccardSimilarity(other: ComparableNode): Double = {
    val intersectionChildProp = children_props_hash.intersect(other.children_props_hash)
    val addedChildProp = children_props_hash.concat(other.children_props_hash)
    // from union remove intersection
    val unionChildProp = addedChildProp.diff(intersectionChildProp)

    val jaccardSimilarityChildProp = if (children_props_hash.size.toDouble == 0.0 || other.children_props_hash.size.toDouble==0.0) 0.0 else intersectionChildProp.size.toDouble / unionChildProp.size.toDouble


    val intersectionProps = properties.intersect(other.properties)
    val addedProps = properties.concat(other.properties)
    // from union remove intersection
    val unionProps = addedProps.diff(intersectionProps)

    val jaccardSimilarityProps = if (properties.size.toDouble == 0.0 || other.properties.size.toDouble==0.0 ) 0.0 else intersectionProps.size.toDouble / unionProps.size.toDouble


    val simScore = jaccardSimilarityProps

    simScore
  }

  def get_id():Int = id

  def get_number_of_incoming_nodes():Int = incoming_nodes_len

  def get_number_of_outgoing_nodes():Int = outgoing_node_len
  def get_children_props_hash():List[Int] = children_props_hash
  def get_properties():List[Int] = properties

  override def toString: String = s"($id, $incoming_nodes_len, $outgoing_node_len, $children_props_hash, $properties)"
}