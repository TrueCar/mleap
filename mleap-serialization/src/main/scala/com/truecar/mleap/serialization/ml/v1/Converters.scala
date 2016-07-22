package com.truecar.mleap.serialization.ml.v1

import com.truecar.mleap.core._
import com.truecar.mleap.runtime.transformer
import com.truecar.mleap.runtime.transformer.Transformer
import ml.bundle.support.v1.core.classification.{DecisionTreeClassification, RandomForestClassification}
import ml.bundle.support.v1.core.regression.{DecisionTreeRegression, RandomForestRegression}
import ml.bundle.support.v1.runtime.PipelineModel
import ml.bundle.support.v1.runtime.regression.RandomForestRegressionModel
import ml.bundle.support.v1.core.tree.node.Node
import ml.bundle.support.v1.runtime.classification.RandomForestClassificationModel
import ml.bundle.v1.core.classification.LogisticRegression.LogisticRegression
import ml.bundle.v1.core.classification.SupportVectorMachine.SupportVectorMachine
import ml.bundle.v1.core.feature.HashingTermFrequency.HashingTermFrequency
import ml.bundle.v1.core.feature.OneHotEncoder.OneHotEncoder
import ml.bundle.v1.core.feature.StandardScaler.StandardScaler
import ml.bundle.v1.core.feature.StringIndexer.StringIndexer
import ml.bundle.v1.core.feature.ReverseStringIndexer.ReverseStringIndexer
import ml.bundle.v1.core.linalg.DenseVector.DenseVector
import ml.bundle.v1.core.linalg.SparseVector.SparseVector
import ml.bundle.v1.core.linalg.Vector.Vector
import ml.bundle.v1.core.regression.LinearRegression.LinearRegression
import ml.bundle.v1.core.tree.CategoricalSplit.CategoricalSplit
import ml.bundle.v1.core.tree.ContinuousSplit.ContinuousSplit
import ml.bundle.v1.core.tree.Split.Split
import ml.bundle.v1.core.tree.node.InternalNodeData.InternalNodeData
import ml.bundle.v1.core.tree.node.LeafNodeData.LeafNodeData
import ml.bundle.v1.core.tree.node.NodeData.NodeData
import ml.bundle.v1.runtime.classification.LogisticRegressionModel.LogisticRegressionModel
import ml.bundle.v1.runtime.classification.SupportVectorMachineModel.SupportVectorMachineModel
import ml.bundle.v1.runtime.feature.HashingTermFrequencyModel.HashingTermFrequencyModel
import ml.bundle.v1.runtime.feature.OneHotEncoderModel.OneHotEncoderModel
import ml.bundle.v1.runtime.feature.StandardScalerModel.StandardScalerModel
import ml.bundle.v1.runtime.feature.StringIndexerModel.StringIndexerModel
import ml.bundle.v1.runtime.feature.ReverseStringIndexerModel.ReverseStringIndexerModel
import ml.bundle.v1.runtime.feature.TokenizerModel.TokenizerModel
import ml.bundle.v1.runtime.feature.VectorAssemblerModel.VectorAssemblerModel
import ml.bundle.v1.runtime.regression.LinearRegressionModel.LinearRegressionModel

/**
  * Created by hwilkins on 3/6/16.
  */
trait Converters {
  import scala.language.implicitConversions

  implicit def mleapVectorToMl(vector: linalg.Vector): Vector = vector match {
    case linalg.DenseVector(values) => Vector(Vector.Data.Dense(DenseVector(values)))
    case linalg.SparseVector(size, indices, values) => Vector(Vector.Data.Sparse(SparseVector(size, indices, values)))
  }

  implicit def mlVectorToMleap(vector: Vector): linalg.Vector = {
    if(vector.data.isDense) {
      linalg.DenseVector(vector.data.dense.get.values.toArray)
    } else if(vector.data.isSparse) {
      val sparse = vector.data.sparse.get
      linalg.SparseVector(sparse.size, sparse.indices.toArray, sparse.values.toArray)
    } else {
      throw new Error("Could not convert to MLeap vector")
    }
  }

  implicit def mleapCategoricalSplitToMl(split: tree.CategoricalSplit): CategoricalSplit = {
    CategoricalSplit(split.featureIndex,
      split.numCategories,
      split.categories,
      split.isLeft)
  }

  implicit def mlCategoricalSplitToMleap(split: CategoricalSplit): tree.CategoricalSplit = {
    tree.CategoricalSplit(split.featureIndex,
      split.numCategories,
      split.categories.toArray,
      split.isLeft)
  }

  implicit def mleapContinuousSplitToMl(split: tree.ContinuousSplit): ContinuousSplit = {
    ContinuousSplit(split.featureIndex, split.threshold)
  }

