# MLeap

[![Join the chat at https://gitter.im/TrueCar/mleap](https://badges.gitter.im/TrueCar/mleap.svg)](https://gitter.im/TrueCar/mleap?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

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

MLeap is broken into 3 modules:

1. mleap-core - Core execution building blocks, includes runtime for executing linear regressions, random forest models, logisitic regressions, assembling feature vectors, string indexing, one hot encoding, etc. It provides a core linear algebra system for all of these tasks.
2. mleap-runtime - Provides LeapFrame, which is essentially a lightweight DataFrame without any dependencies on the Spark libraries. LeapFrames support 3 data types: double, string, and vector. Also provides MLeap Transformers for executing ML pipelines on LeapFrames. Spark ML pipelines get converted to MLeap pipelines which are provided with this library. This module provides serialization for all pipeline obects. This module also includes serializable case class estimators for all supported transformers.
3. mleap-spark - Provides Spark/MLeap integration. SparkLeapFrame is an implementation of LeapFrame with a Spark RDD backing the data so you can execute MLeap transformers on a Spark cluster. Provides conversion from Spark Transformers to MLeap Transformers. Provides conversion from MLeap Estimators to Spark Estimators. This allows a very intuitive usage of MLeap without worrying about how Spark is being used under the hood: MLeap Estimator -> Spark Estimator -> Spark Transformer -> MLeap Transformer.

## Example

Please see the [mleap-demo](https://github.com/TrueCar/mleap-demo) project for an example of building and using a pipeline with MLeap.

## Supported Estimators/Transformers

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
