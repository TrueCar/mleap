package org.apache.spark.ml.mleap.classification

import org.apache.spark.ml.classification.{ProbabilisticClassificationModel, ProbabilisticClassifier}
import org.apache.spark.ml.param._
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.mllib.classification
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.linalg.{DenseVector, SparseVector, Vector, Vectors}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.sql.DataFrame

/**
  * Created by hollinwilkins on 4/14/16.
  */
trait SVMBase extends Params {
  /**
    * Param for step size.
    * @group param
    */
  final val stepSize: DoubleParam = new DoubleParam(this, "stepSize", "step size for SGD")

  /** @group getParam */
  final def getStepSize: Double = $(stepSize)

  /**
    * Param for number of iterations.
    * @group param
    */
  final val numIterations: IntParam = new IntParam(this, "numIterations", "number of iterations for SGD")

  /** @group getParam */
  final def getNumIterations: Int = $(numIterations)

  /**
    * Param for number of iterations.
    * @group param
    */
  final val regParam: DoubleParam = new DoubleParam(this, "regParams", "regularization param for SGD")

  /** @group getParam */
  final def getRegParam: Double = $(regParam)

  /**
    * Param for number of iterations.
    * @group param
    */
  final val miniBatchFraction: DoubleParam = new DoubleParam(this, "miniBatchFraction", "Mini batch fraction for SGD")

  /** @group getParam */
  final def getMiniBatchFraction: Double = $(miniBatchFraction)
}

class SVMModel(override val uid: String,
               val model: classification.SVMModel) extends ProbabilisticClassificationModel[Vector, SVMModel] {
  def this(model: classification.SVMModel) = this(Identifiable.randomUID("svmModel"), model)

  override protected def predictRaw(features: Vector): Vector = {
    val margin = model.predict(features)
    Vectors.dense(-margin, margin)
  }

  override def numClasses: Int = 2

  override def copy(extra: ParamMap): SVMModel = defaultCopy(extra)

  override protected def raw2probabilityInPlace(rawPrediction: Vector): Vector = {
    rawPrediction match {
      case dv: DenseVector =>
        var i = 0
        val size = dv.size
        while (i < size) {
          dv.values(i) = 1.0 / (1.0 + math.exp(-dv.values(i)))
          i += 1
        }
        dv
      case sv: SparseVector =>
        throw new RuntimeException("Unexpected error in LogisticRegressionModel:" +
          " raw2probabilitiesInPlace encountered SparseVector")
    }
  }
}

class SVM(override val uid: String)
  extends ProbabilisticClassifier[Vector, SVM, SVMModel]
    with SVMBase {
  def this() = this(Identifiable.randomUID("svm"))

  /** @group setParam */
  def setStepSize(value: Double): this.type = set(stepSize, value)

  /** @group setParam */
  def setNumIterations(value: Int): this.type = set(numIterations, value)

  /** @group setParam */
  def setRegParam(value: Double): this.type = set(regParam, value)

  /** @group setParam */
  def setMiniBatchFraction(value: Double): this.type = set(miniBatchFraction, value)

  override def copy(extra: ParamMap): SVM = defaultCopy(extra)

  override protected def train(dataset: DataFrame): SVMModel = {
    val labeledPoints = dataset.select($(featuresCol), $(labelCol))
      .map {
        row =>
          LabeledPoint(row.getDouble(1), row.getAs[Vector](0))
      }

    val mllibModel = SVMWithSGD.train(labeledPoints,
      $(numIterations),
      $(stepSize),
      $(regParam),
      $(miniBatchFraction))

    new SVMModel(mllibModel)
  }
}
