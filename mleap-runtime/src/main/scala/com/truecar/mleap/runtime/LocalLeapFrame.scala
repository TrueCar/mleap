package com.truecar.mleap.runtime

import com.truecar.mleap.runtime.LocalDataset.ArrayDatasetDataset
import com.truecar.mleap.runtime.types.{StructField, StructType}

import scala.util.{Success, Try}

/**
  * Created by hwilkins on 12/3/15.
  */
case class LocalLeapFrame(schema: StructType, dataset: LocalDataset) {
  def getRow(index: Int): Row = dataset(index)
}

object LocalLeapFrame {
  implicit object LocalLeapFrameLeapFrame extends LeapFrame[LocalLeapFrame] {
    override type D = LocalDataset
    override implicit val dtc: Dataset[LocalDataset] = ArrayDatasetDataset

    override def toLocal(t: LocalLeapFrame): LocalLeapFrame = t

    override def schema(t: LocalLeapFrame): StructType = t.schema

    override def dataset(t: LocalLeapFrame): LocalDataset = t.dataset

    override protected def withSchemaAndDataset(t: LocalLeapFrame,
                                                schema: StructType,
                                                dataset: LocalDataset): LocalLeapFrame = {
      t.copy(schema = schema, dataset = dataset)
    }

    override protected def withDataset(t: LocalLeapFrame, dataset: LocalDataset): LocalLeapFrame = {
      t.copy(dataset = dataset)
    }
  }
}
