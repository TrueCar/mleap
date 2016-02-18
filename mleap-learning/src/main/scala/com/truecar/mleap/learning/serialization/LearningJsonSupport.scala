package com.truecar.mleap.learning.serialization

import com.truecar.mleap.core.serialization.{CoreJsonSupport, MleapJsonProtocol}
import com.truecar.mleap.runtime.serialization.RuntimeJsonSupport

/**
  * Created by hwilkins on 12/10/15.
  */
trait LearningJsonSupport extends MleapJsonProtocol
with EstimatorJsonSupport
with RuntimeJsonSupport
with CoreJsonSupport
object LearningJsonSupport extends LearningJsonSupport
