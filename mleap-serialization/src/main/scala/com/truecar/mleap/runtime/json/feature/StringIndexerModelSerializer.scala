package com.truecar.mleap.runtime.json.feature

import com.truecar.mleap.core.feature.StringIndexer
import com.truecar.mleap.runtime.transformer.StringIndexerModel
import com.truecar.mleap.serialization.json.JsonSerializer
import spray.json.DefaultJsonProtocol._

/**
  * Created by hwilkins on 3/5/16.
  */
object StringIndexerModelSerializer extends JsonSerializer[StringIndexerModel] {
  private implicit val siFormat = jsonFormat1(StringIndexer)

  override val klazz: Class[StringIndexerModel] = classOf[StringIndexerModel]
  override implicit val format = jsonFormat3(StringIndexerModel)
}
