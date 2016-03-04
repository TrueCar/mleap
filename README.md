# MLeap

Easily put your Spark ML Pipelines into action with MLeap. Train your feature and regression/classification pipeline with Spark then easily convert them to MLeap pipelines to deploy them anywhere. Take your pipelines to an API server, Hadoop, or even back to Spark to execute on a DataFrame.

MLeap allows for easy serialization of your estimator and transformer pipelines so you can save them for reuse later. Executing an MLeap pipeline does not require a SparkContext or DataFrame so there is very little overhead for realtime one-off predictions. You don't have to worry about Spark dependencies for executing your models, just add the lightweight MLeap runtime library instead.

MLeap makes deploying your Spark ML pipelines with 3 core functions:

1. Release: Deploy your entire ML pipeline without a SparkContext or any dependency on Spark libraries.
2. Reuse: Export your ML pipeline to easy-to-read JSON files so you can reuse pipelines.
3. Recycle: Export your training pipelines to easy-to-read JSON files so you can easily modify your training pipelines.

## Setup

### For Runtime Only

#### SBT

```
libraryDependencies += "com.truecar.mleap" %% "mleap-runtime" % "0.1-SNAPSHOT"
```

#### Maven

```
<dependency>
    <groupId>com.truecar.mleap</groupId>
    <artifactId>mleap-runtime_2.10</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
```

### For Spark Export and Learning

#### SBT

```
libraryDependencies += "com.truecar.mleap" %% "mleap-spark" % "0.1-SNAPSHOT"
```

#### Maven

```
<dependency>
    <groupId>com.truecar.mleap</groupId>
    <artifactId>mleap-spark_2.10</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
```

## Modules

MLeap is broken into 5 modules:

1. mleap-core - Core execution building blocks, includes runtime for executing linear regressions, random forest models, logisitic regressions, assembling feature vectors, string indexing, one hot encoding, etc. It provides a core linear algebra system for all of these tasks.
2. mleap-runtime - Provides LeapFrame, which is essentially a lightweight DataFrame without any dependencies on the Spark libraries. LeapFrames support 3 data types: double, string, and vector. Also provides MLeap Transformers for executing ML pipelines on LeapFrames. Spark ML pipelines get converted to MLeap pipelines which are provided with this library. This module provides serialization for all pipeline obects.
3. mleap-learning - Provides definitions for all supported Estimators as well as serialization for them. These objects are converted to Spark Estimators for actual training. This module has no dependencies on Spark.
4. mleap-spark - Provides Spark/MLeap integration. SparkLeapFrame is an implementation of LeapFrame with a Spark RDD backing the data so you can execute MLeap transformers on a Spark cluster. Provides conversion from Spark Transformers to MLeap Transformers. Provides conversion from MLeap Estimators to Spark Estimators. This allows a very intuitive usage of MLeap without worrying about how Spark is being used under the hood: MLeap Estimator -> Spark Estimator -> Spark Transformer -> MLeap Transformer.

## Example

Let's see how we can release, reuse, and recycle with MLeap given the task of predicting the listing prices of used cars. We will use a series of feature assemblers and finally we will create a linear regression pipeline to make the predictions. We will break the task up into several steps:

1. Creating the Estimator Pipeline
2. Training the ML Pipeline
3. Scoring With a Spark DataFrame
4. Scoring Without a Spark DataFrame

### Creating an MLeap Pipeline

