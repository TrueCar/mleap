package com.truecar.mleap.serialization.mleap.v1

import ml.bundle.Serializer
import ml.bundle.support.JsonStreamSerializer._
import MleapJsonSupport._

/**
  * Created by hwilkins on 3/8/16.
  */
trait MleapJsonSerializer extends Serializer {
  addSerializer(mleapLeapFrameFormat)
}
object MleapJsonSerializer extends MleapJsonSerializer
