package com.truecar.mleap.bundle.core.json

import ml.core.feature.HashingTermFrequency.HashingTermFrequency
import ml.core.feature.StandardScaler.StandardScaler
import ml.core.feature.StringIndexer.StringIndexer
import ml.core.feature.Tokenizer.Tokenizer
import ml.core.linalg.DenseVector.DenseVector
import ml.core.linalg.SparseVector.SparseVector
import ml.core.linalg.Vector.Vector
import ml.core.regression.LinearRegression.LinearRegression
import ml.core.regression.RandomForestRegressionMetaData.RandomForestRegressionMetaData
import ml.core.tree.CategoricalSplit.CategoricalSplit
import ml.core.tree.ContinuousSplit.ContinuousSplit
import ml.core.tree.InternalNodeData.InternalNodeData
import ml.core.tree.LeafNodeData.LeafNodeData
import ml.core.tree.NodeData.NodeData
import ml.core.tree.NodeMetaData.NodeMetaData
import ml.core.tree.NodeMetaData.NodeMetaData.NodeFormat
import ml.core.tree.Split.Split
import ml.runtime.feature.HashingTermFrequencyModel.HashingTermFrequencyModel
import ml.runtime.feature.StandardScalerModel.StandardScalerModel
import ml.runtime.feature.StringIndexerModel.StringIndexerModel
import ml.runtime.feature.TokenizerModel.TokenizerModel
import ml.runtime.feature.VectorAssemblerModel.VectorAssemblerModel
import ml.runtime.regression.LinearRegressionModel.LinearRegressionModel
import ml.runtime.regression.RandomForestRegressionModelMetaData.RandomForestRegressionModelMetaData
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.reflect.ClassTag

/**
  * Created by hwilkins on 3/6/16.
  */
trait MlJsonSupport {
  // core.linalg
  implicit val mlSparseVectorFormat = jsonFormat3(SparseVector.apply)
  implicit val mlDenseVectorFormat = jsonFormat1(DenseVector.apply)
  implicit val mlVectorFormat = jsonFormat2(Vector.apply)

  // core.feature
  implicit val mlStringIndexerFormat = jsonFormat1(StringIndexer.apply)
  implicit val mlHashingTermFrequencyFormat = jsonFormat1(HashingTermFrequency.apply)
  implicit val mlStandardScalerFormat = jsonFormat2(StandardScaler.apply)
  implicit val mlTokenizerFormat = jsonFormat1(Tokenizer.apply)

  // core.regression
  implicit val mlLinearRegressionFormat = jsonFormat2(LinearRegression.apply)
  implicit val mlRandomForestRegressionMetaDataFormat: RootJsonFormat[RandomForestRegressionMetaData] =
    jsonFormat2(RandomForestRegressionMetaData.apply)

  // core.tree
  implicit val mlNodeFormatFormat = new JsonFormat[NodeFormat] {
    override def write(obj: NodeFormat): JsValue = obj match {
      case NodeFormat.LINEAR => JsString("linear")
    }

    override def read(json: JsValue): NodeFormat = json match {
      case JsString("linear") => NodeFormat.LINEAR
      case _ => throw new Error("Could not parse node format")
    }
  }
  implicit val mlNodeMetaDataFormat: RootJsonFormat[NodeMetaData] = jsonFormat1(NodeMetaData.apply)
  implicit val mlCategoricalSplitFormat = jsonFormat3(CategoricalSplit.apply)
  implicit val mlContinuousSplitFormat = jsonFormat2(ContinuousSplit.apply)
  implicit val mlSplitFormat = new RootJsonFormat[Split] {
    override def write(obj: Split): JsValue = {
      obj.continuous match {
        case Some(split) =>
          val fields = split.toJson.asJsObject.fields + ("type" -> JsString("continuous"))
          JsObject(fields)
        case None =>
          obj.categorical match {
            case Some(split) =>
              val fields = split.toJson.asJsObject.fields + ("type" -> JsString("categorical"))
              JsObject(fields)
            case None => throw new Error("Could not serialize split")
          }
      }
    }

    override def read(json: JsValue): Split = {
      val typeName = json.asJsObject.fields("type") match {
        case JsString(name) => name
        case _ => throw new Error("Could not read split")
      }

      typeName match {
        case "categorical" =>
          Split(categorical = Some(json.convertTo[CategoricalSplit]))
        case "continuous" =>
          Split(continuous = Some(json.convertTo[ContinuousSplit]))
      }
    }
  }

  implicit val mlInternalNodeDataFormat = jsonFormat4(InternalNodeData.apply)
  implicit val mlLeafNodeDataFormat = jsonFormat2(LeafNodeData.apply)
  implicit val mlNodeDataFormat: RootJsonFormat[NodeData] = new RootJsonFormat[NodeData] {
    override def write(obj: NodeData): JsValue = {
      obj.internal match {
        case Some(node) =>
          val fields = node.toJson.asJsObject.fields + ("type" -> JsString("internal"))
          JsObject(fields)
        case None =>
          obj.leaf match {
            case Some(node) =>
              val fields = node.toJson.asJsObject.fields + ("type" -> JsString("leaf"))
              JsObject(fields)
            case None =>
              throw new Error("Could not serialize node")
          }
      }
    }

    override def read(json: JsValue): NodeData = {
      val typeName = json.asJsObject.fields("type") match {
        case JsString(name) => name
        case _ => throw new Error("Could not read node")
      }

      typeName match {
        case "internal" =>
          NodeData(internal = Some(json.convertTo[InternalNodeData]))
        case "leaf" =>
          NodeData(leaf = Some(json.convertTo[LeafNodeData]))
      }
    }
  }

  // runtime.feature
  implicit val mlStringIndexerModelFormat: RootJsonFormat[StringIndexerModel] = jsonFormat3(StringIndexerModel.apply)
  implicit val mlHashingTermFrequencyModelFormat: RootJsonFormat[HashingTermFrequencyModel] = jsonFormat3(HashingTermFrequencyModel.apply)
  implicit val mlStandardScalerModelFormat: RootJsonFormat[StandardScalerModel] = jsonFormat3(StandardScalerModel.apply)
  implicit val mlVectorAssemblerModelFormat: RootJsonFormat[VectorAssemblerModel] = jsonFormat2(VectorAssemblerModel.apply)
  implicit val mlTokenizerModelFormat: RootJsonFormat[TokenizerModel] = jsonFormat3(TokenizerModel.apply)

  // runtime.regression
  implicit val mlLinearRegressionModelFormat: RootJsonFormat[LinearRegressionModel] = jsonFormat3(LinearRegressionModel.apply)
  implicit val mlRandomForestRegressionModelMetaDataFormat: RootJsonFormat[RandomForestRegressionModelMetaData] = jsonFormat2(RandomForestRegressionModelMetaData.apply)
}
object MlJsonSupport extends MlJsonSupport