  implicit def mlContinuousSplitToMleap(split: ContinuousSplit): tree.ContinuousSplit = {
    tree.ContinuousSplit(split.featureIndex, split.threshold)
  }

  implicit def mleapSplitToMl(split: tree.Split): Split = split match {
    case split: tree.CategoricalSplit => Split(Split.Data.Categorical(split))
    case split: tree.ContinuousSplit => Split(Split.Data.Continuous(split))
  }

  implicit def mlSplitToMleap(split: Split): tree.Split = {
    if(split.data.isCategorical) {
      split.data.categorical.get
    } else {
      split.data.continuous.get
    }
  }

  implicit def mleapStringIndexerToMl(model: feature.StringIndexer): StringIndexer = {
    StringIndexer(model.strings)
  }

  implicit def mlStringIndexerToMleap(model: StringIndexer): feature.StringIndexer = {
    feature.StringIndexer(model.strings)
  }

  implicit def mleapStringIndexerModelToMl(model: transformer.StringIndexerModel): StringIndexerModel = {
    StringIndexerModel(inputCol = model.inputCol,
      outputCol = model.outputCol,
      model = model.indexer)
  }

  implicit def mlStringIndexerModelToMleap(model: StringIndexerModel): transformer.StringIndexerModel = {
    transformer.StringIndexerModel(inputCol = model.inputCol,
      outputCol = model.outputCol,
      indexer = model.model)
  }

  implicit def mleapReverseStringIndexerToMl(model: feature.ReverseStringIndexer): ReverseStringIndexer = {
    ReverseStringIndexer(model.strings)
  }

  implicit def mlReverseStringIndexerToMleap(model: ReverseStringIndexer): feature.ReverseStringIndexer = {
    feature.ReverseStringIndexer(model.strings)
  }

  implicit def mleapReverseStringIndexerModelToMl(model: transformer.ReverseStringIndexerModel): ReverseStringIndexerModel = {
    ReverseStringIndexerModel(inputCol = model.inputCol,
      outputCol = model.outputCol,
      model = model.indexer)
  }

  implicit def mlReverseStringIndexerModelToMleap(model: ReverseStringIndexerModel): transformer.ReverseStringIndexerModel = {
    transformer.ReverseStringIndexerModel(inputCol = model.inputCol,
      outputCol = model.outputCol,
      indexer = model.model)
  }

  implicit def mleapHashingTermFrequencyToMl(model: feature.HashingTermFrequency): HashingTermFrequency = {
    HashingTermFrequency(model.numFeatures)
  }

  implicit def mlHashingTermFrequencyToMleap(model: HashingTermFrequency): feature.HashingTermFrequency = {
    feature.HashingTermFrequency(model.numFeatures)
  }

  implicit def mleapHashingTermFrequencyModelToMl(model: transformer.HashingTermFrequencyModel): HashingTermFrequencyModel = {
    HashingTermFrequencyModel(inputCol = model.inputCol,
      outputCol = model.outputCol,
      model = model.hashingTermFrequency)
  }

  implicit def mlHashingTermFrequencyModelToMleap(model: HashingTermFrequencyModel): transformer.HashingTermFrequencyModel = {
    transformer.HashingTermFrequencyModel(inputCol = model.inputCol,
      outputCol = model.outputCol,
      hashingTermFrequency = model.model)
  }

  implicit def mleapStandardScalerToMl(model: feature.StandardScaler): StandardScaler = {
    val std: Option[Vector] = model.std.map(mleapVectorToMl)
    val mean: Option[Vector] = model.mean.map(mleapVectorToMl)

    StandardScaler(std, mean)
  }

  implicit def mlStandardScalerToMleap(model: StandardScaler): feature.StandardScaler = {
    val std: Option[linalg.Vector] = model.std.map(mlVectorToMleap)
    val mean: Option[linalg.Vector] = model.mean.map(mlVectorToMleap)

    feature.StandardScaler(std, mean)
  }

  implicit def mleapStandardScalerModelToMl(model: transformer.StandardScalerModel): StandardScalerModel = {
    StandardScalerModel(inputCol = model.inputCol,
      outputCol = model.outputCol,
      model = model.scaler)
  }

  implicit def mlStandardScalerModelToMleap(model: StandardScalerModel): transformer.StandardScalerModel = {
    transformer.StandardScalerModel(inputCol = model.inputCol,
      outputCol = model.outputCol,
      scaler = model.model)
  }

  implicit def mleapVectorAssemblerModelToMl(model: transformer.VectorAssemblerModel): VectorAssemblerModel = {
    VectorAssemblerModel(inputCols = model.inputCols,
      outputCol = model.outputCol)
  }

  implicit def mlVectorAssemblerModelToMleap(model: VectorAssemblerModel): transformer.VectorAssemblerModel = {
    transformer.VectorAssemblerModel(inputCols = model.inputCols.toArray,
      outputCol = model.outputCol)
  }

