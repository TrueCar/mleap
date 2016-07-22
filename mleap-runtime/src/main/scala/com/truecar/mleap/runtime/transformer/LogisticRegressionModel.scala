package com.truecar.mleap.runtime.transformer

import com.truecar.mleap.core.classification.LogisticRegression
import com.truecar.mleap.runtime.attribute.{AttributeSchema, CategoricalAttribute}
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder
import com.truecar.mleap.runtime.types.{DoubleType, VectorType}
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder.Ops

import scala.util.Try

/**
  * Created by hwilkins on 10/22/15.
  */
case class LogisticRegressionModel(uid: String = Transformer.uniqueName("logistic_regression"),
                                   featuresCol: String,
                                   predictionCol: String,
                                   model: LogisticRegression) extends Transformer {
  override def build[TB: TransformBuilder](builder: TB): Try[TB] = {
    builder.withInput(featuresCol, VectorType).flatMap {
      case(b, featuresIndex) =>
        b.withOutput(predictionCol, DoubleType)(row => model(row.getVector(featuresIndex)))
    }
  }

  override def transformAttributeSchema(schema: AttributeSchema): AttributeSchema = {
    schema.withField(predictionCol, CategoricalAttribute())
  }
}
