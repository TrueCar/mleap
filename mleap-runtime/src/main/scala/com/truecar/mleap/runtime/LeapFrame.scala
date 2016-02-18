package com.truecar.mleap.runtime

import com.truecar.mleap.core.linalg.Vector
import com.truecar.mleap.runtime.types.{StructField, StructType}
import com.truecar.mleap.runtime.util.LeapFrameUtil

import scala.util.{Success, Failure, Try}


/**
  * Created by hwilkins on 11/2/15.
  */
trait LeapFrame[T] extends Serializable {
  type D
  implicit val dtc: Dataset[D]

  def schema(t: T): StructType
  def dataset(t: T): D

  def select(t: T, fieldNames: String *): Try[T] = {
    schema(t).tryIndicesOf(fieldNames: _*).map {
      indices =>
        val schema2 = schema(t).selectIndices(indices: _*)
        val dataset2 = dtc.selectIndices(dataset(t), indices: _*)
        withSchemaAndDataset(t, schema2, dataset2)
    }
  }

  def withField(t: T, field: StructField, f: (Row) => Any): Try[T] = {
    if(!schema(t).contains(field.name)) {
      val schema2 = schema(t).withField(field)
      val dataset2 = dtc.withValue(dataset(t), f)
      Success(withSchemaAndDataset(t, schema2, dataset2))
    } else {
      Failure(new Error(s"Field ${field.name} already exists"))
    }
  }

  def dropField(t: T, name: String): Try[T] = {
    schema(t).tryIndexOf(name).map {
      index =>
        val schema2 = schema(t).dropIndex(index)
        val dataset2 = dtc.dropIndex(dataset(t), index)
        withSchemaAndDataset(t, schema2, dataset2)
    }
  }

  def distinct(t: T): T = withDataset(t, Dataset.distinct(dataset(t)))

  protected def withDataset(t: T, dataset: D): T
  protected def withSchemaAndDataset(t: T, schema: StructType, dataset: D): T

  def toLocal(t: T): LocalLeapFrame

  def get(t: T, row: Row, field: String): Any = row.get(schema(t).indexOf(field))
  def getDouble(t: T, row: Row, field: String): Double = row.getDouble(schema(t).indexOf(field))
  def getString(t: T, row: Row, field: String): String = row.getString(schema(t).indexOf(field))
  def getVector(t: T, row: Row, field: String): Vector = row.getVector(schema(t).indexOf(field))
  def getStringArray(t: T, row: Row, field: String): Array[String] = row.getStringArray(schema(t).indexOf(field))
}

object LeapFrame extends LeapFrameUtil {
  implicit class Ops[F: LeapFrame](frame: F) {
    def schema(): StructType = LeapFrame.schema(frame)
    def select(fieldNames: String *): Try[F] = LeapFrame.select(frame, fieldNames: _*)
    def distinct: F = LeapFrame.distinct(frame)
    def withField(field: StructField, f: (Row) => Any): Try[F] = LeapFrame.withField(frame, field, f)
    def toLocal: LocalLeapFrame = LeapFrame.toLocal(frame)

    def get(row: Row, field: String): Any = LeapFrame.get(frame, row, field)
    def getDouble(row: Row, field: String): Double = LeapFrame.getDouble(frame, row, field)
    def getString(row: Row, field: String): String = LeapFrame.getString(frame, row, field)
    def getVector(row: Row, field: String): Vector = LeapFrame.getVector(frame, row, field)
    def getStringArray(row: Row, field: String): Array[String] = LeapFrame.getStringArray(frame, row, field)
  }

  def schema[T: LeapFrame](t: T): StructType = implicitly[LeapFrame[T]].schema(t)

  def select[T: LeapFrame](t: T, fieldNames: String *): Try[T] = implicitly[LeapFrame[T]].select(t, fieldNames: _*)
  def withField[T: LeapFrame](t: T,
                              field: StructField,
                              f: (Row) => Any): Try[T] = implicitly[LeapFrame[T]].withField(t, field, f)
  def dropField[T: LeapFrame](t: T, name: String): Try[T] = implicitly[LeapFrame[T]].dropField(t, name)

  def distinct[T: LeapFrame](t: T): T = implicitly[LeapFrame[T]].distinct(t)

  def toLocal[T: LeapFrame](t: T): LocalLeapFrame = implicitly[LeapFrame[T]].toLocal(t)

  def get[T: LeapFrame](t: T, row: Row, field: String): Any = implicitly[LeapFrame[T]].get(t, row, field)
  def getDouble[T: LeapFrame](t: T, row: Row, field: String): Double = implicitly[LeapFrame[T]].getDouble(t, row, field)
  def getString[T: LeapFrame](t: T, row: Row, field: String): String = implicitly[LeapFrame[T]].getString(t, row, field)
  def getVector[T: LeapFrame](t: T, row: Row, field: String): Vector = implicitly[LeapFrame[T]].getVector(t, row, field)
  def getStringArray[T: LeapFrame](t: T, row: Row, field: String): Array[String] = implicitly[LeapFrame[T]].getStringArray(t, row, field)
}
