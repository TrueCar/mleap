package com.truecar.mleap.serialization.serializer.stream.json.runtime.regression

import com.truecar.mleap.serialization.runtime.RandomForestRegressionModelMetaData
import com.truecar.mleap.serialization.serializer.stream.json.JsonStreamSerializer
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._

/**
  * Created by hwilkins on 3/6/16.
  */
object RandomForestRegressionModelMetaDataSerializer extends JsonStreamSerializer[RandomForestRegressionModelMetaData] {
  override val klazz: Class[RandomForestRegressionModelMetaData] = classOf[RandomForestRegressionModelMetaData]
  override implicit val format: RootJsonFormat[RandomForestRegressionModelMetaData] = jsonFormat2(RandomForestRegressionModelMetaData)
}
