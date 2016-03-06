package com.truecar.mleap.serialization.json

import com.truecar.mleap.core.feature._
import com.truecar.mleap.core.linalg
import com.truecar.mleap.core.linalg.{DenseVector, SparseVector}
import com.truecar.mleap.core.regression.LinearRegression
import com.truecar.mleap.runtime.transformer._
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.language.implicitConversions

/**
  * Created by hwilkins on 11/12/15.
  */

trait CoreJsonSupport {
  import scala.language.implicitConversions

  protected implicit def extractString(json: JsValue): String = json match {
    case JsString(value) => value
    case value => throw new Error("invalid string: " + value)
  }

  protected implicit def extractInt(json: JsValue): Int = json match {
    case JsNumber(value) => value.toInt
    case value => throw new Error("invalid int: " + value)
  }

  protected implicit def extractDouble(json: JsValue): Double = json match {
    case JsNumber(value) => value.toDouble
    case value => throw new Error("invalid double: " + value)
  }

  protected implicit def extractIntArray(json: JsValue): Array[Int] = json match {
    case JsArray(values) => values.map(value => extractInt(value)).toArray
    case value => throw new Error("invalid int array: " + value)
  }

  protected implicit def extractDoubleArray(json: JsValue): Array[Double] = json match {
    case JsArray(values) => values.map(value => extractDouble(value)).toArray
    case value => throw new Error("invalid double array: " + value)
  }

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

  // Feature Types

  implicit val mleapOneHotEncoderFormat = jsonFormat[Int, OneHotEncoder](OneHotEncoder, "size")
  implicit val mleapStandardScalerFormat = jsonFormat2(StandardScaler)
  implicit val mleapStringIndexerFormat = jsonFormat1(StringIndexer)
  implicit val mleapVectorAssemblerFormat = jsonFormat0(VectorAssembler.apply)
  implicit val mleapTokenizerFormat = jsonFormat1(Tokenizer.apply)
  implicit val mleapHashingTermFrequencyFormat = jsonFormat1(HashingTermFrequency.apply)

  implicit val mleapStandardScalerModelFormat = jsonFormat3(StandardScalerModel)
  implicit val mleapStringIndexerModelFormat = jsonFormat3(StringIndexerModel)
  implicit val mleapVectorAssemblerModelFormat = jsonFormat[Array[String], String, VectorAssemblerModel](VectorAssemblerModel.apply, "inputCols", "outputCol")
  implicit val mleapTokenizerModelFormat = jsonFormat3(TokenizerModel)
  implicit val mleapHashingTermFrequencyModelFormat = jsonFormat3(HashingTermFrequencyModel)


  // Regression Types

  implicit val mleapLinearRegressionFormat = jsonFormat2(LinearRegression)

  implicit val mleapLinearRegressionModelFormat = jsonFormat3(LinearRegressionModel)
}
object CoreJsonSupport extends CoreJsonSupport
