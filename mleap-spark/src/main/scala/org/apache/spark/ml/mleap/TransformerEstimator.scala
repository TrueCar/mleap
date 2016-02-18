package org.apache.spark.ml.mleap

import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.ml.Estimator
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.StructType

/**
  * Created by hwilkins on 12/30/15.
  */
case class TransformerEstimator(override val uid: String = Identifiable.randomUID("transformer"),
                                model: TransformerModel) extends Estimator[TransformerModel] {
  override def fit(dataset: DataFrame): TransformerModel = model

  override def copy(extra: ParamMap): Estimator[TransformerModel] = defaultCopy(extra)

  @DeveloperApi
  override def transformSchema(schema: StructType): StructType = model.transformSchema(schema)
}
