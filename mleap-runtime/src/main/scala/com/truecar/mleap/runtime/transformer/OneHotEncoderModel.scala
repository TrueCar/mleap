package com.truecar.mleap.runtime.transformer

import com.truecar.mleap.core.feature.OneHotEncoder
import com.truecar.mleap.runtime.attribute.{CategoricalAttribute, AttributeGroup, AttributeSchema}
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder
import com.truecar.mleap.runtime.types.{VectorType, DoubleType}
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder.Ops

import scala.util.Try

/**
  * Created by hwilkins on 10/23/15.
  */
case class OneHotEncoderModel(inputCol: String,
                              outputCol: String,
                              encoder: OneHotEncoder) extends Transformer {
  override def build[T: TransformBuilder](builder: T): Try[T] = {
    builder.withInput(inputCol, DoubleType).flatMap {
      case (b, inputIndex) =>
        b.withOutput(outputCol, VectorType)(row => encoder(row.getDouble(inputIndex)))
    }
  }

  override def transformAttributeSchema(schema: AttributeSchema): AttributeSchema = {
    val attrGroup = AttributeGroup(Array.tabulate(encoder.size)(_ => CategoricalAttribute()))
    schema.withField(outputCol, attrGroup)
  }
}
