package com.truecar.mleap.runtime

import com.truecar.mleap.core.linalg.Vector

/**
  * Created by hwilkins on 11/2/15.
  */
object Row {
  def apply(values: Any *): Row = SeqRow(values)
}

trait Row {
  def apply(index: Int): Any = get(index)

  def get(index: Int): Any
  def getDouble(index: Int): Double = get(index).asInstanceOf[Double]
  def getString(index: Int): String = get(index).asInstanceOf[String]
  def getVector(index: Int): Vector = get(index).asInstanceOf[Vector]
  def getStringArray(index: Int): Array[String] = get(index).asInstanceOf[Array[String]]

  def toArray: Array[Any] // = data.toArray
  def toSeq: Seq[Any]

  def withValue(f: (Row) => Any): Row = withValue(f(this))
  def withValue(value: Any): Row

  def selectIndices(indices: Int *): Row

  def dropIndex(index: Int): Row

  override def toString: String  = s"Row(${mkString(",")})"

  def mkString: String = toArray.mkString
  def mkString(sep: String): String = toArray.mkString(sep)
  def mkString(start: String, sep: String, end: String): String = toArray.mkString(start, sep, end)
}

object SeqRow {
  def apply(values: Seq[Any]): SeqRow = { new SeqRow(values.reverse) }
}

class SeqRow private(values: Seq[Any]) extends Row {
  override def toArray: Array[Any] = values.toArray
  override def toSeq: Seq[Any] = values

  override def get(index: Int): Any = values(index)

  override def selectIndices(indices: Int *): SeqRow = SeqRow(indices.map(values))

  override def withValue(value: Any): Row = new SeqRow(value +: values)

  override def dropIndex(index: Int): Row = new SeqRow(values.indices.filter(_ != index).map(values))
}