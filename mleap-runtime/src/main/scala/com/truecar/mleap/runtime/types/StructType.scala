package com.truecar.mleap.runtime.types

import scala.util.{Failure, Success, Try}

/**
  * Created by hwilkins on 10/23/15.
  */
object StructType {
  val empty = StructType(Seq())

  def withFields(fields: StructField *): StructType = StructType(fields.toSeq)

  def apply(fields:Seq[StructField]) = StructType(fields,
                                                  fields.map(_.name).zipWithIndex.toMap,
                                                  fields.map(_.name).zip(fields).toMap )
}

case class StructType(fields: Seq[StructField],
                      private val nameToIndex: Map[String, Int],
                      private val nameToField: Map[String, StructField])
                                                                  extends Serializable {

  def apply(name: String): StructField = nameToField(name)
  def getField(name: String): Option[StructField] = nameToField.get(name)
  def indexOf(name: String): Int = nameToIndex(name)
  def getIndexOf(name: String): Option[Int] = nameToIndex.get(name)
  def contains(name: String): Boolean = nameToIndex.contains(name)

  def withField(field: StructField): StructType = StructType(fields :+ field)

  def select(fieldNames: String *): Try[StructType] = {
    tryIndicesOf(fieldNames: _*).map(selectIndices(_: _*))
  }

  def selectIndices(indices: Int *): StructType = {
    StructType(indices.map(fields))
  }

  def indicesOf(fieldNames: String *): Seq[Int] = fieldNames.map(nameToIndex)

  def tryIndicesOf(fieldNames: String *): Try[Seq[Int]] = {
    fieldNames.foldLeft(Try(Seq[Int]())) {
      (tryIndices, name) =>
        tryIndices.flatMap {
          indices =>
            if(contains(name)) {
              Success(indices :+ indexOf(name))
            } else {
              Failure(new Error(s"Field $name does not exist"))
            }
        }
    }
  }

  def dropField(name: String): Try[StructType] = {
    tryIndexOf(name).map(dropIndex)
  }

  /**
    * Returns a new {@code StructType} with the field at the desired index dropped.
    * @param index The index of the field to drop.
    * @return StructType with field dropped.
    */
  def dropIndex(index: Int): StructType = {
    val key = fields(index).name

    StructType(fields.drop(index),
               nameToIndex - key,
               nameToField - key)
  }

  def tryIndexOf(name: String): Try[Int] = {
    if(contains(name)) {
      Success(indexOf(name))
    } else {
      Failure(new Error(s"Field $name does not exist"))
    }
  }
}
