## Object Name
`SumJobForStatsCombineJob`

## Purpose
This object is used to combine the output of two different mappers that count the number of occurrences of different types of temporal links (TLs) in a graph. It uses a reducer to combine the counts for each TL type and output the total count for each type.

## Classes
- `BiTLMapper`: A mapper that reads the output of the graph preprocessing script and counts the number of occurrences of different types of temporal links (TLs).
- `BiTLReducer`: A reducer that combines the counts for each TL type and outputs the total count for each type.

## Functions
- `main(args: Array[String]): Unit`: The main function that sets up the job and runs it.

## How it Works
The `SumJobForStatsCombineJob` object sets up a Hadoop job to combine the output of two different mappers that count the number of occurrences of different types of temporal links (TLs) in a graph. It uses a reducer to combine the counts for each TL type and output the total count for each type.

The `BiTLMapper` reads the output of the graph preprocessing script and counts the number of occurrences of different types of temporal links (TLs). It outputs the TL type and the count as a key-value pair.

The `BiTLReducer` combines the counts for each TL type and outputs the total count for each type. It receives the TL type and a list of counts as input. It sums the counts and outputs the TL type and the total count as a key-value pair.

## Object Name
`SumJobForStats`

## Purpose
This object is used to aggregate the counts of different types of temporal links (TLs) in a graph. It uses a mapper to find the counts of GTL, BTL, and RTL, and a reducer to sum up the counts for each TL type.

## Classes
- `TLMapper`: A mapper that reads the output of the graph preprocessing script and finds the counts of GTL, BTL, and RTL.
- `TLReducer`: A reducer that sums up the counts for each TL type.

## Functions
- `main(args: Array[String]): Unit`: The main function that sets up the job and runs it.

## How it Works
The `SumJobForStats` object sets up a Hadoop job to aggregate the counts of different types of temporal links (TLs) in a graph. It uses a mapper to find the counts of GTL, BTL, and RTL, and a reducer to sum up the counts for each TL type.

The `TLMapper` reads the output of the graph preprocessing script and finds the counts of GTL, BTL, and RTL. It outputs the TL type and the count as a key-value pair. It also creates new keys for aggregate counts, such as GTL as the sum of ATL and DTL, BTL as the sum of CTL and WTL, and RTL as the sum of GTL and BTL.

The `TLReducer` sums up the counts for each TL type. It receives the TL type and a list of counts as input. It sums the counts and outputs the TL type and the total count as a key-value pair.
