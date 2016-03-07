package com.truecar.mleap.runtime.estimator

import java.util.UUID

/**
  * Created by hwilkins on 11/19/15.
  */
trait Estimator {
  def name: String
}

object Estimator {
  def createName(base: String): String = base + "_" + UUID.randomUUID().toString
}
