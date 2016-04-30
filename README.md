# MLeap

[![Join the chat at https://gitter.im/TrueCar/mleap](https://badges.gitter.im/TrueCar/mleap.svg)](https://gitter.im/TrueCar/mleap?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Easily put your Spark ML Pipelines into action with MLeap. Train your feature and regression/classification pipeline with Spark then easily convert them to MLeap pipelines to deploy them anywhere. Take your pipelines to an API server, Hadoop, or even back to Spark to execute on a DataFrame.

MLeap allows for easy serialization of your estimator and transformer pipelines so you can save them for reuse later. Executing an MLeap pipeline does not require a SparkContext or DataFrame so there is very little overhead for realtime one-off predictions. You don't have to worry about Spark dependencies for executing your models, just add the lightweight MLeap runtime library instead.

MLeap makes deploying your Spark ML pipelines with 3 core functions:

1. Release: Deploy your entire ML pipeline without a SparkContext or any dependency on Spark libraries.
2. Reuse: Export your ML pipeline to easy-to-read JSON files so you can reuse pipelines.
3. Recycle: Export your training pipelines to easy-to-read JSON files so you can easily modify your training pipelines.

## Setup

### Link with Maven or SBT

MLeap is cross-compiled for Scala 2.10 and 2.11, so just replace 2.10 with 2.11 wherever you see it if you are running Scala version 2.11 and using a POM file for dependency management. Otherwise, use the `%%` operator if you are using SBT and the correct Scala version will be used.

#### SBT

```
libraryDependencies += "com.truecar.mleap" %% "mleap-runtime" % "0.1.4"
```

#### Maven

```
<dependency>
    <groupId>com.truecar.mleap</groupId>
    <artifactId>mleap-runtime_2.10</artifactId>
    <version>0.1.4</version>
</dependency>
```

### For Spark Integration

#### SBT

```
libraryDependencies += "com.truecar.mleap" %% "mleap-spark" % "0.1.4"
```

#### Maven

```
<dependency>
    <groupId>com.truecar.mleap</groupId>
    <artifactId>mleap-spark_2.10</artifactId>
    <version>0.1.4</version>
</dependency>
```

### Spark Packages

MLeap is now a [Spark Package](http://spark-packages.org/package/TrueCar/mleap). The package includes `mleap-spark` and `mleap-serialization`, so you should have full functionality with it. Here is how you can run a Spark shell with MLeap loaded.

```bash
$ bin/spark-shell --packages com.truecar.mleap:mleap-package_2.10:0.1.4
```

## Modules

MLeap is broken into 4 modules:

1. mleap-core - Core execution building blocks, includes runtime for executing linear regressions, random forest models, logisitic regressions, assembling feature vectors, string indexing, one hot encoding, etc. It provides a core linear algebra system for all of these tasks.
2. mleap-runtime - Provides LeapFrame, which is essentially a lightweight DataFrame without any dependencies on the Spark libraries. LeapFrames support 3 data types: double, string, and vector. Also provides MLeap Transformers for executing ML pipelines on LeapFrames. Spark ML pipelines get converted to MLeap pipelines which are provided with this library.
3. mleap-spark - Provides Spark/MLeap integration. SparkLeapFrame is an implementation of LeapFrame with a Spark RDD backing the data so you can execute MLeap transformers on a Spark cluster. Provides conversion from Spark Transformers to MLeap Transformers. Provides conversion from MLeap Estimators to Spark Estimators. This allows a very intuitive usage of MLeap without worrying about how Spark is being used under the hood: MLeap Estimator -> Spark Estimator -> Spark Transformer -> MLeap Transformer.
4. mleap-serialization - Provides serialization for MLeap and Spark models to common JSON/Protobuf format.

## Example

Please see the [mleap-demo](https://github.com/TrueCar/mleap-demo) project for an example of building and using a pipeline with MLeap.

## Supported Transformers

Currently MLeap only supports a select set of estimators/transformers in Spark as a proof of concept.

### Feature

* StringIndexer
* Tokenizer
* HashingTF
* VectorAssembler
* StandardScaler

### Regression

* LinearRegression
* RandomForestRegressor

### Classification

* RandomForestClassification
* SupportVectorMachine (Must Use Estimator Provided with mleap-spark)

### Miscellaneous

* Pipeline

## Future of MLeap

1. Provide Python/R bindings
2. Unify linear algebra and core ML models library with Spark
3. Deploy outside of the JVM to embedded systems
4. Full support for all Spark transformers

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
* Ram Sriharsha (ram@databricks.com)

## License

See LICENSE and NOTICE file in this repository.

Copyright 2016 TrueCar, inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
