package com.truecar.mleap.runtime

/**
  * Created by hwilkins on 11/2/15.
  */
trait Dataset[T] extends Serializable {
  def update(t: T, f: (Row) => Row): T

  def withValue(t: T, f: (Row) => Any): T = update(t, _.withValue(f))
  def selectIndices(t: T, indices: Int *): T = update(t, _.selectIndices(indices: _*))
  def dropIndex(t: T, index: Int): T = update(t, _.dropIndex(index))

  def distinct(t: T): T

  def toLocal(t: T): LocalDataset
  def toArray(t: T): Array[Row] = toLocal(t).data
}

object Dataset {
  implicit class Ops[D: Dataset](dataset: D) {
    def update(f: (Row) => Row): D = Dataset.update(dataset, f)
    def distinct: D = Dataset.distinct(dataset)
    def toLocal: LocalDataset = Dataset.toLocal(dataset)
    def toArray: Array[Row] = Dataset.toArray(dataset)
  }

  def update[T: Dataset](t: T, f: (Row) => Row): T = implicitly[Dataset[T]].update(t, f)
  def distinct[T: Dataset](t: T): T = implicitly[Dataset[T]].distinct(t)
  def toLocal[T: Dataset](t: T): LocalDataset = implicitly[Dataset[T]].toLocal(t)
  def toArray[T: Dataset](t: T): Array[Row] = implicitly[Dataset[T]].toArray(t)
}