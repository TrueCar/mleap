package com.truecar.mleap.serialization.mleap.json

import ml.bundle.Serializer
import ml.bundle.json.JsonStreamSerializer.ImplicitJsonStreamSerializer
import com.truecar.mleap.runtime.LocalLeapFrame
import com.truecar.mleap.serialization.ConversionSerializer
import com.truecar.mleap.serialization.mleap.MleapJsonSupport._
import com.truecar.mleap.serialization.mleap.Converters._
import mleap.runtime.LeapFrame.LeapFrame

/**
  * Created by hwilkins on 3/8/16.
  */
trait MleapSimpleJsonSerializer extends Serializer {
  addSerializer(ConversionSerializer[LocalLeapFrame, LeapFrame](protoMleapLeapFrameFormat))
}
object MleapSimpleJsonSerializer extends MleapSimpleJsonSerializer
