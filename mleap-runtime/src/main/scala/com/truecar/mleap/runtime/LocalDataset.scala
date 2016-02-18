package com.truecar.mleap.runtime

/**
  * Created by hwilkins on 12/3/15.
  */
case class LocalDataset(data: Array[Row]) {
  def apply(index: Int): Row = data(index)
}

object LocalDataset {
  implicit object ArrayDatasetDataset extends Dataset[LocalDataset] {
    override def update(t: LocalDataset, f: (Row) => Row): LocalDataset = {
      val data2 = t.data.map(f)
      t.copy(data = data2)
    }

    override def distinct(t: LocalDataset): LocalDataset = t.copy(data = t.data.distinct)

    override def toLocal(t: LocalDataset): LocalDataset = t
  }
}
