package com.truecar.mleap.serialization.ml.bundle.runtime.regression

import ml.bundle._
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

  override def serialize(obj: RandomForestRegressionModel, bundle: BundleWriter): Unit = {
    val meta = bundle.contentWriter("meta")
    val metaData = RandomForestRegressionModelMetaData(obj.featuresCol, obj.predictionCol)
    randomForestRegressionModelMetaDataSerializer.serialize(metaData, meta)
    bundle.close(meta)

    randomForestRegressionSerializer.serialize(obj.model, bundle.createBundle("model"))
  }

  override def deserialize(bundle: BundleReader): RandomForestRegressionModel = {
    val meta = bundle.contentReader("meta")
    val metaData = randomForestRegressionModelMetaDataSerializer.deserialize(meta)
    bundle.close(meta)

    val model = randomForestRegressionSerializer.deserialize(bundle.getBundle("model"))

    RandomForestRegressionModel(metaData.featuresCol, metaData.predictionCol, model)
  }
}
