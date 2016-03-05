package com.truecar.mleap.runtime

import com.truecar.mleap.core.linalg.Vector

/**
  * Created by hwilkins on 11/2/15.
  */
case class Row(data: Array[Any]) {
  def apply(index: Int): Any = get(index)

  def get(index: Int): Any = data(index)
  def getDouble(index: Int): Double = data(index).asInstanceOf[Double]
  def getString(index: Int): String = data(index).asInstanceOf[String]
  def getVector(index: Int): Vector = data(index).asInstanceOf[Vector]
  def getStringArray(index: Int): Array[String] = data(index).asInstanceOf[Array[String]]

  def toArray: Array[Any] = data.toArray

  def withValue(f: (Row) => Any): Row = withValue(f(this))
  def withValue(value: Any): Row = {
    Row(data :+ value)
  }

  def selectIndices(indices: Int *): Row = {
    val values = indices.toArray.map(data)
    Row(values)
  }

  def dropIndex(index: Int): Row = {
    val values = toArray.zipWithIndex.filter(_._2 != index).map(_._1)
    Row(values)
  }

  override def toString: String = s"Row(${mkString(",")})"

  def mkString: String = data.mkString
  def mkString(sep: String): String = data.mkString(sep)
  def mkString(start: String, sep: String, end: String): String = data.mkString(start, sep, end)
}