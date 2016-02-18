package org.apache.spark.ml.mleap

import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.ml.{Transformer, Model}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.StructType

/**
  * Created by hwilkins on 12/30/15.
  */
case class TransformerModel(override val uid: String = Identifiable.randomUID("transformer"),
                            transformer: Transformer) extends Model[TransformerModel] {
  override def copy(extra: ParamMap): TransformerModel = defaultCopy(extra)

  override def transform(dataset: DataFrame): DataFrame = transformer.transform(dataset)

  @DeveloperApi
  override def transformSchema(schema: StructType): StructType = transformer.transformSchema(schema)
}
