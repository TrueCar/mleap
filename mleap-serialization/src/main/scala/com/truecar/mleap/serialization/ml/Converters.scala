package com.truecar.mleap.serialization.ml

import com.truecar.mleap.core.{regression, feature, tree, linalg}
import com.truecar.mleap.runtime.transformer
import ml.core.feature.HashingTermFrequency.HashingTermFrequency
import ml.core.feature.StandardScaler.StandardScaler
import ml.core.feature.StringIndexer.StringIndexer
import ml.core.feature.Tokenizer.Tokenizer
import ml.core.linalg.DenseVector.DenseVector
import ml.core.linalg.SparseVector.SparseVector
import ml.core.linalg.Vector.Vector
import ml.core.regression.LinearRegression.LinearRegression
import ml.core.tree.CategoricalSplit.CategoricalSplit
import ml.core.tree.ContinuousSplit.ContinuousSplit
import ml.core.tree.Split.Split
import ml.runtime.feature.HashingTermFrequencyModel.HashingTermFrequencyModel
import ml.runtime.feature.StandardScalerModel.StandardScalerModel
import ml.runtime.feature.StringIndexerModel.StringIndexerModel
import ml.runtime.feature.TokenizerModel.TokenizerModel
import ml.runtime.feature.VectorAssemblerModel.VectorAssemblerModel
import ml.runtime.regression.LinearRegressionModel.LinearRegressionModel

/**
  * Created by hwilkins on 3/6/16.
  */
trait Converters {
  import scala.language.implicitConversions

  implicit def mleapVectorToMl(vector: linalg.Vector): Vector = vector match {
    case linalg.DenseVector(values) => Vector(dense = Some(DenseVector(values)))
    case linalg.SparseVector(size, indices, values) => Vector(sparse = Some(SparseVector(size, indices, values)))
  }

  implicit def mlVectorToMleap(vector: Vector): linalg.Vector = {
    vector.dense match {
      case Some(DenseVector(values)) => linalg.DenseVector(values.toArray)
      case None =>
        vector.sparse match {
          case Some(SparseVector(size, indices, values)) =>
            linalg.SparseVector(size, indices.toArray, values.toArray)
          case None =>
            throw new Error("Could not convert to MLeap vector")
        }
    }
  }

  implicit def mleapCategoricalSplitToMl(split: tree.CategoricalSplit): CategoricalSplit = {
    CategoricalSplit(split.featureIndex,
      split.categories,
      split.isLeft)
  }

  implicit def mlCategoricalSplitToMleap(split: CategoricalSplit): tree.CategoricalSplit = {
    tree.CategoricalSplit(split.featureIndex,
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
    case split: tree.CategoricalSplit => Split(categorical = Some(split))
    case split: tree.ContinuousSplit => Split(continuous = Some(split))
  }

  implicit def mlSplitToMleap(split: Split): tree.Split = split.categorical match {
    case Some(data) => data
    case None =>
      split.continuous match {
        case Some(data) => data
        case None => throw new Error("Could not convert split")
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

  implicit def mleapTokenizerToMl(model: feature.Tokenizer): Tokenizer = {
    Tokenizer(model.regex)
  }

  implicit def mlTokenizerToMleap(model: Tokenizer): feature.Tokenizer = {
    feature.Tokenizer(model.regex)
  }

  implicit def mleapTokenizerModelToMl(model: transformer.TokenizerModel): TokenizerModel = {
    TokenizerModel(inputCol = model.inputCol,
      outputCol = model.outputCol,
      model = model.tokenizer)
  }

  implicit def mlTokenizerModelToMleap(model: TokenizerModel): transformer.TokenizerModel = {
    transformer.TokenizerModel(inputCol = model.inputCol,
      outputCol = model.outputCol,
      tokenizer = model.model)
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
}
object Converters extends Converters