  implicit def mleapTokenizerModelToMl(model: transformer.TokenizerModel): TokenizerModel = {
    TokenizerModel(inputCol = model.inputCol,
      outputCol = model.outputCol)
  }

  implicit def mlTokenizerModelToMleap(model: TokenizerModel): transformer.TokenizerModel = {
    transformer.TokenizerModel(inputCol = model.inputCol,
      outputCol = model.outputCol)
  }

  implicit def mleapOneHotEncoderToMl(model: feature.OneHotEncoder): OneHotEncoder = {
    OneHotEncoder(model.size)
  }

  implicit def mlOneHotEncoderToMleap(model: OneHotEncoder): feature.OneHotEncoder = {
    feature.OneHotEncoder(model.size)
  }

  implicit def mleapOneHotEncoderModelToMl(model: transformer.OneHotEncoderModel): OneHotEncoderModel = {
    OneHotEncoderModel(inputCol = model.inputCol,
      outputCol = model.outputCol,
      model = model.model)
  }

  implicit def mlOneHotEncoderModelToMleap(model: OneHotEncoderModel): transformer.OneHotEncoderModel = {
    transformer.OneHotEncoderModel(inputCol = model.inputCol,
      outputCol = model.outputCol,
      model = model.model)
  }

  implicit def mleapLinearRegressionToMl(model: regression.LinearRegression): LinearRegression = {
    LinearRegression(model.coefficients,
      model.intercept)
  }

  implicit def mlLinearRegressionToMleap(model: LinearRegression): regression.LinearRegression = {
    regression.LinearRegression(model.coefficients, model.intercept)
  }

  implicit def mleapLinearRegressionModelToMl(model: transformer.LinearRegressionModel): LinearRegressionModel = {
    LinearRegressionModel(featuresCol = model.featuresCol,
      predictionCol = model.predictionCol,
      model = model.model)
  }

  implicit def mlLinearRegressionModelToMleap(model: LinearRegressionModel): transformer.LinearRegressionModel = {
    transformer.LinearRegressionModel(featuresCol = model.featuresCol,
      predictionCol = model.predictionCol,
      model = model.model)
  }

  implicit def mleapLogisticRegressionToMl(model: classification.LogisticRegression): LogisticRegression = {
    LogisticRegression(model.coefficients, model.intercept, model.threshold)
  }

  implicit def mlLogisticRegressionToMleap(model: LogisticRegression): classification.LogisticRegression = {
    classification.LogisticRegression(model.coefficients, model.intercept, model.threshold)
  }

  implicit def mleapLogisticRegressionModelToMl(model: transformer.LogisticRegressionModel): LogisticRegressionModel = {
    LogisticRegressionModel(featuresCol = model.featuresCol,
      predictionCol = model.predictionCol,
      model = model.model)
  }

  implicit def mlLogisticRegressionModelToMleap(model: LogisticRegressionModel): transformer.LogisticRegressionModel = {
    transformer.LogisticRegressionModel(featuresCol = model.featuresCol,
      predictionCol = model.predictionCol,
      model = model.model)
  }

  implicit def mleapSupportVectorMachineToMl(model: classification.SupportVectorMachine): SupportVectorMachine = {
    SupportVectorMachine(model.coefficients, model.intercept, model.threshold)
  }

  implicit def mlSupportVectorMachineToMleap(model: SupportVectorMachine): classification.SupportVectorMachine = {
    classification.SupportVectorMachine(model.coefficients, model.intercept, model.threshold)
  }

  implicit def mleapSupportVectorMachineModelToMl(model: transformer.SupportVectorMachineModel): SupportVectorMachineModel = {
    SupportVectorMachineModel(featuresCol = model.featuresCol,
      predictionCol = model.predictionCol,
      model = model.model)
  }

  implicit def mlSupportVectorMachineModelToMleap(model: SupportVectorMachineModel): transformer.SupportVectorMachineModel = {
    transformer.SupportVectorMachineModel(featuresCol = model.featuresCol,
      predictionCol = model.predictionCol,
      model = model.model)
  }

