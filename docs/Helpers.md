
## Class Name
`ComparableNode`

## Purpose
This class is used to compare nodes in a graph based on their properties. It provides a `SimRankFromJaccardSimilarity` function that calculates the similarity between two nodes based on their properties.

## Properties
- `id`: The id of the node.
- `incoming_nodes_len`: The number of incoming nodes.
- `outgoing_node_len`: The number of outgoing nodes.
- `children_props_hash`: The hash of children properties.
- `properties`: The properties of the node.
- `graphType`: The type of the graph (original or reversed).

## Functions
- `SimRankFromJaccardSimilarity(other: ComparableNode): Double`: This function is used to compare two nodes based on their properties. It calculates the Jaccard similarity coefficient for two sets of properties: the properties of the node and the properties of the children of the node. It then takes the average of the two Jaccard similarity coefficients to get the final similarity score.
- `get_id: Int`: This function returns the id of the node.
- `get_number_of_incoming_nodes: Int`: This function returns the number of incoming nodes.
- `get_number_of_outgoing_nodes: Int`: This function returns the number of outgoing nodes.
- `get_children_props_hash: List[Int]`: This function returns the hash of children properties.
- `get_properties: List[Int]`: This function returns the properties of the node.

## How it Works
The `ComparableNode` class uses the Jaccard similarity coefficient to compare nodes based on their properties. The `SimRankFromJaccardSimilarity` function calculates the Jaccard similarity coefficient for two sets of properties: the properties of the node and the properties of the children of the node. It then takes the average of the two Jaccard similarity coefficients to get the final similarity score.

## Insights from Comments
The comments in the `ComparableNode` class provide examples of how to use the class and how it works. They also provide information about the purpose of the class and the parameters of the `SimRankFromJaccardSimilarity` function. The comments also provide information about the format of the string encoding of a `ComparableNode` object.

## Example Usage
```scala
val node1 = new ComparableNode(1, 2, 3, List(1, 2, 3), List(4, 5, 6))
val node2 = new ComparableNode(2, 3, 4, List(2, 3, 4), List(5, 6, 7))

val similarity = node1.SimRankFromJaccardSimilarity(node2)
println(similarity) // prints the similarity score between node1 and node2
```

## Class Name
`ComparableEdge`

## Purpose
This class is used to compare edges in a graph based on their properties. It provides a `SimRankJaccardSimilarity` function that calculates the similarity between two edges based on their properties.

## Properties
- `srcId`: The source node id.
- `dstId`: The destination node id.
- `weight`: The weight of the edge.
- `propertiesSrc`: The properties of the source node.
- `propertiesDst`: The properties of the destination node.
- `children_prop_hash_source`: The properties of the children of the source node.
- `children_prop_hash_destination`: The properties of the children of the destination node.

## Functions
- `SimRankJaccardSimilarity(other: ComparableEdge): Double`: This function is used to compare two edges based on their properties. It calculates the Jaccard similarity coefficient for four sets of properties: the source node properties of both edges, the destination node properties of both edges, the properties of the children of the source node of both edges, and the properties of the children of the destination node of both edges. It then takes the average of the four Jaccard similarity coefficients to get the final similarity score.

- `getSrcId: Int`: This function returns the source node id.
- `getDstId: Int`: This function returns the destination node id.
- `getWeight: Double`: This function returns the weight of the edge.
- `getPropertiesSrc: List[Int]`: This function returns the properties of the source node.
- `getPropertiesDst: List[Int]`: This function returns the properties of the destination node.
- `getChildrenPropHashSource: List[Int]`: This function returns the properties of the children of the source node.
- `getChildrenPropHashDestination: List[Int]`: This function returns the properties of the children of the destination node.

## How it Works
The `ComparableEdge` class uses the Jaccard similarity coefficient to compare edges based on their properties. The `SimRankJaccardSimilarity` function calculates the Jaccard similarity coefficient for four sets of properties: the source node properties of both edges, the destination node properties of both edges, the properties of the children of the source node of both edges, and the properties of the children of the destination node of both edges. It then takes the average of the four Jaccard similarity coefficients to get the final similarity score.

## Insights from Comments
The comments in the `ComparableEdge` class provide examples of how to use the class and how it works. They also provide information about the purpose of the class and the parameters of the `SimRankJaccardSimilarity` function. The comments also provide information about the format of the string encoding of a `ComparableEdge` object.

## Example Usage
```scala
val edge1 = new ComparableEdge(1, 2, 0.5, List(1, 2, 3), List(4, 5, 6), List(7, 8, 9), List(10, 11, 12))
val edge2 = new ComparableEdge(2, 3, 0.7, List(2, 3, 4), List(5, 6, 7), List(8, 9, 10), List(11, 12, 13))

val similarity = edge1.SimRankJaccardSimilarity(edge2)
println(similarity) // prints the similarity score between edge1 and edge2
```

## Object Name
`NGSYamlParser`

## Purpose
This object is used to parse YAML files generated by the NGS tool. It provides functions to parse the YAML file and extract information about added, modified, and removed nodes and edges.

