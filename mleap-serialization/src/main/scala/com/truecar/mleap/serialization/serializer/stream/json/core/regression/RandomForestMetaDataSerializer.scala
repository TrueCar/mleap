package com.truecar.mleap.serialization.serializer.stream.json.core.regression

import com.truecar.mleap.serialization.core.regression.RandomForestMetaData
import com.truecar.mleap.serialization.serializer.stream.json.JsonStreamSerializer
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._

/**
  * Created by hwilkins on 3/6/16.
  */
object RandomForestMetaDataSerializer extends JsonStreamSerializer[RandomForestMetaData] {
  override val klazz: Class[RandomForestMetaData] = classOf[RandomForestMetaData]
  override implicit val format: RootJsonFormat[RandomForestMetaData] = jsonFormat1(RandomForestMetaData)
}
