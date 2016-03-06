package com.truecar.mleap.runtime.estimator

import java.util.UUID

import com.truecar.mleap.core.serialization.TypeName

/**
  * Created by hwilkins on 11/19/15.
  */
trait Estimator extends TypeName {
  def name: String
  override def typeName: String = getClass.getCanonicalName
}

object Estimator {
  def createName(base: String): String = base + "_" + UUID.randomUUID().toString
}
