package com.truecar.mleap.serialization.serializer.bundle.runtime.regression

import com.truecar.mleap.bundle.{StreamSerializer, Bundle, BundleSerializer}
import com.truecar.mleap.core.regression.RandomForestRegression
import com.truecar.mleap.runtime.transformer.RandomForestRegressionModel
import ml.runtime.regression.RandomForestRegressionModelMetaData.RandomForestRegressionModelMetaData

/**
  * Created by hwilkins on 3/6/16.
  */
case class RandomForestRegressionModelSerializer(randomForestRegressionModelMetaDataSerializer: StreamSerializer[RandomForestRegressionModelMetaData],
                                                 randomForestRegressionSerializer: BundleSerializer[RandomForestRegression])
  extends BundleSerializer[RandomForestRegressionModel] {
  override val key: String = "ml.runtime.regression.RandomForestRegressionModel"

  override def serialize(obj: RandomForestRegressionModel, bundle: Bundle): Unit = {
    val meta = bundle.contentWriter("meta")
    val metaData = RandomForestRegressionModelMetaData(obj.featuresCol, obj.predictionCol)
    randomForestRegressionModelMetaDataSerializer.serialize(metaData, meta)
    meta.close()

    randomForestRegressionSerializer.serialize(obj.model, bundle.createBundle("model"))
  }

  override def deserialize(bundle: Bundle): RandomForestRegressionModel = {
    val meta = bundle.contentReader("meta")
    val metaData = randomForestRegressionModelMetaDataSerializer.deserialize(meta)
    meta.close()

    val model = randomForestRegressionSerializer.deserialize(bundle.getBundle("model"))

    RandomForestRegressionModel(metaData.featuresCol, metaData.predictionCol, model)
  }
}
