package com.truecar.mleap.runtime.attribute

/**
  * Created by hwilkins on 12/3/15.
  */
sealed trait Attribute
sealed trait BaseAttribute

case class ContinuousAttribute() extends Attribute with BaseAttribute
case class CategoricalAttribute() extends Attribute with BaseAttribute
case class OtherAttribute() extends Attribute
case class AttributeGroup(attrs: Array[BaseAttribute]) extends Attribute
