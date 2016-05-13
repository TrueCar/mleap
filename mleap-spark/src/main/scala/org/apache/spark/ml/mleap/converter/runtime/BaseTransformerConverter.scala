package org.apache.spark.ml.mleap.converter.runtime

import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.classification.{LogisticRegressionModel, RandomForestClassificationModel}
import org.apache.spark.ml.feature.{IndexToString, StandardScalerModel, StringIndexerModel, VectorAssembler}
import org.apache.spark.ml.mleap.classification.SVMModel
import org.apache.spark.ml.mleap.converter.runtime.classification.{LogisticRegressionModelToMleap, RandomForestClassificationModelToMleap, SupportVectorMachineModelToMleap}
import org.apache.spark.ml.mleap.converter.runtime.feature._
import org.apache.spark.ml.mleap.converter.runtime.regression.{LinearRegressionModelToMleap, RandomForestRegressionModelToMleap}
import org.apache.spark.ml.mleap.feature.OneHotEncoderModel
import org.apache.spark.ml.regression.{LinearRegressionModel, RandomForestRegressionModel}

/**
  * Created by hollinwilkins on 4/17/16.
  */
trait BaseTransformerConverter extends SparkTransformerConverter {
  // regression
  implicit val mleapLinearRegressionModelToMleap: TransformerToMleap[LinearRegressionModel, transformer.LinearRegressionModel] =
    addConverter(LinearRegressionModelToMleap)
  implicit val mleapRandomForestRegressionModelToMleap: TransformerToMleap[RandomForestRegressionModel, transformer.RandomForestRegressionModel] =
    addConverter(RandomForestRegressionModelToMleap)

  // classification
  implicit val mleapLogisticRegressionModelToMleap: TransformerToMleap[LogisticRegressionModel, transformer.LogisticRegressionModel] =
    addConverter(LogisticRegressionModelToMleap)
  implicit val mleapRandomForestClassificationModelToMleap: TransformerToMleap[RandomForestClassificationModel, transformer.RandomForestClassificationModel] =
    addConverter(RandomForestClassificationModelToMleap)
  implicit val mleapSupportVectorMachineModelToMleap: TransformerToMleap[SVMModel, transformer.SupportVectorMachineModel] =
    addConverter(SupportVectorMachineModelToMleap)

  //feature
  implicit val mleapOneHotEncoderToMleap: TransformerToMleap[OneHotEncoderModel, transformer.OneHotEncoderModel] =
    addConverter(OneHotEncoderToMleap)
  implicit val mleapIndexToStringToMleap: TransformerToMleap[IndexToString, transformer.ReverseStringIndexerModel] =
    addConverter(IndexToStringToMleap)
  implicit val mleapStandardScalerModelToMleap: TransformerToMleap[StandardScalerModel, transformer.StandardScalerModel] =
    addConverter(StandardScalerModelToMleap)
  implicit val mleapStringIndexerModelToMleap: TransformerToMleap[StringIndexerModel, transformer.StringIndexerModel] =
    addConverter(StringIndexerModelToMleap)
  implicit val mleapVectorAssemblerToMleap: TransformerToMleap[VectorAssembler, transformer.VectorAssemblerModel] =
    addConverter(VectorAssemblerModelToMleap)

  // other
  implicit val mleapPipelineModelToMleap: TransformerToMleap[PipelineModel, transformer.PipelineModel] =
    addConverter(PipelineModelToMleap(this))
}
object BaseTransformerConverter extends BaseTransformerConverter
