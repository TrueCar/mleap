package com.truecar.mleap.serialization.mleap

import com.truecar.mleap.runtime.types.{DoubleType, StructField}
import MleapJsonSupport._
import spray.json._

/**
  * Created by hwilkins on 3/7/16.
  */
object Test {
  val field = StructField("hey", DoubleType)
  field.toJson
}
