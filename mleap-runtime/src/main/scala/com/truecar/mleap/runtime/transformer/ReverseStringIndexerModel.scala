package com.truecar.mleap.runtime.transformer

import com.truecar.mleap.core.feature.ReverseStringIndexer
import com.truecar.mleap.runtime.attribute.{AttributeSchema, CategoricalAttribute}
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder
import com.truecar.mleap.runtime.types.StringType
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder.Ops

import scala.util.Try

/**
  * Created by hollinwilkins on 3/30/16.
  */
case class ReverseStringIndexerModel(uid: String = Transformer.uniqueName("reverse_string_indexer"),
                                     inputCol: String,
                                     outputCol: String,
                                     indexer: ReverseStringIndexer) extends Transformer {
  override def build[TB: TransformBuilder](builder: TB): Try[TB] = {
    builder.withInput(inputCol).flatMap {
      case (b, inputIndex) =>
        b.withOutput(outputCol, StringType)(row => indexer(row.getDouble(inputIndex).toInt))
    }
  }

  override def transformAttributeSchema(schema: AttributeSchema): AttributeSchema = {
    schema.withField(outputCol, CategoricalAttribute())
  }
}
