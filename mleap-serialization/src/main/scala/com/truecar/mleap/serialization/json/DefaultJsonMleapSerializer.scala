package com.truecar.mleap.serialization.json

import com.truecar.mleap.serialization.serializer.bundle.core.regression.{RandomForestRegressionSerializer, DecisionTreeRegressionSerializer}
import com.truecar.mleap.serialization.serializer.bundle.core.tree.node.LinearNodeSerializer
import com.truecar.mleap.serialization.serializer.bundle.runtime.PipelineModelSerializer
import com.truecar.mleap.serialization.serializer.bundle.runtime.regression.RandomForestRegressionModelSerializer
import com.truecar.mleap.serialization.serializer.stream.json.core.regression.RandomForestMetaDataSerializer
import com.truecar.mleap.serialization.serializer.stream.json.core.tree.node.{NodeMetaDataSerializer, NodeDataSerializer}
import com.truecar.mleap.serialization.serializer.stream.json.runtime.feature._
import com.truecar.mleap.serialization.MleapSerializer
import com.truecar.mleap.serialization.serializer.stream.json.runtime.regression.{RandomForestRegressionModelMetaDataSerializer, LinearRegressionModelSerializer}

/**
  * Created by hwilkins on 3/5/16.
  */
object DefaultJsonMleapSerializer {
  val decisionTreeRegressionSerializer = DecisionTreeRegressionSerializer(LinearNodeSerializer(
    NodeMetaDataSerializer,
    NodeDataSerializer
  ))
  val randomForestMetaDataSerializer = RandomForestMetaDataSerializer
  val randomForestRegressionSerializer = RandomForestRegressionSerializer(randomForestMetaDataSerializer,
    decisionTreeRegressionSerializer)
  val randomForestRegressionModelMetaDataSerializer = RandomForestRegressionModelMetaDataSerializer
  val randomForestRegressionModelSerializer = RandomForestRegressionModelSerializer(randomForestRegressionModelMetaDataSerializer,
    randomForestRegressionSerializer)

  def createSerializer(): MleapSerializer = {
    val serializer = new MleapSerializer()

    serializer.addSerializer(HashingTermFrequencySerializer)
    serializer.addSerializer(StandardScalerModelSerializer)
    serializer.addSerializer(StringIndexerModelSerializer)
    serializer.addSerializer(TokenizerModelSerializer)
    serializer.addSerializer(VectorAssemblerModelSerializer)

    serializer.addSerializer(LinearRegressionModelSerializer)

    serializer.addBundleSerializer(randomForestRegressionModelSerializer)
    serializer.addBundleSerializer(PipelineModelSerializer(serializer))

    serializer
  }
}
