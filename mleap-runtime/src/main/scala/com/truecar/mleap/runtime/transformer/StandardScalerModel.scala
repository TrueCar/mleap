package com.truecar.mleap.runtime.transformer

import com.truecar.mleap.core.feature.StandardScaler
import com.truecar.mleap.runtime.attribute.AttributeSchema
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder
import com.truecar.mleap.runtime.types.VectorType
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder.Ops

import scala.util.Try

/**
  * Created by hwilkins on 10/23/15.
  */
case class StandardScalerModel(uid: String = Transformer.uniqueName("standard_scaler"),
                               inputCol: String,
                               outputCol: String,
                               scaler: StandardScaler) extends Transformer {
  override def build[TB: TransformBuilder](builder: TB): Try[TB] = {
    builder.withInput(inputCol, VectorType).flatMap {
      case (b, inputIndex) =>
        b.withOutput(outputCol, VectorType)(row => scaler(row.getVector(inputIndex)))
    }
  }

  override def transformAttributeSchema(schema: AttributeSchema): AttributeSchema = {
    schema.withField(outputCol, schema(inputCol))
  }
}