```scala
package com.example

import java.io.File

import com.truecar.mleap.learning.estimator._
import com.truecar.mleap.core.serialization.JsonSerializationSupport._
import com.truecar.mleap.learning.serialization.LearningJsonSupport._
import com.truecar.mleap.runtime.LeapFrame
import com.truecar.mleap.runtime.transformer.{PipelineModel, Transformer}
import com.truecar.mleap.spark.MleapSparkSupport._
import com.truecar.mleap.runtime.LeapFrame.Ops
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}

/**
  * Created by hwilkins on 12/28/15.
  */
object TrainLinearRegression extends App {
  /*
  1. Create the Estimator Pipeline

  To accomplish this, we will create the necessary
  string indexers, vector assemblers, scalers, and one hot encoders
  for our features to be properly fed into a linear regression.
   */

  val continuousFeatures = Seq("mileage", "averageListingPrice")
  val categoricalFeatures = Seq("modelId", "modelAge", "condition")

  // Assemble the continuous features into a vector and scale them with mean/stdev.
  val continuousFeatureAssembler = VectorAssemblerEstimator(inputCols = continuousFeatures,
    outputCol = "continuousFeatures")
  val continuousScaler = StandardScalerEstimator(inputCol = "continuousFeatures",
    outputCol = "scaledContinuousFeatures")

  // Index our categorical features and then one hot encode them into vectors.
  // Finally, compose all one-hot encoded vectors into a single vector for
  // our categorical features.
  val categoricalIndexers = categoricalFeatures.map {
    feature =>
      StringIndexerEstimator(inputCol = feature, outputCol = s"${feature}Index")
  }
  val categoricalOneHots = categoricalIndexers.map {
    indexer =>
      OneHotEncoderEstimator(inputCol = indexer.outputCol, outputCol = s"${indexer.inputCol}OneHot")
  }
  val categoricalVectorCols = categoricalOneHots.map(_.outputCol)
  val categoricalFeatureAssembler = VectorAssemblerEstimator(inputCols = categoricalVectorCols,
    outputCol = "categoricalFeatures")
  val featureAssembler = VectorAssemblerEstimator(inputCols = Seq(continuousScaler.outputCol,
    categoricalFeatureAssembler.outputCol), outputCol = "features")

  // Use the feature vector we just created to train a linear regression.
  // Our dependent variable is "listingOverMsrp", which is a ratio of the listing
  // price to the manufacturer price of the vehicle.
  val linearRegression = LinearRegressionEstimator(featuresCol = featureAssembler.outputCol,
    labelCol = "listingOverMsrp",
    predictionCol = "listingOverMsrpPrediction")

  // Create the feature pipeline which performs all the scaling, indexing, one-hot encoding.
  val featureEstimators = Seq(continuousFeatureAssembler, continuousScaler)
    .union(categoricalIndexers)
    .union(categoricalOneHots)
    .union(Seq(categoricalFeatureAssembler, featureAssembler))
  val featurePipeline = PipelineEstimator(estimators = featureEstimators)

  // Save our estimators to files so we can modify them easily or use them later.
  featurePipeline.serializeToFile(new File("/tmp/feature_pipeline_estimator.json"))
  linearRegression.serializeToFile(new File("/tmp/regression_estimator.json"))

  /*
  2. Training the ML Pipeline

  To accomplish this we will setup a SQLContext to load our data with,
  train our feature pipeline, use the resulting model to generate the
  training dataset for our linear regression, then train our linear regression.
  We will save our estimator pipelines and their resulting models to JSON files
  along the way.
   */

  // Setup a SQLContext so we can load our listing price data
  val conf = new SparkConf().setAppName("MLeap Linear Regression Example")
    .setMaster("local[2]")
  val sparkContext = new SparkContext(conf)
  val sqlContext = new SQLContext(sparkContext)

  // Load our full listing price dataset with features and dependent variable.
  // Split the full dataset into a training dataset and a validation dataset
  val fullDataset = sqlContext.read.format("com.databrick.spark.avro").load("/tmp/listing_prices.avro")
  val Array(trainingDataset, validationDataset) = fullDataset.randomSplit(Array(0.7, 0.3))

  // Train the feature pipeline on the full dataset so that it learns ALL of the
  // categories for our categorical variables.
  val sparkFeaturePipelineModel = featurePipeline.sparkEstimate(fullDataset)
  val mleapFeaturePipelineModel: Transformer = sparkFeaturePipelineModel

  // Transform our training dataset to include the "features" vector for traininig the linear regression.
  val preparedTrainingDataset = sparkFeaturePipelineModel.transform(trainingDataset)

  // Train our linear regression on the training dataset.
  val mleapLinearRegressionModel = linearRegression.estimate(preparedTrainingDataset)

  // Combine the feature pipeline with the linear regression model for the complete pipeline
  val fullPipeline: Transformer = PipelineModel(stages = Seq(mleapFeaturePipelineModel, mleapLinearRegressionModel))

  // Save our full pipeline to a JSON file.
  fullPipeline.serializeToFile(new File("/tmp/pipeline.json"))

  /*
  3. Scoring With a Spark DataFrame

  To accomplish this we will use our validation dataset as a test subject.
  We will use our fullPipeline object to score the records in the validation
  dataset.
   */

  // Score the Spark DataFrame using our MLeap pipeline.
  val sparkScoredValidationDataset = fullPipeline.sparkTransform(validationDataset)

  /*
  4. Scoring Without a Spark DataFrame

  To accomplish this, we will use a utility method from MLeap to construct a
  LocalLeapFrame from a Map[String, Any]. Note that scoring this way has
  NO dependencies on Spark.
   */

  // Create our feature set that we want to predict.
  val featuresToPredict = Map(
    "mileage" -> 15000,
    "averageListingPrice" -> 12000,
    "modelId" -> "Honda",
    "modelAge" -> 6,
    "condition" -> "Good"
  )

  // Turn our map of features into a LeapFrame.
  val frame = LeapFrame.fromMap(featuresToPredict)

  // Transform the LeapFrame using our ML pipeline.
  val transformedFrame = fullPipeline.transform(frame).get

  // Extract the prediction.
  val prediction = transformedFrame.getDouble(transformedFrame.getRow(0), "listOverMsrpPrediction")

  println(s"Predicted value: $prediction")
}
```

## Supported Estimators/Transformers

Currently MLeap only supports a select set of estimators/transformers in Spark as a proof of concept.

### Feature

* StringIndexer
* OneHotEncoder
* VectorAssembler
* StandardScaler

### Regression

* LinearRegression
* RandomForestRegressor

### Miscellaneous

* Pipeline

## Future of MLeap

It would be great to guarantee that executing a Spark Transformer is equivalent to executing an MLeap Transformer. In order to absolutely guarantee this, there are two options:

1. Use the same underlying runtime in MLeap as in Spark. In this case, we would modify Spark Transformers to use the obects in mleap-core for execution.
2. Have Spark Estimators train MLeap Transformers.

The 2nd option is the most desirable, as there is then no need for the conversion libraries from Spark to MLeap. This option is also a large undertaking, as MLeap Transformers only understand doubles, strings, and vectors. They also do not use meta data to aid in their execution, which certain Spark Transformers depend on. 

The easier, perhaps more natural route to take would be to implement option 1 first. Then, once option 1 is implemented and merged into Spark master, start working on option 2 to complete the unification of ML pipeline execution.

## Contributing

There are a few ways to contribute to MLeap.

* Write documentation. As you can see looking through the source code, there is very little.
* Contribute an Estimator/Transformer from Spark.
* Use MLeap at your company and tell us what you think.
* Make a feature request or report a bug in github.
* Make a pull request for an existing feature request or bug report.
* Join the discussion of how to get MLeap into Spark as a dependency. Our gitter is here: (insert gitter link here)

## Contact Information

* Hollin Wilkins (hollinrwilkins@gmail.com)
* Mikhail Semeniuk (seme0021@gmail.com)
* Ram Sriharsha (rsriharsha@hortonworks.com)


