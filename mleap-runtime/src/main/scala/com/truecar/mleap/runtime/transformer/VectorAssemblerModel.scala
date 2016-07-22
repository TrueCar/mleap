package com.truecar.mleap.runtime.transformer

import com.truecar.mleap.core.feature.VectorAssembler
import com.truecar.mleap.runtime.attribute.{BaseAttribute, AttributeGroup, AttributeSchema}
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder
import com.truecar.mleap.runtime.types.{StructType, VectorType}
import com.truecar.mleap.runtime.transformer.builder.TransformBuilder.Ops

import scala.util.Try

/**
  * Created by hwilkins on 10/23/15.
  */
case class VectorAssemblerModel(uid: String = Transformer.uniqueName("vector_assembler"),
                                inputCols: Array[String],
                                outputCol: String) extends Transformer {
  private val assembler: VectorAssembler = VectorAssembler.default

  override def build[TB: TransformBuilder](builder: TB): Try[TB] = {
    inputCols.foldLeft(Try((builder, Seq[Int]()))) {
      (result, col) => result.flatMap {
        case (b, indices) =>
          b.withInput(col)
            .map {
              case (b3, index) => (b3, indices :+ index)
            }
      }
    }.flatMap {
      case (b, indices) =>
        b.withOutput(outputCol, VectorType)(row => assembler(indices.map(row.get): _*))
    }
  }

  override def transformAttributeSchema(schema: AttributeSchema): AttributeSchema = {
    val attrs: Array[BaseAttribute] = inputCols.toArray.map(col => schema(col)).flatMap {
      case AttributeGroup(groupAttrs) => groupAttrs: Array[BaseAttribute]
      case attr: BaseAttribute => Array(attr): Array[BaseAttribute]
      case _ =>
        // TODO: better error here
        throw new Error("Unsupported attribute type")
    }

    schema.withField(outputCol, AttributeGroup(attrs))
  }
}
