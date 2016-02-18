package com.truecar.mleap.runtime.transformer

import com.truecar.mleap.runtime.attribute.AttributeSchema
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder

import scala.util.Try

/**
 * Created by hwilkins on 11/8/15.
 */
case class PipelineModel(stages: Seq[Transformer]) extends Transformer {
  override def build[TB: TransformBuilder](builder: TB): Try[TB] = {
    stages.foldLeft(Try(builder))((b, stage) => b.flatMap(stage.build(_)))
  }

  override def transformAttributeSchema(schema: AttributeSchema): AttributeSchema = {
    stages.foldLeft(schema)((m, transformer) => transformer.transformAttributeSchema(m))
  }
}
