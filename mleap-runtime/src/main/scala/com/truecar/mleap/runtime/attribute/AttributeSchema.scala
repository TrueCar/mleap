package com.truecar.mleap.runtime.attribute

/**
  * Created by hwilkins on 12/3/15.
  */
case class AttributeSchema(info: Map[String, Attribute]) {
  def apply(name: String): Attribute = info(name)

  def select(names: String *): AttributeSchema = {
    val namesSet = names.toSet
    val info2 = info.filterKeys(namesSet.contains)
    copy(info = info2)
  }
  def withField(name: String, attr: Attribute): AttributeSchema = copy(info = info + (name -> attr))
  def dropField(name: String): AttributeSchema = copy(info = info - name)
}
