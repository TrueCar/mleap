package com.truecar.mleap.serialization.ml.v1

import com.truecar.mleap.core.tree.Node
import com.truecar.mleap.runtime.transformer._
import ml.bundle.support.v1.runtime.PipelineModelSerializer
import ml.bundle.{BundleSerializer, Serializer, StreamSerializer}
import Converters._
import ml.bundle.support.ConversionSerializer._
import ml.bundle.support.v1.core.classification.{DecisionTreeClassificationSerializer, RandomForestClassificationSerializer}
import ml.bundle.support.v1.json.MlJsonSerializerSupport._
import ml.bundle.support.v1.core.regression.{DecisionTreeRegressionSerializer, RandomForestRegressionSerializer}
import ml.bundle.support.v1.core.tree.node.LinearNodeSerializer
import ml.bundle.support.v1.runtime
import ml.bundle.support.v1.runtime.classification.RandomForestClassificationModelSerializer
import ml.bundle.support.v1.runtime.regression.RandomForestRegressionModelSerializer
import ml.bundle.v1.runtime.{feature, regression, classification}

/**
  * Created by hollinwilkins on 3/8/16.
  */
trait MlJsonSerializer extends Serializer {
  // regression

  val regressionBundleNodeSerializer = LinearNodeSerializer(mlNodeMetaDataSerializer, mlNodeDataSerializer, includeImpurityStats = false)
  val decisionTreeRegressionSerializer = DecisionTreeRegressionSerializer(mlDecisionTreeMetaDataSerializer, regressionBundleNodeSerializer)
  val randomForestRegressionSerializer = RandomForestRegressionSerializer(mlRandomForestMetaDataSerializer,
    decisionTreeRegressionSerializer)
  val randomForestRegressionModelSerializer: BundleSerializer[RandomForestRegressionModel] = conversionSerializer[RandomForestRegressionModel, runtime.regression.RandomForestRegressionModel[Node]](
    RandomForestRegressionModelSerializer(mlRandomForestRegressionModelMetaDataSerializer,
    randomForestRegressionSerializer))
  val linearRegressionModelSerializer: StreamSerializer[LinearRegressionModel] = conversionSerializer[LinearRegressionModel, regression.LinearRegressionModel.LinearRegressionModel](mlLinearRegressionModelSerializer)

  addSerializer(randomForestRegressionModelSerializer)
  addSerializer(linearRegressionModelSerializer)

  // classification

  val classificationBundleNodeSerializer = LinearNodeSerializer(mlNodeMetaDataSerializer, mlNodeDataSerializer, includeImpurityStats = true)
  val decisionTreeClassificationSerializer = DecisionTreeClassificationSerializer(mlDecisionTreeClassificationMetaDataSerializer, classificationBundleNodeSerializer)
  val randomForestClassificationSerializer = RandomForestClassificationSerializer(mlRandomForestClassificationMetaDataSerializer,
    decisionTreeClassificationSerializer)
  val randomForestClassificationModelSerializer: BundleSerializer[RandomForestClassificationModel] = conversionSerializer[RandomForestClassificationModel, runtime.classification.RandomForestClassificationModel[Node]](
    RandomForestClassificationModelSerializer(mlRandomForestClassificationModelMetaDataSerializer,
    randomForestClassificationSerializer))
  val supportVectorMachineModelSerializer: StreamSerializer[SupportVectorMachineModel] = conversionSerializer[SupportVectorMachineModel, classification.SupportVectorMachineModel.SupportVectorMachineModel](mlSupportVectorMachineModelSerializer)

  addSerializer(supportVectorMachineModelSerializer)
  addSerializer(randomForestClassificationModelSerializer)

  // feature
  val hashingTermFrequencyModelSerializer: StreamSerializer[HashingTermFrequencyModel] = conversionSerializer[HashingTermFrequencyModel, feature.HashingTermFrequencyModel.HashingTermFrequencyModel](mlHashingTermFrequencyModelSerializer)
  val standardScalerModelSerializer: StreamSerializer[StandardScalerModel] = conversionSerializer[StandardScalerModel, feature.StandardScalerModel.StandardScalerModel](mlStandardScalerModelSerializer)
  val stringIndexerModelSerializer: StreamSerializer[StringIndexerModel] = conversionSerializer[StringIndexerModel, feature.StringIndexerModel.StringIndexerModel](mlStringIndexerModelSerializer)
  val reverseStringIndexerModelSerializer: StreamSerializer[ReverseStringIndexerModel] = conversionSerializer[ReverseStringIndexerModel, feature.ReverseStringIndexerModel.ReverseStringIndexerModel](mlReverseStringIndexerModelSerializer)
  val tokenizerModelSerializer: StreamSerializer[TokenizerModel] = conversionSerializer[TokenizerModel, feature.TokenizerModel.TokenizerModel](mlTokenizerModelSerializer)
  val vectorAssemblerModelSerializer: StreamSerializer[VectorAssemblerModel] = conversionSerializer[VectorAssemblerModel, feature.VectorAssemblerModel.VectorAssemblerModel](mlVectorAssemblerModelSerializer)

  addSerializer(hashingTermFrequencyModelSerializer)
  addSerializer(standardScalerModelSerializer)
  addSerializer(stringIndexerModelSerializer)
  addSerializer(reverseStringIndexerModelSerializer)
  addSerializer(tokenizerModelSerializer)
  addSerializer(vectorAssemblerModelSerializer)

  // pipeline

  val pipelineModelSerializer: BundleSerializer[PipelineModel] = conversionSerializer[PipelineModel, runtime.PipelineModel](PipelineModelSerializer(this,
    mlPipelineModelMetaDataSerializer))

  addSerializer(pipelineModelSerializer)
}
object MlJsonSerializer extends MlJsonSerializer {
  val supportedVersions = Set("0.1-SNAPSHOT")

  override val namespace: String = "ml.bundle.json"
  override val version: String = "0.1-SNAPSHOT"

  override def isCompatibleVersion(otherVersion: String): Boolean = supportedVersions.contains(otherVersion)
}
