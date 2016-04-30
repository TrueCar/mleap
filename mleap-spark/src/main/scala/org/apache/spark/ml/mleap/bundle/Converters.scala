package org.apache.spark.ml.mleap.bundle

import ml.bundle.support.v1.core.classification.{DecisionTreeClassification, RandomForestClassification}
import ml.bundle.support.v1.core.regression.{DecisionTreeRegression, RandomForestRegression}
import ml.bundle.support.v1.core.tree.node.Node
import ml.bundle.support.v1.runtime
import ml.bundle.support.v1.runtime.classification.RandomForestClassificationModel
import ml.bundle.support.v1.runtime.regression.RandomForestRegressionModel
import ml.bundle.v1.core.classification.LogisticRegression.LogisticRegression
import ml.bundle.v1.core.classification.SupportVectorMachine.SupportVectorMachine
import ml.bundle.v1.core.feature.HashingTermFrequency.HashingTermFrequency
import ml.bundle.v1.core.feature.ReverseStringIndexer.ReverseStringIndexer
import ml.bundle.v1.core.feature.StandardScaler.StandardScaler
import ml.bundle.v1.core.feature.StringIndexer.StringIndexer
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
import ml.bundle.v1.runtime.feature.ReverseStringIndexerModel.ReverseStringIndexerModel
import ml.bundle.v1.runtime.feature.StandardScalerModel.StandardScalerModel
import ml.bundle.v1.runtime.feature.StringIndexerModel.StringIndexerModel
import ml.bundle.v1.runtime.feature.TokenizerModel.TokenizerModel
import ml.bundle.v1.runtime.feature.VectorAssemblerModel.VectorAssemblerModel
import ml.bundle.v1.runtime.regression.LinearRegressionModel.LinearRegressionModel
import org.apache.spark.ml.mleap.classification.SVMModel
import org.apache.spark.ml.{PipelineModel, Transformer, classification, feature, regression, tree}
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.mllib
import org.apache.spark.mllib.linalg
import org.apache.spark.mllib.tree.impurity.GiniCalculator

/**
  * Created by hollinwilkins on 3/28/16.
  */
trait Converters {
  import scala.language.implicitConversions

  implicit def sparkVectorToMl(vector: linalg.Vector): Vector = vector match {
    case linalg.DenseVector(values) => Vector(Vector.Data.Dense(DenseVector(values)))
    case linalg.SparseVector(size, indices, values) => Vector(Vector.Data.Sparse(SparseVector(size, indices, values)))
  }

  implicit def mlVectorToSpark(vector: Vector): linalg.Vector = {
    if(vector.data.isDense) {
      new linalg.DenseVector(vector.data.dense.get.values.toArray)
    } else {
      val sparse = vector.data.sparse.get
      new linalg.SparseVector(sparse.size, sparse.indices.toArray, sparse.values.toArray)
    }
  }

  implicit def sparkCategoricalSplitToMl(split: tree.CategoricalSplit): CategoricalSplit = {
    val (isLeft, categories) = if(split.leftCategories.length >= split.rightCategories.length) {
      (true, split.leftCategories)
    } else {
      (false, split.rightCategories)
    }
    val numCategories = split.leftCategories.length + split.rightCategories.length
    CategoricalSplit(split.featureIndex, numCategories, categories, isLeft)
  }

  implicit def mlCategoricalSplitToSpark(split: CategoricalSplit): tree.CategoricalSplit = {
    new tree.CategoricalSplit(split.featureIndex,
      split.categories.toArray,
      split.numCategories)
  }

  implicit def mleapContinuousSplitToMl(split: tree.ContinuousSplit): ContinuousSplit = {
    ContinuousSplit(split.featureIndex, split.threshold)
  }

  implicit def mlContinuousSplitToMleap(split: ContinuousSplit): tree.ContinuousSplit = {
    new tree.ContinuousSplit(split.featureIndex, split.threshold)
  }

  implicit def sparkSplitToMl(split: tree.Split): Split = split match {
    case split: tree.CategoricalSplit => Split(Split.Data.Categorical(split))
    case split: tree.ContinuousSplit => Split(Split.Data.Continuous(split))
  }

  implicit def mlSplitToSpark(split: Split): tree.Split = {
    if(split.data.isCategorical) {
      split.data.categorical.get
    } else {
      split.data.continuous.get
    }
  }

  implicit def sparkStringIndexerModelToMl(model: feature.StringIndexerModel): StringIndexerModel = {
    val indexer = StringIndexer(model.labels)
    StringIndexerModel(inputCol = model.getInputCol,
      outputCol = model.getOutputCol,
      model = indexer)
  }

  implicit def mlStringIndexerModelToSpark(model: StringIndexerModel): feature.StringIndexerModel = {
    new feature.StringIndexerModel(model.model.strings.toArray)
      .setInputCol(model.inputCol)
      .setOutputCol(model.outputCol)
  }

