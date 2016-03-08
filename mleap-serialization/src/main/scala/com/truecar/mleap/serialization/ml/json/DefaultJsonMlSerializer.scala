package com.truecar.mleap.serialization.ml.json

import com.truecar.mleap.bundle.MleapSerializer
import com.truecar.mleap.runtime.transformer._
import com.truecar.mleap.bundle.core.json.JsonStreamSerializer.ImplicitJsonStreamSerializer
import com.truecar.mleap.serialization.ConversionSerializer
import com.truecar.mleap.serialization.ml.bundle.core.regression.{RandomForestRegressionSerializer, DecisionTreeRegressionSerializer}
import com.truecar.mleap.serialization.ml.bundle.core.tree.node.LinearNodeSerializer
import com.truecar.mleap.serialization.ml.bundle.runtime.PipelineModelSerializer
import com.truecar.mleap.serialization.ml.bundle.runtime.regression.RandomForestRegressionModelSerializer
import com.truecar.mleap.serialization.ml.Converters._
import com.truecar.mleap.bundle.core.json.MlJsonSupport._
import ml.runtime.{feature, regression}

/**
  * Created by hwilkins on 3/5/16.
  */
object DefaultJsonMlSerializer {
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

    serializer.addSerializer(ConversionSerializer[StringIndexerModel, feature.StringIndexerModel.StringIndexerModel](mlStringIndexerModelFormat))
    serializer.addSerializer(ConversionSerializer[HashingTermFrequencyModel, feature.HashingTermFrequencyModel.HashingTermFrequencyModel](mlHashingTermFrequencyModelFormat))
    serializer.addSerializer(ConversionSerializer[StandardScalerModel, feature.StandardScalerModel.StandardScalerModel](mlStandardScalerModelFormat))
    serializer.addSerializer(ConversionSerializer[VectorAssemblerModel, feature.VectorAssemblerModel.VectorAssemblerModel](mlVectorAssemblerModelFormat))
    serializer.addSerializer(ConversionSerializer[TokenizerModel, feature.TokenizerModel.TokenizerModel](mlTokenizerModelFormat))
    serializer.addSerializer(ConversionSerializer[LinearRegressionModel, regression.LinearRegressionModel.LinearRegressionModel](mlLinearRegressionModelFormat))

    serializer.addBundleSerializer(randomForestRegressionModelSerializer)
    serializer.addBundleSerializer(PipelineModelSerializer(serializer))

    serializer
  }
}
