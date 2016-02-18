package com.truecar.mleap.learning.serialization

import com.truecar.mleap.core.serialization.{AtomFormat, BasicTypedFormat, MleapJsonProtocol, MultiFormat}
import com.truecar.mleap.core.util.Atom
import com.truecar.mleap.learning.estimator._
import spray.json.{JsValue, RootJsonFormat}

/**
  * Created by hwilkins on 12/7/15.
  */
case class LiftedEstimatorFormat[E <: Estimator](format: RootJsonFormat[E]) extends RootJsonFormat[Estimator] {
  override def read(json: JsValue): Estimator = {
    format.read(json)
  }

  override def write(obj: Estimator): JsValue = {
    format.write(obj.asInstanceOf[E])
  }
}

trait EstimatorJsonSupport {
  import EstimatorFormat._

  implicit val estimatorEstimatorFormat = estimatorFormat

  implicit val linearRegressionEstimatorFormat = linearRegression
  implicit val oneHotEncoderEstimatorFormat = oneHotEncoder
  implicit val pipelineEstimatorFormat = pipeline
  implicit val randomForestRegressionEstimatorFormat = randomForestRegression
  implicit val selectorEstimatorFormat = selector
  implicit val standardScalerEstimatorFormat = standardScaler
  implicit val stringIndexerEstimatorFormat = stringIndexer
  implicit val tokenizerEstimatorFormat = tokenizer
  implicit val vectorAssemblerEstimatorFormat = vectorAssembler
}
object EstimatorJsonSupport extends EstimatorJsonSupport

object EstimatorFormat {
  import MleapJsonProtocol._

  import scala.language.implicitConversions

  implicit val estimatorFormat = AtomFormat[Estimator, MultiFormat[Estimator]](Atom(MultiFormat[Estimator](Map())))

  implicit def liftEstimatorFormat[E <: Estimator](format: BasicTypedFormat[E]): LiftedEstimatorFormat[E] = {
    LiftedEstimatorFormat(format)
  }

  def addFormat[E <: Estimator](klazz: Class[E], format: RootJsonFormat[E]): BasicTypedFormat[E] = {
    estimatorFormat.atom.set(estimatorFormat.atom.get.withFormat(klazz.getCanonicalName, typedFormat(liftEstimatorFormat(format))))
    typedFormat(format)
  }

  val linearRegression = addFormat(classOf[LinearRegressionEstimator], jsonFormat12(LinearRegressionEstimator.apply))
  val oneHotEncoder = addFormat(classOf[OneHotEncoderEstimator], jsonFormat4(OneHotEncoderEstimator.apply))
  val pipeline = addFormat(classOf[PipelineEstimator], jsonFormat2(PipelineEstimator.apply))
  val randomForestRegression = addFormat(classOf[RandomForestRegressionEstimator], jsonFormat16(RandomForestRegressionEstimator.apply))
  val selector = addFormat(classOf[SelectorEstimator], jsonFormat2(SelectorEstimator.apply))
  val standardScaler = addFormat(classOf[StandardScalerEstimator], jsonFormat5(StandardScalerEstimator.apply))
  val stringIndexer = addFormat(classOf[StringIndexerEstimator], jsonFormat4(StringIndexerEstimator.apply))
  val tokenizer = addFormat(classOf[TokenizerEstimator], jsonFormat3(TokenizerEstimator.apply))
  val vectorAssembler = addFormat(classOf[VectorAssemblerEstimator], jsonFormat3(VectorAssemblerEstimator.apply))
}