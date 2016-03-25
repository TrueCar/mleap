package com.truecar.mleap.serialization.ml.v1

import com.truecar.mleap.core.tree.Node
import com.truecar.mleap.runtime.transformer._
import ml.bundle.support.v1.runtime.PipelineModelSerializer
import ml.bundle.{StreamSerializer, BundleSerializer, Serializer}
import Converters._
import ml.bundle.support.ConversionSerializer._
import ml.bundle.support.JsonStreamSerializer._
import ml.bundle.support.v1.core.regression.{DecisionTreeRegressionSerializer, RandomForestRegressionSerializer}
import ml.bundle.support.v1.core.tree.node.LinearNodeSerializer
import ml.bundle.support.v1.runtime
import ml.bundle.support.v1.runtime.regression.RandomForestRegressionModelSerializer
import ml.bundle.support.v1.json.MlJsonSupport._
import ml.bundle.v1.runtime.{feature, regression}

/**
  * Created by hollinwilkins on 3/8/16.
  */
trait MlJsonSerializer extends Serializer {
  // regression

  val bundleNodeSerializer = LinearNodeSerializer(mlNodeMetaDataFormat, mlNodeDataFormat)
  val decisionTreeRegressionSerializer = DecisionTreeRegressionSerializer(bundleNodeSerializer)
  val randomForestRegressionSerializer = RandomForestRegressionSerializer(mlRandomForestRegressionMetaDataFormat,
    decisionTreeRegressionSerializer)
  val randomForestRegressionModelSerializer: BundleSerializer[RandomForestRegressionModel] = conversionSerializer[RandomForestRegressionModel, runtime.regression.RandomForestRegressionModel[Node]](RandomForestRegressionModelSerializer(mlRandomForestRegressionModelMetaDataFormat,
    randomForestRegressionSerializer))
  val linearRegressionModelSerializer: StreamSerializer[LinearRegressionModel] = conversionSerializer[LinearRegressionModel, regression.LinearRegressionModel.LinearRegressionModel](mlLinearRegressionModelFormat)

  addSerializer(randomForestRegressionModelSerializer)
  addSerializer(linearRegressionModelSerializer)

  // feature
  val hashingTermFrequencyModelSerializer: StreamSerializer[HashingTermFrequencyModel] = conversionSerializer[HashingTermFrequencyModel, feature.HashingTermFrequencyModel.HashingTermFrequencyModel](mlHashingTermFrequencyModelFormat)
  val standardScalerModelSerializer: StreamSerializer[StandardScalerModel] = conversionSerializer[StandardScalerModel, feature.StandardScalerModel.StandardScalerModel](mlStandardScalerModelFormat)
  val stringIndexerModelSerializer: StreamSerializer[StringIndexerModel] = conversionSerializer[StringIndexerModel, feature.StringIndexerModel.StringIndexerModel](mlStringIndexerModelFormat)
  val tokenizerModelSerializer: StreamSerializer[TokenizerModel] = conversionSerializer[TokenizerModel, feature.TokenizerModel.TokenizerModel](mlTokenizerModelFormat)
  val vectorAssemblerModelSerializer: StreamSerializer[VectorAssemblerModel] = conversionSerializer[VectorAssemblerModel, feature.VectorAssemblerModel.VectorAssemblerModel](mlVectorAssemblerModelFormat)

  addSerializer(hashingTermFrequencyModelSerializer)
  addSerializer(standardScalerModelSerializer)
  addSerializer(stringIndexerModelSerializer)
  addSerializer(tokenizerModelSerializer)
  addSerializer(vectorAssemblerModelSerializer)

  // pipeline

  val pipelineModelSerializer: BundleSerializer[PipelineModel] = conversionSerializer[PipelineModel, runtime.PipelineModel](PipelineModelSerializer(this,
    mlPipelineModelMetaDataFormat))

  addSerializer(pipelineModelSerializer)
}
object MlJsonSerializer extends MlJsonSerializer {
  val supportedVersions = Set("0.1-SNAPSHOT")

  override val namespace: String = "ml.bundle.json"
  override val version: String = "0.1-SNAPSHOT"

  override def isCompatibleVersion(otherVersion: String): Boolean = supportedVersions.contains(otherVersion)
}
