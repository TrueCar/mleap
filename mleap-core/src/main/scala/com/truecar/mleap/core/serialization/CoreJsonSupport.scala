package com.truecar.mleap.core.serialization

import com.truecar.mleap.core.feature._
import com.truecar.mleap.core.linalg
import com.truecar.mleap.core.linalg.{DenseVector, SparseVector}
import com.truecar.mleap.core.regression.{LinearRegression, RandomForestRegression, DecisionTreeRegression}
import com.truecar.mleap.core.tree._
import spray.json._

import scala.language.implicitConversions

/**
  * Created by hwilkins on 11/12/15.
  */
trait CoreJsonSupport extends MleapJsonProtocol {
  // Linear Algebra Types

  implicit object MleapVectorFormat extends JsonFormat[linalg.Vector] {
    override def write(obj: linalg.Vector): JsValue = obj match {
      case DenseVector(values) =>
        val jsValues = values.map(JsNumber(_))
        JsArray(jsValues: _*)
      case SparseVector(size, indices, values) =>
        val jsSize = JsNumber(size)
        val jsIndices = JsArray(indices.map(JsNumber(_)): _*)
        val jsValues = JsArray(values.map(JsNumber(_)): _*)
        JsObject(Map("size" -> jsSize, "indices" -> jsIndices, "values" -> jsValues))
    }

    override def read(json: JsValue): linalg.Vector = json match {
      case jsValues: JsArray =>
        val values: Array[Double] = jsValues
        DenseVector(values)
      case JsObject(fields) =>
        val size: Int = fields("size")
        val indices: Array[Int] = fields("indices")
        val values: Array[Double] = fields("values")
        SparseVector(size, indices, values)
      case _ => throw new Error("invalid JSON Vector format")
    }
  }

  // Tree Types

  private implicit val mleapCategoricalSplitFormat = BasicTypedFormat[CategoricalSplit](jsonFormat3(CategoricalSplit))
  private implicit val mleapContinuousSplitFormat = BasicTypedFormat[ContinuousSplit](jsonFormat2(ContinuousSplit))

  implicit object MleapSplitFormat extends RootJsonFormat[Split] {
    override def write(obj: Split): JsValue = obj match {
      case obj: CategoricalSplit => obj.toJson
      case obj: ContinuousSplit => obj.toJson
    }

    override def read(json: JsValue): Split = {
      (json.asJsObject.fields("type"): String) match {
        case Split.categoricalSplitName => json.convertTo[CategoricalSplit]
        case Split.continuousSplitName => json.convertTo[ContinuousSplit]
      }
    }
  }

  private[mleap] case class MleapNodeFormat() extends RootJsonFormat[Node] {
    override def write(obj: Node): JsValue = obj match {
      case obj: InternalNode => obj.toJson
      case obj: LeafNode => obj.toJson
    }

    override def read(json: JsValue): Node = {
      (json.asJsObject.fields("type"): String) match {
        case Node.internalNodeName => json.convertTo[InternalNode]
        case Node.leafNodeName => json.convertTo[LeafNode]
      }
    }
  }

  implicit val mleapNodeFormat = rootFormat(lazyFormat(MleapNodeFormat()))

  private implicit val mleapInternalNodeFormat = BasicTypedFormat[InternalNode](jsonFormat6(InternalNode))
  private implicit val mleapLeafNodeFormat = BasicTypedFormat[LeafNode](jsonFormat2(LeafNode))

  // Feature Types

  implicit val mleapOneHotEncoderFormat = jsonFormat[Int, OneHotEncoder](OneHotEncoder, "size")
  implicit val mleapStandardScalerFormat = jsonFormat2(StandardScaler)
  implicit val mleapStringIndexerFormat = jsonFormat1(StringIndexer)
  implicit val mleapVectorAssemblerFormat = jsonFormat0(VectorAssembler.apply)
  implicit val mleapTokenizerFormat = jsonFormat1(Tokenizer.apply)
  implicit val mleapHashingTermFrequencyFormat = jsonFormat1(HashingTermFrequency.apply)

  // Regression Types

  implicit val mleapLinearRegressionFormat = jsonFormat2(LinearRegression)
  implicit val mleapDecisionTreeRegressionFormat = jsonFormat1(DecisionTreeRegression)
  implicit val mleapRandomForestRegressionFormat =
    jsonFormat[Seq[DecisionTreeRegression], Seq[Double], RandomForestRegression](RandomForestRegression, "trees", "treeWeights")
}
object CoreJsonSupport extends CoreJsonSupport
