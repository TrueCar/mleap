package org.apache.spark.ml.mleap.runtime

import com.truecar.mleap.core.util.Atom
import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.feature.{StringIndexerModel, StandardScalerModel}
import org.apache.spark.ml.mleap.{TransformerModel, OneHotEncoderModel, VectorAssemblerModel}
import org.apache.spark.ml.regression.{LinearRegressionModel, RandomForestRegressionModel}
import org.apache.spark.ml.{PipelineModel, Transformer}

/**
  * Created by hwilkins on 11/18/15.
  */
trait TransformerToMleap[T] {
  def toMleap(t: T): transformer.Transformer
}

case class LiftedTransformerToMleap[T]
(transformerToMleap: TransformerToMleap[T])
  extends TransformerToMleap[Transformer] {
  override def toMleap(t: Transformer): transformer.Transformer = transformerToMleap.toMleap(t.asInstanceOf[T])
}

case class AtomicTransformerToMleap[T, TM <: TransformerToMleap[T]]
(atom: Atom[TM])
  extends TransformerToMleap[T] {
  override def toMleap(t: T): transformer.Transformer = atom.get.toMleap(t)
}

case class MultiTransformerToMleap
(map: Map[String, TransformerToMleap[Transformer]])
  extends TransformerToMleap[Transformer] {
  override def toMleap(t: Transformer): transformer.Transformer = {
    map(t.getClass.getCanonicalName).toMleap(t)
  }

  def withTransformerToMleap(name: String, tm: TransformerToMleap[Transformer]): MultiTransformerToMleap = {
    copy(map = map + (name -> tm))
  }
}

trait TransformerToMleapSupport {
  import MleapTransformer._

  implicit val transformerTransformerToMleap = transformerToMleap

  implicit val linearRegressionTransformerToMleap = linearRegression
  implicit val standardScalerTransformerToMleap = standardScaler
  implicit val stringIndexerTransformerToMleap = stringIndexer
  implicit val vectorAssemblerTransformerToMleap = vectorAssembler
  implicit val oneHotEncoderTransformerToMleap = oneHotEncoder
  implicit val pipelineTransformerToMleap = pipeline
  implicit val transformerModelTransformerToMleap = transformerModel
  implicit val randomForestRegressionTransformerToMleap = randomForestRegression
}
object TransformerToMleapSupport extends TransformerToMleapSupport

object MleapTransformer {
  import scala.language.implicitConversions

  val transformerToMleap = AtomicTransformerToMleap[Transformer, MultiTransformerToMleap](Atom(MultiTransformerToMleap(Map())))

  implicit def liftTransformerToMleap[T](tm: TransformerToMleap[T]): LiftedTransformerToMleap[T] = {
    LiftedTransformerToMleap(tm)
  }

  def addTransformerToMleap[T](klazz: Class[T], tm: TransformerToMleap[T]): TransformerToMleap[T] = {
    transformerToMleap.atom.set(transformerToMleap.atom.get.withTransformerToMleap(klazz.getCanonicalName, tm))
    tm
  }

  val linearRegression = addTransformerToMleap(classOf[LinearRegressionModel], LinearRegressionModelToMleap)
  val standardScaler = addTransformerToMleap(classOf[StandardScalerModel], StandardScalerModelToMleap)
  val stringIndexer = addTransformerToMleap(classOf[StringIndexerModel], StringIndexerModelToMleap)
  val vectorAssembler = addTransformerToMleap(classOf[VectorAssemblerModel], VectorAssemblerModelToMleap)
  val oneHotEncoder = addTransformerToMleap(classOf[OneHotEncoderModel], OneHotEncoderModelToMleap)
  val pipeline = addTransformerToMleap(classOf[PipelineModel], PipelineModelToMleap(transformerToMleap))
  val transformerModel = addTransformerToMleap(classOf[TransformerModel], TransformerModelToMleap)
  val randomForestRegression = addTransformerToMleap(classOf[RandomForestRegressionModel], RandomForestRegressionModelToMleap)
}
