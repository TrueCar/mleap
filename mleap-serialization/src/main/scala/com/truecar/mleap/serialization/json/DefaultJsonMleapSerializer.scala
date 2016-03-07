package com.truecar.mleap.serialization.json

import com.truecar.mleap.core.feature.StandardScaler
import com.truecar.mleap.runtime.transformer._
import com.truecar.mleap.serialization.serializer.bundle.core.regression.{RandomForestRegressionSerializer, DecisionTreeRegressionSerializer}
import com.truecar.mleap.serialization.serializer.bundle.core.tree.node.LinearNodeSerializer
import com.truecar.mleap.serialization.serializer.bundle.runtime.PipelineModelSerializer
import com.truecar.mleap.serialization.serializer.bundle.runtime.regression.RandomForestRegressionModelSerializer
import com.truecar.mleap.serialization.{MleapToMlSerializer, MleapSerializer}
import com.truecar.mleap.serialization.Converters._
import com.truecar.mleap.serialization.core.json.JsonSupport._
import ml.runtime.{feature, regression}

/**
  * Created by hwilkins on 3/5/16.
  */
object DefaultJsonMleapSerializer {
  val decisionTreeRegressionSerializer = DecisionTreeRegressionSerializer(LinearNodeSerializer(
    mleapNodeMetaDataFormat,
    mleapNodeDataFormat
  ))
  val randomForestRegressionSerializer = RandomForestRegressionSerializer(mleapRandomForestRegressionMetaDataFormat,
    decisionTreeRegressionSerializer)
  val randomForestRegressionModelSerializer = RandomForestRegressionModelSerializer(mleapRandomForestRegressionModelMetaDataFormat,
    randomForestRegressionSerializer)

  def createSerializer(): MleapSerializer = {
    val serializer = new MleapSerializer()

    serializer.addSerializer(MleapToMlSerializer[StringIndexerModel, feature.StringIndexerModel.StringIndexerModel](mleapStringIndexerModelFormat))
    serializer.addSerializer(MleapToMlSerializer[HashingTermFrequencyModel, feature.HashingTermFrequencyModel.HashingTermFrequencyModel](mleapHashingTermFrequencyModelFormat))
    serializer.addSerializer(MleapToMlSerializer[StandardScalerModel, feature.StandardScalerModel.StandardScalerModel](mleapStandardScalerModelFormat))
    serializer.addSerializer(MleapToMlSerializer[VectorAssemblerModel, feature.VectorAssemblerModel.VectorAssemblerModel](mleapVectorAssemblerModelFormat))
    serializer.addSerializer(MleapToMlSerializer[TokenizerModel, feature.TokenizerModel.TokenizerModel](mleapTokenizerModelFormat))
    serializer.addSerializer(MleapToMlSerializer[LinearRegressionModel, regression.LinearRegressionModel.LinearRegressionModel](mleapLinearRegressionModelFormat))

    serializer.addBundleSerializer(randomForestRegressionModelSerializer)
    serializer.addBundleSerializer(PipelineModelSerializer(serializer))

    serializer
  }
}
