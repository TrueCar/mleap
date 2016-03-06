package com.truecar.mleap.spark.learning

import com.truecar.mleap.core.util.Atom
import com.truecar.mleap.runtime.estimator.Estimator
import com.truecar.mleap.runtime.transformer.Transformer
import org.apache.spark.ml
import org.apache.spark.ml.mleap.runtime.TransformerToMleap
import org.apache.spark.sql.DataFrame

/**
  * Created by hwilkins on 12/3/15.
  */
case class LiftedEstimatorToSpark[E](es: EstimatorToSpark[E]) extends EstimatorToSpark[Estimator] {
  override def toSpark(e: Estimator): ml.PipelineStage = {
    es.toSpark(e.asInstanceOf[E])
  }
}

case class AtomEstimatorToSpark[E, ES <: EstimatorToSpark[E]](atom: Atom[ES]) extends EstimatorToSpark[E] {
  override def toSpark(e: E): ml.PipelineStage = {
    atom.get.toSpark(e)
  }
}

trait EstimatorToSpark[E] {
  def toSpark(e: E): ml.PipelineStage
}

object EstimatorToSpark {
  implicit class SparkEstimatorWrapper[E: EstimatorToSpark](e: E) {
    def mleapFit(dataset: DataFrame)(implicit transformerToMleap: TransformerToMleap[ml.Transformer]): Transformer = {
      val sparkTransformer = e.toSpark match {
        case estimator: ml.Estimator[_] => estimator.fit(dataset).asInstanceOf[ml.Transformer]
        case transformer: ml.Transformer => transformer
      }

      transformerToMleap.toMleap(sparkTransformer)
    }
  }

  implicit class Ops[E: EstimatorToSpark](e: E) {
    def toSpark: ml.PipelineStage = EstimatorToSpark.toSpark(e)
  }

  def toSpark[E: EstimatorToSpark](e: E): ml.PipelineStage = {
    implicitly[EstimatorToSpark[E]].toSpark(e)
  }
}
