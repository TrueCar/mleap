package com.truecar.mleap.serialization.json

import com.truecar.mleap.runtime.transformer._
import com.truecar.mleap.bundle.core.MleapSerializer
import com.truecar.mleap.serialization.serializer.bundle.core.regression.{RandomForestRegressionSerializer, DecisionTreeRegressionSerializer}
import com.truecar.mleap.serialization.serializer.bundle.core.tree.node.LinearNodeSerializer
import com.truecar.mleap.serialization.serializer.bundle.runtime.PipelineModelSerializer
import com.truecar.mleap.serialization.serializer.bundle.runtime.regression.RandomForestRegressionModelSerializer
import com.truecar.mleap.serialization.MleapToMlSerializer
import com.truecar.mleap.serialization.Converters._
import com.truecar.mleap.bundle.core.json.MlJsonSupport._
import ml.runtime.{feature, regression}

/**
  * Created by hwilkins on 3/5/16.
  */
object DefaultJsonMleapSerializer {
  val decisionTreeRegressionSerializer = DecisionTreeRegressionSerializer(LinearNodeSerializer(
    mlNodeMetaDataFormat,
    mlNodeDataFormat
  ))
  val randomForestRegressionSerializer = RandomForestRegressionSerializer(mlRandomForestRegressionMetaDataFormat,
    decisionTreeRegressionSerializer)
  val randomForestRegressionModelSerializer = RandomForestRegressionModelSerializer(mlRandomForestRegressionModelMetaDataFormat,
    randomForestRegressionSerializer)

  def createSerializer(): MleapSerializer = {
    val serializer = new MleapSerializer()

    serializer.addSerializer(MleapToMlSerializer[StringIndexerModel, feature.StringIndexerModel.StringIndexerModel](mlStringIndexerModelFormat))
    serializer.addSerializer(MleapToMlSerializer[HashingTermFrequencyModel, feature.HashingTermFrequencyModel.HashingTermFrequencyModel](mlHashingTermFrequencyModelFormat))
    serializer.addSerializer(MleapToMlSerializer[StandardScalerModel, feature.StandardScalerModel.StandardScalerModel](mlStandardScalerModelFormat))
    serializer.addSerializer(MleapToMlSerializer[VectorAssemblerModel, feature.VectorAssemblerModel.VectorAssemblerModel](mlVectorAssemblerModelFormat))
    serializer.addSerializer(MleapToMlSerializer[TokenizerModel, feature.TokenizerModel.TokenizerModel](mlTokenizerModelFormat))
    serializer.addSerializer(MleapToMlSerializer[LinearRegressionModel, regression.LinearRegressionModel.LinearRegressionModel](mlLinearRegressionModelFormat))

    serializer.addBundleSerializer(randomForestRegressionModelSerializer)
    serializer.addBundleSerializer(PipelineModelSerializer(serializer))

    serializer
  }
}
