package com.truecar.mleap.core.feature

/**
 * Created by hwilkins on 11/5/15.
 */
case class StringIndexer(strings: Seq[String]) extends Serializable {
  val stringToIndex: Map[String, Int] = strings.zipWithIndex.toMap

  def apply(value: String): Double = stringToIndex(value)

  def toReverse: ReverseStringIndexer = ReverseStringIndexer(strings)
}