## Functions
- `parseInputFile(filePath: String): ParsedData`: This function takes a file path as input and returns a `ParsedData` object that contains information about added, modified, and removed nodes and edges.
- `parseEdge(edge: String): (Int, Int)`: This function takes an edge string in the format "source:target" and returns a tuple containing the source and target node ids.
- `parseYaml(filePath: String): ParsedData`: This function is an alias for `parseInputFile` and takes a file path as input and returns a `ParsedData` object that contains information about added, modified, and removed nodes and edges.

## ParsedData
This is the data structure that the YAML file is parsed into. It contains the following fields:
- `addedNodes: List[Int]`: A list of node ids that were added.
- `nodesModified: List[Int]`: A list of node ids that were modified.
- `removedNodes: List[Int]`: A list of node ids that were removed.
- `modifiedEdges: List[(Int, Int)]`: A list of tuples containing the source and target node ids of edges that were modified.
- `addedEdges: List[(Int, Int)]`: A list of tuples containing the source and target node ids of edges that were added.
- `removedEdges: List[(Int, Int)]`: A list of tuples containing the source and target node ids of edges that were removed.

## How it Works
The `NGSYamlParser` object uses the SnakeYAML library to parse YAML files. It reads the YAML file and extracts information about added, modified, and removed nodes and edges. It returns a `ParsedData` object that contains this information.

## Insights from Comments
The comments in the `NGSYamlParser` object provide examples of how to use the functions and how they work. They also provide information about the format of the YAML file and the structure of the `ParsedData` object.

## Example Usage
```scala
val filePath = "./check.yaml" // Replace with the actual file path

val parsedData = NGSYamlParser.parseInputFile(filePath)

println("Added Nodes: " + parsedData.addedNodes)
println("Nodes Modified: " + parsedData.nodesModified)
println("Nodes Removed: " + parsedData.removedNodes)
println("Modified Edges: " + parsedData.modifiedEdges)
println("Added Edges: " + parsedData.addedEdges)
println("Removed Edges: " + parsedData.removedEdges)
```

GitHub Copilot: Sure, here's some documentation for the `NodeDataParser` object in the `NodeDataParser.scala` file:

## Object Name
`NodeDataParser`

## Purpose
This object is used to parse the output of the graph preprocessing script. It provides functions to deserialize the string representation of a node or an edge and create `ComparableNode` or `ComparableEdge` objects.

## Functions
- `parseNodeData(input: String): ComparableNode`: This function takes a string representation of a node and returns a `ComparableNode` object.
- `parseEdgeData(input: String): ComparableEdge`: This function takes a string representation of an edge and returns a `ComparableEdge` object.

## ComparableNode
This is the data structure that represents a node in the graph. It contains the following fields:
- `id: Int`: The id of the node.
- `incoming_nodes_len: Int`: The number of incoming nodes.
- `outgoing_node_len: Int`: The number of outgoing nodes.
- `children_props_hash: List[Int]`: The hash of children properties.
- `properties: List[Int]`: The properties of the node.

## ComparableEdge
This is the data structure that represents an edge in the graph. It contains the following fields:
- `srcId: Int`: The source node id.
- `dstId: Int`: The destination node id.
- `weight: Double`: The weight of the edge.
- `propertiesSrc: List[Int]`: The properties of the source node.
- `propertiesDst: List[Int]`: The properties of the destination node.
- `children_prop_hash_source: List[Int]`: The properties of the children of the source node.
- `children_prop_hash_destination: List[Int]`: The properties of the children of the destination node.

## How it Works
The `NodeDataParser` object uses regular expressions to parse the string representation of a node or an edge. It extracts the relevant information from the string and creates a `ComparableNode` or `ComparableEdge` object.

## Insights from Comments
The comments in the `NodeDataParser` object provide examples of how to use the functions and how they work. They also provide information about the format of the string representation of a node or an edge.

## Example Usage
```scala
val nodeString = "(1, 2, 3, List(1, 2, 3), List(4, 5, 6))"
val edgeString = "(1, 2, 0.5, List(1, 2, 3), List(4, 5, 6), List(7, 8, 9), List(10, 11, 12))"

val node = NodeDataParser.parseNodeData(nodeString)
val edge = NodeDataParser.parseEdgeData(edgeString)

println("Node ID: " + node.id)
println("Incoming Nodes: " + node.incoming_nodes_len)
println("Outgoing Nodes: " + node.outgoing_node_len)
println("Children Properties Hash: " + node.children_props_hash)
println("Properties: " + node.properties)

println("Source ID: " + edge.srcId)
println("Destination ID: " + edge.dstId)
println("Weight: " + edge.weight)
println("Source Node Properties: " + edge.propertiesSrc)
println("Destination Node Properties: " + edge.propertiesDst)
println("Source Node Children Properties Hash: " + edge.children_prop_hash_source)
println("Destination Node Children Properties Hash: " + edge.children_prop_hash_destination)
```