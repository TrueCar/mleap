package com.truecar.mleap.runtime.transformer

import com.truecar.mleap.core.feature.StringIndexer
import com.truecar.mleap.runtime.attribute.{CategoricalAttribute, AttributeSchema}
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder
import com.truecar.mleap.runtime.types.DoubleType
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder.Ops

import scala.util.Try

/**
  * Created by hwilkins on 10/22/15.
  */
case class StringIndexerModel(uid: String = Transformer.uniqueName("string_indexer"),
                              inputCol: String,
                              outputCol: String,
                              indexer: StringIndexer) extends Transformer {
  override def build[TB: TransformBuilder](builder: TB): Try[TB] = {
    builder.withInput(inputCol).flatMap {
      case (b, inputIndex) =>
        b.withOutput(outputCol, DoubleType)(row => indexer(row.get(inputIndex).toString))
    }
  }

  override def transformAttributeSchema(schema: AttributeSchema): AttributeSchema = {
    schema.withField(outputCol, CategoricalAttribute())
  }

  def toReverse: ReverseStringIndexerModel = ReverseStringIndexerModel(inputCol = inputCol,
    outputCol = outputCol,
    indexer = indexer.toReverse)

  def toReverse(name: String): ReverseStringIndexerModel = ReverseStringIndexerModel(uid = name,
    inputCol = inputCol,
    outputCol = outputCol,
    indexer = indexer.toReverse)
}
