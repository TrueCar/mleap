package com.truecar.mleap.serialization.ml.json

import ml.bundle.Serializer
import com.truecar.mleap.runtime.transformer._
import ml.bundle.json.JsonStreamSerializer.ImplicitJsonStreamSerializer
import com.truecar.mleap.serialization.ConversionSerializer
import com.truecar.mleap.serialization.ml.bundle.core.regression.{RandomForestRegressionSerializer, DecisionTreeRegressionSerializer}
import com.truecar.mleap.serialization.ml.bundle.core.tree.node.LinearNodeSerializer
import com.truecar.mleap.serialization.ml.bundle.runtime.PipelineModelSerializer
import com.truecar.mleap.serialization.ml.bundle.runtime.regression.RandomForestRegressionModelSerializer
import com.truecar.mleap.serialization.ml.Converters._
import ml.bundle.json.MlJsonSupport._
import ml.runtime.{feature, regression}

/**
  * Created by hwilkins on 3/8/16.
  */
trait MlSimpleJsonSerializer extends Serializer {
  val decisionTreeRegressionSerializer = DecisionTreeRegressionSerializer(LinearNodeSerializer(
    mlNodeMetaDataFormat,
    mlNodeDataFormat
  ))
  val randomForestRegressionSerializer = RandomForestRegressionSerializer(mlRandomForestRegressionMetaDataFormat,
    decisionTreeRegressionSerializer)
  val randomForestRegressionModelSerializer = RandomForestRegressionModelSerializer(mlRandomForestRegressionModelMetaDataFormat,
    randomForestRegressionSerializer)

  addSerializer(ConversionSerializer[StringIndexerModel, feature.StringIndexerModel.StringIndexerModel](mlStringIndexerModelFormat))
  addSerializer(ConversionSerializer[HashingTermFrequencyModel, feature.HashingTermFrequencyModel.HashingTermFrequencyModel](mlHashingTermFrequencyModelFormat))
  addSerializer(ConversionSerializer[StandardScalerModel, feature.StandardScalerModel.StandardScalerModel](mlStandardScalerModelFormat))
  addSerializer(ConversionSerializer[VectorAssemblerModel, feature.VectorAssemblerModel.VectorAssemblerModel](mlVectorAssemblerModelFormat))
  addSerializer(ConversionSerializer[TokenizerModel, feature.TokenizerModel.TokenizerModel](mlTokenizerModelFormat))
  addSerializer(ConversionSerializer[LinearRegressionModel, regression.LinearRegressionModel.LinearRegressionModel](mlLinearRegressionModelFormat))

  addBundleSerializer(randomForestRegressionModelSerializer)
  addBundleSerializer(PipelineModelSerializer(this))
}
object MlSimpleJsonSerializer extends MlSimpleJsonSerializer
