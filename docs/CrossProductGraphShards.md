## Object Name
`CrossProductGraphShards`

## Purpose
This object is used to take the cross product of nodes from a 5% shard of the original graph and a 5% shard of the perturbed graph. It uses a mapper to take the cross product and write the output to a file.

## Classes
- `crossMapper`: A mapper that takes the cross product of nodes from a 5% shard of the original graph and a 5% shard of the perturbed graph.

## Functions
- `main(args: Array[String]): Unit`: The main function that sets up the job and runs it.

## How it Works
The `CrossProductGraphShards` object sets up a Hadoop job to take the cross product of nodes from a 5% shard of the original graph and a 5% shard of the perturbed graph. It uses a mapper to take the cross product and write the output to a file.

The `crossMapper` takes the input file, which contains two groups of nodes in the original and perturbed graph, and splits them into shards. It then takes the cross product of nodes from a 5% shard of the original graph and a 5% shard of the perturbed graph and writes the output to a file.
