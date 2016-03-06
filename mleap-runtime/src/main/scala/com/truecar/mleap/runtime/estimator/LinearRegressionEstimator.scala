package com.truecar.mleap.runtime.estimator

/**
  * Created by hwilkins on 11/19/15.
  */
case class LinearRegressionEstimator(name: String = Estimator.createName("linearRegression"),
                                     elasticNetParam: Double = 0.0,
                                     fitIntercept: Boolean = true,
                                     maxIter: Int = 100,
                                     regParam: Double = 0.0,
                                     standardization: Boolean = true,
                                     tol: Double = 1E-6,
                                     solver: String = "auto",
                                     weightCol: Option[String] = None,
                                     featuresCol: String = "features",
                                     labelCol: String = "label",
                                     predictionCol: String = "prediction") extends Estimator