  implicit object MleapNode extends Node[tree.Node] {
    override def nodeData(t: tree.Node, includeImpurityStats: Boolean): NodeData = t match {
      case node: tree.InternalNode =>
        NodeData(NodeData.Data.Internal(InternalNodeData(node.prediction, node.gain, node.impurity, node.split)))
      case node: tree.LeafNode =>
        val impurityStats = if(includeImpurityStats) {
          Some(node.impurityStats.get).map(mleapVectorToMl)
        } else { None }
        NodeData(NodeData.Data.Leaf(LeafNodeData(node.prediction, node.impurity, impurityStats)))
    }

    override def isLeaf(t: tree.Node): Boolean = t match {
      case node: tree.InternalNode => false
      case node: tree.LeafNode => true
    }

    override def isInternal(t: tree.Node): Boolean = t match {
      case node: tree.InternalNode => true
      case node: tree.LeafNode => false
    }

    override def leftNode(t: tree.Node): tree.Node = t match {
      case node: tree.InternalNode => node.leftChild
      case _ => throw new Error("Not an internal node")
    }
    override def rightNode(t: tree.Node): tree.Node = t match {
      case node: tree.InternalNode => node.rightChild
      case _ => throw new Error("Not an internal node")
    }

    override def leafFromNodeData(nodeData: LeafNodeData, includeImpurityStats: Boolean): tree.LeafNode = {
      val impurityStats = if(includeImpurityStats) {
        Some(nodeData.impurityStats.get).map(mlVectorToMleap)
      } else { None }
      tree.LeafNode(nodeData.prediction, nodeData.impurity, impurityStats)
    }

    override def internalFromNodeData(nodeData: InternalNodeData,
                                      left: tree.Node,
                                      right: tree.Node): tree.InternalNode = {
      tree.InternalNode(nodeData.prediction,
        nodeData.impurity,
        nodeData.gain,
        left,
        right,
        nodeData.split)
    }
  }

  implicit def mleapDecisionTreeRegressionToMl(model: regression.DecisionTreeRegression): DecisionTreeRegression[tree.Node] = {
    DecisionTreeRegression(model.rootNode, model.numFeatures)
  }

  implicit def mlDecisionTreeRegressionToMleap(model: DecisionTreeRegression[tree.Node]): regression.DecisionTreeRegression = {
    regression.DecisionTreeRegression(model.rootNode, model.numFeatures)
  }

  implicit def mleapRandomForestRegressionToMl(model: regression.RandomForestRegression): RandomForestRegression[tree.Node] = {
    RandomForestRegression(model.numFeatures, model.trees.map(mleapDecisionTreeRegressionToMl))
  }

  implicit def mlRandomForestRegressionToMleap(model: RandomForestRegression[tree.Node]): regression.RandomForestRegression = {
    regression.RandomForestRegression(model.trees.map(mlDecisionTreeRegressionToMleap), model.numFeatures)
  }

  implicit def mleapRandomForestRegressionModelToMl(model: transformer.RandomForestRegressionModel): RandomForestRegressionModel[tree.Node] = {
    RandomForestRegressionModel(featuresCol = model.featuresCol,
      predictionCol = model.predictionCol,
      model = model.model)
  }

  implicit def mlRandomForestRegressionModelToMleap(model: RandomForestRegressionModel[tree.Node]): transformer.RandomForestRegressionModel = {
    transformer.RandomForestRegressionModel(featuresCol = model.featuresCol,
      predictionCol = model.predictionCol,
      model = model.model)
  }

  implicit def mleapDecisionTreeClassificationToMl(model: classification.DecisionTreeClassification): DecisionTreeClassification[tree.Node] = {
    DecisionTreeClassification(model.rootNode, model.numFeatures, model.numClasses)
  }

  implicit def mlDecisionTreeClassificationToMleap(model: DecisionTreeClassification[tree.Node]): classification.DecisionTreeClassification = {
    classification.DecisionTreeClassification(model.rootNode, model.numFeatures, model.numClasses)
  }

  implicit def mleapRandomForestClassificationToMl(model: classification.RandomForestClassification): RandomForestClassification[tree.Node] = {
    RandomForestClassification(model.trees.map(mleapDecisionTreeClassificationToMl), model.numFeatures, model.numClasses)
  }

  implicit def mlRandomForestClassificationToMleap(model: RandomForestClassification[tree.Node]): classification.RandomForestClassification = {
    classification.RandomForestClassification(model.trees.map(mlDecisionTreeClassificationToMleap), model.numFeatures, model.numClasses)
  }

  implicit def mleapRandomForestClassificationModelToMl(model: transformer.RandomForestClassificationModel): RandomForestClassificationModel[tree.Node] = {
    RandomForestClassificationModel(featuresCol = model.featuresCol,
      predictionCol = model.predictionCol,
      model = model.model)
  }

  implicit def mlRandomForestClassificationModelToMleap(model: RandomForestClassificationModel[tree.Node]): transformer.RandomForestClassificationModel = {
    transformer.RandomForestClassificationModel(featuresCol = model.featuresCol,
      predictionCol = model.predictionCol,
      model = model.model)
  }

  implicit def mleapPipelineModelToMl(model: transformer.PipelineModel): PipelineModel = {
    PipelineModel(model.transformers)
  }

  implicit def mlPipelineModelToMleap(model: PipelineModel): transformer.PipelineModel = {
    transformer.PipelineModel(transformers = model.models.map(_.asInstanceOf[Transformer]))
  }
}
object Converters extends Converters
