package com.truecar.mleap.runtime.transformer

import com.truecar.mleap.core.regression.LinearRegression
import com.truecar.mleap.runtime.attribute.{ContinuousAttribute, AttributeSchema}
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder
import com.truecar.mleap.runtime.types.{DoubleType, VectorType}
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder.Ops

import scala.util.Try

/**
  * Created by hwilkins on 10/22/15.
  */
case class LinearRegressionModel(uid: String = Transformer.uniqueName("linear_regression"),
                                 featuresCol: String,
                                 predictionCol: String,
                                 model: LinearRegression) extends Transformer {
  override def build[TB: TransformBuilder](builder: TB): Try[TB] = {
    builder.withInput(featuresCol, VectorType).flatMap {
      case(b, featuresIndex) =>
        b.withOutput(predictionCol, DoubleType)(row => model(row.getVector(featuresIndex)))
    }
  }

  override def transformAttributeSchema(schema: AttributeSchema): AttributeSchema = {
    schema.withField(predictionCol, ContinuousAttribute())
  }
}
