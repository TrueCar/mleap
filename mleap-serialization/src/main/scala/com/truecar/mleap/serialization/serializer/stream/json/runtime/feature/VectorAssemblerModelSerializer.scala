package com.truecar.mleap.serialization.serializer.stream.json.runtime.feature

import com.truecar.mleap.runtime.transformer.VectorAssemblerModel
import com.truecar.mleap.serialization.json.CoreJsonSupport._
import com.truecar.mleap.serialization.serializer.stream.json.JsonStreamSerializer

/**
  * Created by hwilkins on 3/5/16.
  */
object VectorAssemblerModelSerializer extends JsonStreamSerializer[VectorAssemblerModel] {
  override val klazz: Class[VectorAssemblerModel] = classOf[VectorAssemblerModel]
  override implicit val format = mleapVectorAssemblerModelFormat
}
