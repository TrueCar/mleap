package org.apache.spark.ml.mleap.bundle

import ml.bundle.support.v1.runtime
import ml.bundle.support.v1.runtime.PipelineModelSerializer
import ml.bundle.v1.runtime.feature.HashingTermFrequencyModel.HashingTermFrequencyModel
import ml.bundle.v1.runtime.feature.StandardScalerModel.StandardScalerModel
import ml.bundle.v1.runtime.feature.StringIndexerModel.StringIndexerModel
import ml.bundle.v1.runtime.feature.TokenizerModel.TokenizerModel
import ml.bundle.v1.runtime.feature.VectorAssemblerModel.VectorAssemblerModel
import ml.bundle.v1.runtime.regression.LinearRegressionModel.LinearRegressionModel
import ml.bundle.{BundleSerializer, Serializer, StreamSerializer}
import ml.bundle.support.v1.core.regression.{DecisionTreeRegressionSerializer, RandomForestRegressionSerializer}
import ml.bundle.support.v1.core.tree.node.LinearNodeSerializer
import ml.bundle.support.v1.runtime.regression.{RandomForestRegressionModel, RandomForestRegressionModelSerializer}
import org.apache.spark.ml.mleap.bundle.Converters._
import ml.bundle.support.ConversionSerializer._
import ml.bundle.support.v1.core.classification.{DecisionTreeClassificationSerializer, RandomForestClassificationSerializer}
import ml.bundle.support.v1.json.MlJsonSerializerSupport._
import ml.bundle.support.v1.runtime.classification.{RandomForestClassificationModel, RandomForestClassificationModelSerializer}
import ml.bundle.v1.runtime.classification.LogisticRegressionModel.LogisticRegressionModel
import ml.bundle.v1.runtime.classification.SupportVectorMachineModel.SupportVectorMachineModel
import ml.bundle.v1.runtime.feature.OneHotEncoderModel.OneHotEncoderModel
import ml.bundle.v1.runtime.feature.ReverseStringIndexerModel.ReverseStringIndexerModel
import org.apache.spark.ml.mleap.classification.SVMModel
import org.apache.spark.ml._
import org.apache.spark.ml.tree.Node

/**
  * Created by hollinwilkins on 3/28/16.
  */
trait MlJsonSerializer extends Serializer {
  // regression

  val regressionBundleNodeSerializer = LinearNodeSerializer(mlNodeMetaDataSerializer, mlNodeDataSerializer, includeImpurityStats = false)
  val decisionTreeRegressionSerializer = DecisionTreeRegressionSerializer(mlDecisionTreeMetaDataSerializer, regressionBundleNodeSerializer)
  val randomForestRegressionSerializer = RandomForestRegressionSerializer(mlRandomForestMetaDataSerializer,
    decisionTreeRegressionSerializer)
  val randomForestRegressionModelSerializer: BundleSerializer[regression.RandomForestRegressionModel] = conversionSerializer[regression.RandomForestRegressionModel, RandomForestRegressionModel[Node]](RandomForestRegressionModelSerializer(mlRandomForestRegressionModelMetaDataSerializer,
    randomForestRegressionSerializer))
  val linearRegressionModelSerializer: StreamSerializer[regression.LinearRegressionModel] = conversionSerializer[regression.LinearRegressionModel, LinearRegressionModel](mlLinearRegressionModelSerializer)

  addSerializer(randomForestRegressionModelSerializer)
  addSerializer(linearRegressionModelSerializer)

  // classification
  val classificationBundleNodeSerializer = LinearNodeSerializer(mlNodeMetaDataSerializer, mlNodeDataSerializer, includeImpurityStats = true)
  val decisionTreeClassificationSerializer = DecisionTreeClassificationSerializer(mlDecisionTreeClassificationMetaDataSerializer, classificationBundleNodeSerializer)
  val randomForestClassificationSerializer = RandomForestClassificationSerializer(mlRandomForestClassificationMetaDataSerializer,
    decisionTreeClassificationSerializer)
  val randomForestClassificationModelSerializer: BundleSerializer[classification.RandomForestClassificationModel] = conversionSerializer[classification.RandomForestClassificationModel, RandomForestClassificationModel[Node]](RandomForestClassificationModelSerializer(mlRandomForestClassificationModelMetaDataSerializer,
    randomForestClassificationSerializer))
  val logisticRegressionModelSerializer: StreamSerializer[classification.LogisticRegressionModel] = conversionSerializer[classification.LogisticRegressionModel, LogisticRegressionModel](mlLogisticRegressionModelSerializer)
  val supportVectorMachineModelSerializer: StreamSerializer[SVMModel] = conversionSerializer[SVMModel, SupportVectorMachineModel](mlSupportVectorMachineModelSerializer)

  addSerializer(logisticRegressionModelSerializer)
  addSerializer(supportVectorMachineModelSerializer)
  addSerializer(randomForestClassificationModelSerializer)

  // feature
  val oneHotEncoderModelSerializer: StreamSerializer[mleap.feature.OneHotEncoderModel] = conversionSerializer[mleap.feature.OneHotEncoderModel, OneHotEncoderModel](mlOneHotEncoderModelSerializer)
  val hashingTermFrequencyModelSerializer: StreamSerializer[feature.HashingTF] = conversionSerializer[feature.HashingTF, HashingTermFrequencyModel](mlHashingTermFrequencyModelSerializer)
  val standardScalerModelSerializer: StreamSerializer[feature.StandardScalerModel] = conversionSerializer[feature.StandardScalerModel, StandardScalerModel](mlStandardScalerModelSerializer)
  val stringIndexerModelSerializer: StreamSerializer[feature.StringIndexerModel] = conversionSerializer[feature.StringIndexerModel, StringIndexerModel](mlStringIndexerModelSerializer)
  val reverseStringIndexerModelSerializer: StreamSerializer[feature.IndexToString] = conversionSerializer[feature.IndexToString, ReverseStringIndexerModel](mlReverseStringIndexerModelSerializer)
  val tokenizerModelSerializer: StreamSerializer[feature.Tokenizer] = conversionSerializer[feature.Tokenizer, TokenizerModel](mlTokenizerModelSerializer)
  val vectorAssemblerModelSerializer: StreamSerializer[feature.VectorAssembler] = conversionSerializer[feature.VectorAssembler, VectorAssemblerModel](mlVectorAssemblerModelSerializer)

  addSerializer(oneHotEncoderModelSerializer)
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
  val supportedVersions = Set("0.1.1")

  override val version: String = "0.1.1"

  override def isCompatibleVersion(otherVersion: String): Boolean = supportedVersions.contains(otherVersion)
}
