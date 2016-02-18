package com.truecar.mleap.spark

import com.truecar.mleap.runtime._
import com.truecar.mleap.runtime.types.StructType
import com.truecar.mleap.spark.SparkDataset.SparkDatasetDataset
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StructType => SparkStructType}

/**
  * Created by hwilkins on 11/12/15.
  */
case class SparkLeapFrame(schema: StructType,
                          sparkSchema: SparkStructType,
                          dataset: SparkDataset) extends Serializable

object SparkLeapFrame {
  implicit object SparkLeapFrameLeapFrame extends LeapFrame[SparkLeapFrame] {
    override type D = SparkDataset
    override implicit val dtc: Dataset[SparkDataset] = SparkDatasetDataset

    override def schema(t: SparkLeapFrame): StructType = t.schema

    override def toLocal(t: SparkLeapFrame): LocalLeapFrame = LocalLeapFrame(t.schema, Dataset.toLocal(t.dataset))

    override def dataset(t: SparkLeapFrame): SparkDataset = t.dataset

    override protected def withSchemaAndDataset(t: SparkLeapFrame,
                                                schema: StructType,
                                                dataset: SparkDataset): SparkLeapFrame = {
      t.copy(schema = schema, dataset = dataset)
    }

    override protected def withDataset(t: SparkLeapFrame, dataset: SparkDataset): SparkLeapFrame = {
      t.copy(dataset = dataset)
    }
  }
}

case class SparkDataset(rdd: RDD[(Row, IndexedSeq[Any])]) extends Serializable

object SparkDataset {
  implicit object SparkDatasetDataset extends Dataset[SparkDataset] {
    override def update(t: SparkDataset, f: (Row) => Row): SparkDataset = {
      val rdd2 = t.rdd.map {
        case (row, sparkValues) => (f(row), sparkValues)
      }
      t.copy(rdd = rdd2)
    }

    override def distinct(t: SparkDataset): SparkDataset = t.copy(rdd = t.rdd.distinct())

    override def toLocal(t: SparkDataset): LocalDataset = LocalDataset(t.rdd.map(_._1).collect)
  }
}
