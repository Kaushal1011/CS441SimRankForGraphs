# Helpers Documentation

## Function Name
`SimRankJaccardSimilarity`

## Purpose
This function is used to calculate the similarity between two edges in a graph based on their properties. The similarity score is calculated using the Jaccard similarity coefficient.

## Parameters
- `other`: The other edge to compare to.

## Return Value
The similarity score between the two edges as a `Double`.

## How it Works
The function calculates the Jaccard similarity coefficient for four sets of properties:
- The source node properties of both edges
- The destination node properties of both edges
- The properties of the children of the source node of both edges
- The properties of the children of the destination node of both edges

The Jaccard similarity coefficient is calculated as the size of the intersection of two sets divided by the size of the union of the two sets. The function then takes the average of the four Jaccard similarity coefficients to get the final similarity score.

## Why it's Used
The `SimRankJaccardSimilarity` function is used to compare edges in a graph based on their properties. This is useful in a variety of applications, such as recommendation systems, where we might want to recommend items to a user based on the properties of items they have previously interacted with. By calculating the similarity between edges based on their properties, we can identify edges that are similar and use this information to make recommendations.

## Example Usage
```scala
val edge1 = new ComparableEdge(1, 2, 0.5, List(1, 2, 3), List(4, 5, 6), List(7, 8, 9), List(10, 11, 12))
val edge2 = new ComparableEdge(2, 3, 0.7, List(2, 3, 4), List(5, 6, 7), List(8, 9, 10), List(11, 12, 13))

val similarity = edge1.SimRankJaccardSimilarity(edge2)
println(similarity) // prints the similarity score between edge1 and edge2
```


## Function Name
`SimRankFromJaccardSimilarity`

## Purpose
This function is used to calculate the similarity between two nodes in a graph based on their properties. The similarity score is calculated using the Jaccard similarity coefficient.

## Parameters
- `other`: The other node to compare to.

## Return Value
The similarity score between the two nodes as a `Double`.

## How it Works
The function calculates the Jaccard similarity coefficient for two sets of properties:
- The properties of the node
- The properties of the children of the node

The Jaccard similarity coefficient is calculated as the size of the intersection of two sets divided by the size of the union of the two sets. The function then takes the average of the two Jaccard similarity coefficients to get the final similarity score.

## Why it's Used
The `SimRankFromJaccardSimilarity` function is used to compare nodes in a graph based on their properties. This is useful in a variety of applications, such as recommendation systems, where we might want to recommend items to a user based on the properties of items they have previously interacted with. By calculating the similarity between nodes based on their properties, we can identify nodes that are similar and use this information to make recommendations.

## Example Usage
```scala
val node1 = new ComparableNode(1, 2, 3, List(1, 2, 3), List(4, 5, 6))
val node2 = new ComparableNode(2, 3, 4, List(2, 3, 4), List(5, 6, 7))

val similarity = node1.SimRankFromJaccardSimilarity(node2)
println(similarity) // prints the similarity score between node1 and node2
```


## Object Name
`NodeDataParser`

## Purpose
This object is used to parse the string representation of a node or an edge in a graph. It deserializes the string representation of the node or edge that the preprocessing script outputs.

## Functions
- `parseNodeData(input: String): ComparableNode`: This function takes a string representation of a node and returns a `ComparableNode` object. It uses a regular expression pattern to extract the relevant information from the string and create a `ComparableNode` object.
- `parseEdgeData(input: String): ComparableEdge`: This function takes a string representation of an edge and returns a `ComparableEdge` object. It uses a regular expression pattern to extract the relevant information from the string and create a `ComparableEdge` object.

## How it Works
The `NodeDataParser` object uses regular expression patterns to extract the relevant information from the string representation of a node or an edge. It then creates a `ComparableNode` or `ComparableEdge` object using the extracted information.

## Why it's Used
The `NodeDataParser` object is used to deserialize the string representation of a node or an edge in a graph. This is useful in a variety of applications, such as recommendation systems, where we might want to parse the graph data and use it to make recommendations.

## Pros
- The `NodeDataParser` object is a simple and efficient way to parse the string representation of a node or an edge in a graph.
- It uses regular expression patterns to extract the relevant information from the string, which is a widely used technique for parsing text data.
- The `ComparableNode` and `ComparableEdge` objects that are created by the `NodeDataParser` object are easy to work with and can be used in a variety of applications.

## Example Usage
```scala
val nodeString = "(-1, 0, 1, List(1, 2, 3), List(4, 5, 6))"
val edgeString = "(-1, 0, 0.5, List(1, 2, 3), List(4, 5, 6), List(7, 8, 9), List(10, 11, 12))"

val node = NodeDataParser.parseNodeData(nodeString)
val edge = NodeDataParser.parseEdgeData(edgeString)

println(node) // prints the ComparableNode object created from the nodeString
println(edge) // prints the ComparableEdge object created from the edgeString
```