package com.truecar.mleap.runtime.transformer.builder

import com.truecar.mleap.runtime.Row
import com.truecar.mleap.runtime.transformer.TransformerSchema
import com.truecar.mleap.runtime.types.{StructType, DataType, StructField}

import scala.util.{Failure, Success, Try}

/**
  * Created by hwilkins on 11/15/15.
  */
case class TransformerSchemaBuilder(schema: StructType = StructType.empty,
                                    inputSchema: StructType = StructType.empty,
                                    drops: Set[String] = Set(),
                                    selected: Boolean = false) extends Serializable {
  def build(): TransformerSchema = {
    TransformerSchema(inputSchema, schema)
  }
}
object TransformerSchemaBuilder {
  implicit object TransformerSchemaBuilderTransformBuilder extends TransformBuilder[TransformerSchemaBuilder] {
    override def withInput(t: TransformerSchemaBuilder,
                           name: String,
                           dataType: DataType): Try[(TransformerSchemaBuilder, Int)] = {
      if(t.schema.contains(name)) {
        Success(t, t.schema.indexOf(name))
      } else if(t.selected || t.drops.contains(name)) {
        Failure(new Error(s"Field $name was dropped"))
      } else {
        val field = StructField(name, dataType)
        val schema2 = t.schema.withField(field)
        val inputSchema2 = t.inputSchema.withField(field)
        val index = t.schema.fields.length
        val builder = t.copy(schema = schema2, inputSchema = inputSchema2)

        Success(builder, index)
      }
    }

    override def withOutput(t: TransformerSchemaBuilder,
                            name: String,
                            dataType: DataType)(o: (Row) => Any): Try[TransformerSchemaBuilder] = {
      t.schema.getIndexOf(name) match {
        case Some(index) =>
          Failure(new Error(s"Field $name already exists"))
        case None =>
          val field = StructField(name, dataType)
          val schema2 = t.schema.withField(field)
          val drops2 = t.drops - name
          val builder = t.copy(schema = schema2, drops = drops2)

          Success(builder)
      }
    }

    override def withSelect(t: TransformerSchemaBuilder,
                            fieldNames: Seq[String]): Try[TransformerSchemaBuilder] = {
      t.schema.select(fieldNames: _*).map {
        schema2 =>
          t.copy(schema = schema2,
            drops = Set(),
            selected = true)
      }
    }

    override def withDrop(t: TransformerSchemaBuilder,
                          name: String): Try[TransformerSchemaBuilder] = {
      t.schema.dropField(name).map {
        schema2 =>
          val drops2 = t.drops + name

          t.copy(schema = schema2, drops = drops2)
      }
    }
  }
}
