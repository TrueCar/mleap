package com.truecar.mleap.runtime.transformer

import com.truecar.mleap.core.classification.RandomForestClassification
import com.truecar.mleap.runtime.attribute.{AttributeSchema, CategoricalAttribute}
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder
import com.truecar.mleap.runtime.types.{DoubleType, VectorType}
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder.Ops

import scala.util.Try

/**
  * Created by hollinwilkins on 3/30/16.
  */
case class RandomForestClassificationModel(uid: String = Transformer.uniqueName("random_forest_classification"),
                                           featuresCol: String,
                                           predictionCol: String,
                                           model: RandomForestClassification) extends Transformer {
  override def build[TB: TransformBuilder](builder: TB): Try[TB] = {
    builder.withInput(featuresCol, VectorType).flatMap {
      case (b, featuresIndex) =>
        b.withOutput(predictionCol, DoubleType)(row => model(row.getVector(featuresIndex)))
    }
  }

  override def transformAttributeSchema(schema: AttributeSchema): AttributeSchema = {
    schema.withField(predictionCol, CategoricalAttribute())
  }
}
