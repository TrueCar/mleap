package com.truecar.mleap.runtime.transformer

import com.truecar.mleap.runtime.attribute.AttributeSchema
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder.Ops

import scala.util.Try

/**
  * Created by hwilkins on 11/15/15.
  */
case class SelectorModel(fieldNames: Seq[String]) extends Transformer {
  override def build[TB: TransformBuilder](builder: TB): Try[TB] = {
    builder.withSelect(fieldNames)
  }

  override def transformAttributeSchema(schema: AttributeSchema): AttributeSchema = schema.select(fieldNames: _*)
}
