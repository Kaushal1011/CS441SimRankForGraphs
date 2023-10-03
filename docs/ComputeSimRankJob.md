## Object Name
`ComputeSimRankJob`

## Purpose
This object is used to compute the similarity between all nodes in a graph using the Jaccard Similarity metric. It uses a custom writable to store the similarity and the node string, a cyclic reducer to act as a combiner and reducer, a custom parser to parse the node data, and a custom comparable node to compare nodes.

## Functions
- `main(args: Array[String]): Unit`: The main function that sets up the job and runs it.

## Classes
- `NodeSimWritable`: A custom writable to store similarity and node string.
- `JaccardMapper`: A mapper that finds similarity of original node(i) - perturbed node(j) for all i and j.
- `JaccardReducer`: A cyclic reducer such that it can act as a combiner and reducer. The reducer finds the maximum in all the values for a given key.

## How it Works
The `ComputeSimRankJob` object sets up a Hadoop job to compute the similarity between all nodes in a graph using the Jaccard Similarity metric. It uses a custom writable to store the similarity and the node string, a cyclic reducer to act as a combiner and reducer, a custom parser to parse the node data, and a custom comparable node to compare nodes.

The `JaccardMapper` maps the similarity of original node(i) - perturbed node(j) for all i and j. The input file contains the cross product of the set of all nodes in the graph. The mapper calculates the Jaccard similarity of properties selected for comparison and writes the similarity in both forward and reverse directions.

The `JaccardReducer` finds the maximum in all the values for a given key. It sorts all values and finds the value with the highest similarity.
