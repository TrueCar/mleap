package com.truecar.mleap.runtime.serialization

import com.truecar.mleap.core.serialization.{CoreJsonSupport, MleapJsonProtocol}

/**
  * Created by hwilkins on 12/10/15.
  */
trait LearningJsonSupport extends MleapJsonProtocol
with EstimatorJsonSupport
with RuntimeJsonSupport
with CoreJsonSupport
object LearningJsonSupport extends LearningJsonSupport
