package com.truecar.mleap.spark.learning

import com.truecar.mleap.core.util.Atom
import com.truecar.mleap.runtime.estimator._
import com.truecar.mleap.spark.learning.feature._
import com.truecar.mleap.spark.learning.regression.{SparkRandomForestRegression, SparkLinearRegression}

/**
  * Created by hollinwilkins on 12/4/15.
  */
trait EstimatorToSparkSupport {
  import SparkEstimator._

  implicit val estimatorEstimatorToSpark = estimatorToSpark

  implicit val standardScalerEstimator = standardScaler
  implicit val stringIndexerEstimator = stringIndexer
  implicit val vectorAssemblerEstimator = vectorAssembler
  implicit val linearRegressionEstimator = linearRegression
  implicit val randomForestRegressionEstimator = randomForest
  implicit val pipelineEstimator = pipeline
}

object SparkEstimator {
  import scala.language.implicitConversions

  val estimatorToSpark = AtomEstimatorToSpark[Estimator, MultiEstimatorToSpark](Atom(MultiEstimatorToSpark(Map())))

  implicit def liftEstimatorToSpark[E](es: EstimatorToSpark[E]): LiftedEstimatorToSpark[E] = {
    LiftedEstimatorToSpark(es)
  }

  def addEstimatorToSpark[E](klazz: Class[E], es: EstimatorToSpark[E]): EstimatorToSpark[E] = {
    estimatorToSpark.atom.set(estimatorToSpark.atom.get.withEstimatorToSpark(klazz.getCanonicalName, es))
    es
  }

  val standardScaler = addEstimatorToSpark(classOf[StandardScalerEstimator], SparkStandardScaler)
  val stringIndexer = addEstimatorToSpark(classOf[StringIndexerEstimator], SparkStringIndexer)
  val vectorAssembler = addEstimatorToSpark(classOf[VectorAssemblerEstimator], SparkVectorAssembler)
  val linearRegression = addEstimatorToSpark(classOf[LinearRegressionEstimator], SparkLinearRegression)
  val randomForest = addEstimatorToSpark(classOf[RandomForestRegressionEstimator], SparkRandomForestRegression)
  val pipeline = addEstimatorToSpark(classOf[PipelineEstimator], SparkPipelineEstimator(estimatorToSpark))
}
