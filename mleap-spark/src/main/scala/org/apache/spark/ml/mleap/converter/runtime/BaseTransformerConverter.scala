package org.apache.spark.ml.mleap.converter.runtime

import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.classification.RandomForestClassificationModel
import org.apache.spark.ml.feature.{IndexToString, StandardScalerModel, StringIndexerModel, VectorAssembler}
import org.apache.spark.ml.mleap.classification.SVMModel
import org.apache.spark.ml.mleap.converter.runtime.classification.{RandomForestClassificationModelToMleap, SupportVectorMachineModelToMleap}
import org.apache.spark.ml.mleap.converter.runtime.feature.{IndexToStringToMleap, StandardScalerModelToMleap, StringIndexerModelToMleap, VectorAssemblerModelToMleap}
import org.apache.spark.ml.mleap.converter.runtime.regression.{LinearRegressionModelToMleap, RandomForestRegressionModelToMleap}
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
  implicit val mleapRandomForestClassificationModelToMleap: TransformerToMleap[RandomForestClassificationModel, transformer.RandomForestClassificationModel] =
    addConverter(RandomForestClassificationModelToMleap)
  implicit val mleapSupportVectorMachineModelToMleap: TransformerToMleap[SVMModel, transformer.SupportVectorMachineModel] =
    addConverter(SupportVectorMachineModelToMleap)

  //feature
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
