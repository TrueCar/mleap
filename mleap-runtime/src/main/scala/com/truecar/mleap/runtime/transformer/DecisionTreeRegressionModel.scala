package com.truecar.mleap.runtime.transformer

import com.truecar.mleap.core.regression.DecisionTreeRegression
import com.truecar.mleap.runtime.attribute.{AttributeSchema, ContinuousAttribute}
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder
import com.truecar.mleap.runtime.types.{DoubleType, VectorType}
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder.Ops

import scala.util.Try

/**
  * Created by hwilkins on 11/8/15.
  */
case class DecisionTreeRegressionModel(featuresCol: String,
                                       predictionCol: String,
                                       model: DecisionTreeRegression) extends Transformer {
  override def build[TB: TransformBuilder](builder: TB): Try[TB] = {
    builder.withInput(featuresCol, VectorType).flatMap {
      case (b, featuresIndex) =>
        b.withOutput(predictionCol, DoubleType)(row => model(row.getVector(featuresIndex)))
    }
  }

  override def transformAttributeSchema(schema: AttributeSchema): AttributeSchema = {
    schema.withField(predictionCol, ContinuousAttribute())
  }
}
