package com.truecar.mleap.serialization.mleap.json

import com.truecar.mleap.bundle.MleapSerializer
import com.truecar.mleap.bundle.core.json.JsonStreamSerializer.ImplicitJsonStreamSerializer
import com.truecar.mleap.runtime.LocalLeapFrame
import com.truecar.mleap.serialization.ConversionSerializer
import com.truecar.mleap.serialization.mleap.MleapJsonSupport._
import com.truecar.mleap.serialization.mleap.Converters._
import mleap.runtime.LeapFrame.LeapFrame

/**
  * Created by hwilkins on 3/7/16.
  */
object DefaultJsonMleapSerializer {
  def createSerializer(): MleapSerializer = {
    val serializer = new MleapSerializer()

    serializer.addSerializer(ConversionSerializer[LocalLeapFrame, LeapFrame](protoMleapLeapFrameFormat))

    serializer
  }
}
