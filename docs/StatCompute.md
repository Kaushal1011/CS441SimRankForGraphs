## Object Name
`StatCompute`

## Purpose
This object is used to compute the accuracy, BLTR, and VPR metrics using a clever map reduce. It uses an identity mapper to pass the data through and a clever reducer that uses a case class to hold the metrics values.

## Classes
- `IdentityMapper`: An identity mapper that just passes the data through.
- `MetricsReducer`: A reducer that uses a case class to hold the metrics values and computes the accuracy, BLTR, and VPR metrics.

## Functions
- `main(args: Array[String]): Unit`: The main function that sets up the job and runs it.

## How it Works
The `StatCompute` object sets up a Hadoop job to compute the accuracy, BLTR, and VPR metrics using a clever map reduce. It uses an identity mapper to pass the data through and a clever reducer that uses a case class to hold the metrics values.

The `IdentityMapper` just passes the data through and writes it to the context with a key of "metrics".

The `MetricsReducer` uses a case class to hold the metrics values and computes the accuracy, BLTR, and VPR metrics. It folds over the values and updates the metrics case class. It then computes the metrics and writes them to the context with the appropriate key.