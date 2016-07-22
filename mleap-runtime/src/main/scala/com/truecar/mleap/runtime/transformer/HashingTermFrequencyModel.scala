package com.truecar.mleap.runtime.transformer

import com.truecar.mleap.core.feature.HashingTermFrequency
import com.truecar.mleap.runtime.attribute.{CategoricalAttribute, AttributeGroup, AttributeSchema}
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder.Ops
import com.truecar.mleap.runtime.types.{DoubleType, StringType}

import scala.util.Try

/**
  * Created by hwilkins on 12/30/15.
  */
case class HashingTermFrequencyModel(uid: String = Transformer.uniqueName("hashing_term_frequency"),
                                     inputCol: String,
                                     outputCol: String,
                                     hashingTermFrequency: HashingTermFrequency) extends Transformer {
  override def build[TB: TransformBuilder](builder: TB): Try[TB] = {
    builder.withInput(inputCol, StringType).flatMap {
      case (b, inputIndex) =>
        b.withOutput(outputCol, DoubleType)(row => hashingTermFrequency(row.getString(inputIndex)))
    }
  }

  override def transformAttributeSchema(schema: AttributeSchema): AttributeSchema = {
    val attrGroup = AttributeGroup(Array.tabulate(hashingTermFrequency.numFeatures)(_ => CategoricalAttribute()))
    schema.withField(outputCol, attrGroup)
  }
}