  implicit def sparkReverseStringIndexerModelToMl(model: feature.IndexToString): ReverseStringIndexerModel = {
    val indexer = ReverseStringIndexer(model.getLabels)
    ReverseStringIndexerModel(inputCol = model.getInputCol,
      outputCol = model.getOutputCol,
      model = indexer)
  }

  implicit def mlReverseStringIndexerModelToSpark(model: ReverseStringIndexerModel): feature.IndexToString = {
    new feature.IndexToString()
      .setLabels(model.model.strings.toArray)
      .setInputCol(model.inputCol)
      .setOutputCol(model.outputCol)
  }

  implicit def sparkHashingTermFrequencyModelToMl(model: feature.HashingTF): HashingTermFrequencyModel = {
    val htf = HashingTermFrequency(model.getNumFeatures)
    HashingTermFrequencyModel(inputCol = model.getInputCol,
      outputCol = model.getOutputCol,
      model = htf)
  }

  implicit def mlHashingTermFrequencyModelToSpark(model: HashingTermFrequencyModel): feature.HashingTF = {
    new feature.HashingTF()
      .setInputCol(model.inputCol)
      .setOutputCol(model.outputCol)
      .setNumFeatures(model.model.numFeatures)
  }


  implicit def sparkStandardScalerModelToMl(model: feature.StandardScalerModel): StandardScalerModel = {
    val stddev: Option[Vector] = if(model.getWithStd) {
      Some(model.std)
    } else {
      None
    }

    val mean: Option[Vector] = if(model.getWithMean) {
      Some(model.mean)
    } else {
      None
    }

    val scaler = StandardScaler(stddev, mean)
    StandardScalerModel(inputCol = model.getInputCol,
      outputCol = model.getOutputCol,
      model = scaler)
  }

  implicit def mlStandardScalerModelToSpark(model: StandardScalerModel): feature.StandardScalerModel = {
    new feature.StandardScalerModel(Identifiable.randomUID("stdScalerModel"),
      model.model.std.map(mlVectorToSpark).orNull,
      model.model.mean.map(mlVectorToSpark).orNull)
      .setInputCol(model.inputCol)
      .setOutputCol(model.outputCol)
  }

  implicit def sparkVectorAssemblerModelToMl(model: feature.VectorAssembler): VectorAssemblerModel = {
    VectorAssemblerModel(inputCols = model.getInputCols,
      outputCol = model.getOutputCol)
  }

  implicit def mlVectorAssemblerModelToSpark(model: VectorAssemblerModel): feature.VectorAssembler = {
    new feature.VectorAssembler()
      .setInputCols(model.inputCols.toArray)
      .setOutputCol(model.outputCol)
  }


  implicit def sparkTokenizerModelToMl(model: feature.Tokenizer): TokenizerModel = {
    TokenizerModel(inputCol = model.getInputCol,
      outputCol = model.getOutputCol)
  }

  implicit def mlTokenizerModelToSpark(model: TokenizerModel): feature.Tokenizer = {
    new feature.Tokenizer()
      .setInputCol(model.inputCol)
      .setOutputCol(model.outputCol)
  }


  implicit def sparkLinearRegressionModelToMl(model: regression.LinearRegressionModel): LinearRegressionModel = {
    val linearRegression = LinearRegression(model.coefficients, model.intercept)
    LinearRegressionModel(featuresCol = model.getFeaturesCol,
      predictionCol = model.getPredictionCol,
      model = linearRegression)
  }

  implicit def mlLinearRegressionModelToSpark(model: LinearRegressionModel): regression.LinearRegressionModel = {
    new regression.LinearRegressionModel(Identifiable.randomUID("linearRegressionModel"),
      model.model.coefficients,
      model.model.intercept)
      .setFeaturesCol(model.featuresCol)
      .setPredictionCol(model.predictionCol)
  }

