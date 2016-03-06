package com.truecar.mleap.serialization.serializer.stream.json.runtime.regression

import com.truecar.mleap.runtime.transformer.LinearRegressionModel
import com.truecar.mleap.serialization.json.CoreJsonSupport._
import com.truecar.mleap.serialization.serializer.stream.json.JsonStreamSerializer

/**
  * Created by hwilkins on 3/5/16.
  */
object LinearRegressionModelSerializer extends JsonStreamSerializer[LinearRegressionModel] {
  override val klazz: Class[LinearRegressionModel] = classOf[LinearRegressionModel]
  override implicit val format = mleapLinearRegressionModelFormat
}
