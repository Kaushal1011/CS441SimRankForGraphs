## Object Name
`TLLabellerJob`

## Purpose
This object is used to label nodes and edges in a graph as added, removed, modified, or matched based on threshold values loaded from a configuration file. It uses a mapper to read the input graph data and a reducer to output the counts of different types of tracebility link metrics (TLs).

## Classes
- `TLLabeller`: A mapper that reads the input graph data and labels nodes and edges as added, removed, modified, or matched based on threshold values loaded from a configuration file.
- `TLChecker`: A reducer that outputs the counts of different types of tracebility link metrics (TLs).

## Functions
- `main(args: Array[String]): Unit`: The main function that sets up the job and runs it.

## How it Works
The `TLLabellerJob` object sets up a Hadoop job to label nodes and edges in a graph as added, removed, modified, or matched based on threshold values loaded from a configuration file. It uses a mapper to read the input graph data and a reducer to output the counts of different types of tracebility link metrics (TLs).

The `TLLabeller` mapper reads the input graph data and labels nodes and edges as added, removed, modified, or matched based on threshold values loaded from a configuration file. It outputs the node or edge ID and the label as a key-value pair.

The `TLChecker` reducer outputs the counts of different types of tracebility link metrics (TLs). It receives the node or edge ID and a list of labels as input. It computes the counts of CTL, DTL, WTL, and ATL based on the labels and outputs the counts as a key-value pair.

## Class Name
`TLLabeller`

## Purpose
This class is a mapper used in the `TLLabellerJob` object to label nodes and edges in a graph as added, removed, modified, or matched based on threshold values loaded from a configuration file.

## Functions
- `map(key: LongWritable, value: Text, context: Mapper[LongWritable, Text, Text, Text]#Context): Unit`: The mapper function that reads the input graph data and labels nodes and edges as added, removed, modified, or matched based on threshold values loaded from a configuration file.

## How it Works
The `TLLabeller` class is a mapper used in the `TLLabellerJob` object to label nodes and edges in a graph as added, removed, modified, or matched based on threshold values loaded from a configuration file.

The `map` function reads the input graph data and labels nodes and edges as added, removed, modified, or matched based on threshold values loaded from a configuration file. It splits the input value by tabs and extracts the node or edge ID and the similarity rank. It then reads the threshold values from the configuration file based on the type of processing (node or edge) and the TL type (forward or reverse). It compares the similarity rank to the threshold values and outputs the node or edge ID and the label as a key-value pair.

If the TL type is forward, it labels the node or edge as removed if the similarity rank is below the removed threshold, as modified if the similarity rank is between the modified threshold down and up, and as matched if the similarity rank is above the modified threshold up. If the TL type is reverse, it labels the node or edge as added if the similarity rank is below the added threshold.

## Class Name
`TLChecker`

## Purpose
This class is a reducer that finds CTL, DTL, WTL, ATL values for each type of processing in a graph. It uses a lazy loader to load the NGS YAML only once, and uses the setup step to get the truth YAML. It also uses one var to store the context option.

## Functions
- `setup(context: Reducer[Text, Text, Text, IntWritable]#Context): Unit`: A function that sets up the context option and logs the setup.
- `reduce(key: Text, values: java.lang.Iterable[Text], context: Reducer[Text, Text, Text, IntWritable]#Context): Unit`: A function that finds CTL, DTL, WTL, ATL values for each type of processing in a graph.

## How it Works
The `TLChecker` class is a reducer that finds CTL, DTL, WTL, ATL values for each type of processing in a graph. It uses a lazy loader to load the NGS YAML only once, and uses the setup step to get the truth YAML. It also uses one var to store the context option.

The `setup` function sets up the context option and logs the setup.

The `reduce` function finds CTL, DTL, WTL, ATL values for each type of processing in a graph. It receives the key, values, and context as input. It uses the parsed data from the NGS YAML to find the true discarded TLs. It then uses the key to determine the type of processing (node or edge) and finds the CTL, DTL, WTL, ATL values accordingly.
