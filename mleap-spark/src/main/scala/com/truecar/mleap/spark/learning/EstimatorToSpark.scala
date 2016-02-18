package com.truecar.mleap.spark.learning

import com.truecar.mleap.core.util.Atom
import com.truecar.mleap.learning.estimator.Estimator
import com.truecar.mleap.runtime.transformer.Transformer
import org.apache.spark.ml
import org.apache.spark.ml.mleap.runtime.TransformerToMleap
import org.apache.spark.sql.DataFrame

/**
  * Created by hwilkins on 12/3/15.
  */
case class LiftedEstimatorToSpark[E](es: EstimatorToSpark[E]) extends EstimatorToSpark[Estimator] {
  override def toSpark(e: Estimator): ml.Estimator[_] = {
    es.toSpark(e.asInstanceOf[E])
  }
}

case class AtomEstimatorToSpark[E, ES <: EstimatorToSpark[E]](atom: Atom[ES]) extends EstimatorToSpark[E] {
  override def toSpark(e: E): ml.Estimator[_] = {
    atom.get.toSpark(e)
  }
}

trait EstimatorToSpark[E] {
  def toSpark(e: E): ml.Estimator[_]
}

object EstimatorToSpark {
  implicit class SparkEstimatorWrapper[E: EstimatorToSpark](e: E) {
    def mleapFit(dataset: DataFrame)(implicit transformerToMleap: TransformerToMleap[ml.Transformer]): Transformer = {
      transformerToMleap.toMleap(e.toSpark.fit(dataset).asInstanceOf[ml.Transformer])
    }
  }

  implicit class Ops[E: EstimatorToSpark](e: E) {
    def toSpark: ml.Estimator[_] = EstimatorToSpark.toSpark(e)
  }

  def toSpark[E: EstimatorToSpark](e: E): ml.Estimator[_] = {
    implicitly[EstimatorToSpark[E]].toSpark(e)
  }
}
