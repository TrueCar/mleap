package com.truecar.mleap.runtime.serialization

import com.truecar.mleap.core.serialization.{AtomFormat, BasicTypedFormat, MultiFormat, CoreJsonSupport}
import com.truecar.mleap.core.util.Atom
import com.truecar.mleap.core.serialization._
import com.truecar.mleap.runtime.transformer._
import com.truecar.mleap.runtime.types.StructType
import spray.json.{JsValue, RootJsonFormat}

/**
  * Created by hwilkins on 11/21/15.
  */
case class LiftedTransformerFormat[T <: Transformer](format: RootJsonFormat[T]) extends RootJsonFormat[Transformer] {
  override def read(json: JsValue): Transformer = format.read(json)

  override def write(obj: Transformer): JsValue = format.write(obj.asInstanceOf[T])
}

trait TransformerJsonSupport {
  import TransformerFormat._

  implicit val transformerTransformerFormat = transformerFormat

  implicit val linearRegressionModelFormat = linearRegression
  implicit val pipelineModelFormat = pipeline
  implicit val oneHotEncoderModelFormat = oneHotEncoder
  implicit val randomForestRegressionModelFormat = randomForestRegression
  implicit val selectorModelFormat = selector
  implicit val standardScalerModelFormat = standardScaler
  implicit val stringIndexerModelFormat = stringIndexer
  implicit val tokenizerModelFormat = tokenizer
  implicit val vectorAssemblerModelFormat = vectorAssembler
}
object TransformerJsonSupport extends TransformerJsonSupport

object TransformerFormat extends BaseRuntimeJsonSupport with CoreJsonSupport {
  import scala.language.implicitConversions

  implicit val transformerFormat = AtomFormat[Transformer, MultiFormat[Transformer]](Atom(MultiFormat[Transformer](Map())))

  implicit def liftTransformerFormat[T <: Transformer](format: RootJsonFormat[T]): LiftedTransformerFormat[T] = {
    LiftedTransformerFormat(format)
  }

  def addFormat[T <: Transformer](klazz: Class[T], format: RootJsonFormat[T]): BasicTypedFormat[T] = {
    transformerFormat.atom.set(transformerFormat.atom.get.withFormat(klazz.getCanonicalName, typedFormat(liftTransformerFormat(format))))
    typedFormat(format)
  }

  val linearRegression = addFormat(classOf[LinearRegressionModel], jsonFormat3(LinearRegressionModel.apply))
  val pipeline = addFormat(classOf[PipelineModel], jsonFormat1(PipelineModel))
  val oneHotEncoder = TransformerFormat.addFormat(classOf[OneHotEncoderModel], jsonFormat3(OneHotEncoderModel))
  val randomForestRegression = addFormat(classOf[RandomForestRegressionModel], jsonFormat3(RandomForestRegressionModel))
  val selector = addFormat(classOf[SelectorModel], jsonFormat1(SelectorModel))
  val standardScaler = addFormat(classOf[StandardScalerModel], jsonFormat3(StandardScalerModel))
  val stringIndexer = addFormat(classOf[StringIndexerModel], jsonFormat3(StringIndexerModel))
  val tokenizer = addFormat(classOf[TokenizerModel], jsonFormat3(TokenizerModel))
  val vectorAssembler = addFormat(classOf[VectorAssemblerModel], jsonFormat[StructType, String, VectorAssemblerModel](VectorAssemblerModel.apply, "inputSchema", "outputCol"))
}