  implicit object SparkNode extends Node[tree.Node] {
    override def nodeData(t: tree.Node, includeImpurityStats: Boolean): NodeData = t match {
      case node: tree.InternalNode =>
        NodeData(NodeData.Data.Internal(InternalNodeData(node.prediction, node.gain, node.impurity, node.split)))
      case node: tree.LeafNode =>
        val impurityStats: Option[Vector] = if(includeImpurityStats) {
          Some(Vector(Vector.Data.Dense(DenseVector(node.impurityStats.stats))))
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
      // TODO: do we want to support other calculators?
      // Not necessary at this time, as we are only interested in the stats, not the calculations
      val calc: GiniCalculator = if(includeImpurityStats) {
        nodeData.impurityStats.map(impurityStats => new GiniCalculator(impurityStats.getDense.values.toArray)).orNull
      } else { null }
      new tree.LeafNode(nodeData.prediction, nodeData.impurity, calc)
    }

    override def internalFromNodeData(nodeData: InternalNodeData,
                                      left: tree.Node,
                                      right: tree.Node): tree.InternalNode = {
      new tree.InternalNode(nodeData.prediction,
        nodeData.impurity,
        nodeData.gain,
        left,
        right,
        nodeData.split,
        null)
    }
  }

  implicit def sparkDecisionTreeRegressionToMl(model: regression.DecisionTreeRegressionModel): DecisionTreeRegression[tree.Node] = {
    DecisionTreeRegression(model.rootNode,
      model.numFeatures)
  }

  implicit def mlDecisionTreeRegressionToSpark(model: DecisionTreeRegression[tree.Node]): regression.DecisionTreeRegressionModel = {
    new regression.DecisionTreeRegressionModel(model.rootNode, model.numFeatures)
  }

  implicit def sparkRandomForestRegressionModelToMl(model: regression.RandomForestRegressionModel): RandomForestRegressionModel[tree.Node] = {
    val trees = model.trees.asInstanceOf[Array[regression.DecisionTreeRegressionModel]]
    val randomForestRegression = RandomForestRegression(model.numFeatures, trees.map(sparkDecisionTreeRegressionToMl))

    RandomForestRegressionModel(featuresCol = model.getFeaturesCol,
      predictionCol = model.getPredictionCol,
      model = randomForestRegression)
  }

  implicit def mlRandomForestRegressionModelToSpark(model: RandomForestRegressionModel[tree.Node]): regression.RandomForestRegressionModel = {
    val trees = model.model.trees.map(mlDecisionTreeRegressionToSpark).toArray
    new regression.RandomForestRegressionModel(trees, model.model.numFeatures)
      .setFeaturesCol(model.featuresCol)
      .setPredictionCol(model.predictionCol)
  }

  implicit def sparkLogisticRegressionModelToMl(model: classification.LogisticRegressionModel): LogisticRegressionModel = {
    val m = LogisticRegression(model.coefficients, model.intercept, model.getThreshold)
    LogisticRegressionModel(featuresCol = model.getFeaturesCol,
      predictionCol = model.getPredictionCol,
      model = m)
  }

  implicit def mlLogisticRegressionModelToSpark(model: LogisticRegressionModel): classification.LogisticRegressionModel = {
    new classification.LogisticRegressionModel(Identifiable.randomUID("logisticRegressionModel"),
      model.model.coefficients,
      model.model.intercept)
      .setFeaturesCol(model.featuresCol)
      .setPredictionCol(model.predictionCol)
  }

  implicit def sparkSupportVectorMachineModelToMl(model: SVMModel): SupportVectorMachineModel = {
    val m = SupportVectorMachine(model.model.weights, model.model.intercept, model.getThreshold)
    SupportVectorMachineModel(featuresCol = model.getFeaturesCol,
      predictionCol = model.getPredictionCol,
      model = m)
  }

  implicit def mlSupportVectorMachineModelToSpark(model: SupportVectorMachineModel): SVMModel = {
    val m = new mllib.classification.SVMModel(model.model.coefficients, model.model.intercept)
    new SVMModel(m)
  }

  implicit def sparkDecisionTreeClassificationToMl(model: classification.DecisionTreeClassificationModel): DecisionTreeClassification[tree.Node] = {
    DecisionTreeClassification(model.rootNode,
      model.numFeatures,
      model.numClasses)
  }

  implicit def mlDecisionTreeClassificationToSpark(model: DecisionTreeClassification[tree.Node]): classification.DecisionTreeClassificationModel = {
    new classification.DecisionTreeClassificationModel(model.rootNode, model.numFeatures, model.numClasses)
  }

  implicit def sparkRandomForestClassificationModelToMl(model: classification.RandomForestClassificationModel): RandomForestClassificationModel[tree.Node] = {
    val trees = model.trees.asInstanceOf[Array[classification.DecisionTreeClassificationModel]]
    val randomForestRegression = RandomForestClassification(trees.map(sparkDecisionTreeClassificationToMl),
      model.numFeatures,
      model.numClasses)

    RandomForestClassificationModel(featuresCol = model.getFeaturesCol,
      predictionCol = model.getPredictionCol,
      model = randomForestRegression)
  }

  implicit def mlRandomForestClassificationModelToSpark(model: RandomForestClassificationModel[tree.Node]): classification.RandomForestClassificationModel = {
    val trees = model.model.trees.map(mlDecisionTreeClassificationToSpark).toArray
    new classification.RandomForestClassificationModel(trees, model.model.numFeatures, model.model.numClasses)
      .setFeaturesCol(model.featuresCol)
      .setPredictionCol(model.predictionCol)
  }

  implicit def sparkPipelineModelToMl(model: PipelineModel): runtime.PipelineModel = {
    runtime.PipelineModel(model.stages)
  }

  implicit def mlPipelineModelToSpark(model: runtime.PipelineModel): PipelineModel = {
    new PipelineModel(Identifiable.randomUID("pipelineModel"),
      model.models.map(_.asInstanceOf[Transformer]).toArray)
  }
}
object Converters extends Converters
