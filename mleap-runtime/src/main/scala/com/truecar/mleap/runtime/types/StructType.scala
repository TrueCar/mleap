package com.truecar.mleap.runtime.types

import scala.util.{Failure, Success, Try}

/**
  * Structured container (schema) for fields.
  * Operations include select, withField, and dropField.
  */
object StructType {
  val empty = StructType(Seq())

  def withFields(fields: StructField *): StructType = StructType(fields.toSeq)

  def apply(fields: Seq[StructField]): StructType = StructType(fields,
    fields.map(_.name).zipWithIndex.toMap,
    fields.map(_.name).zip(fields).toMap )
}

// TODO: add access modifiers to tryIndicesOf, selectIndices, dropIndex.
/**
  *
  * @param fields
  * @param nameToIndex
  * @param nameToField
  */
case class StructType private (fields: Seq[StructField],
                               private val nameToIndex: Map[String, Int],
                               private val nameToField: Map[String, StructField])
  extends Serializable {

  def apply(name: String): StructField = nameToField(name)
  def getField(name: String): Option[StructField] = nameToField.get(name)
  def indexOf(name: String): Int = nameToIndex(name)
  def getIndexOf(name: String): Option[Int] = nameToIndex.get(name)
  def contains(name: String): Boolean = nameToIndex.contains(name)

  /**
    * Returns a new {@code StructType} with {@code field} added.
    * @param field The field to add.
    * @return StructType with field added.
    */
  def withField(field: StructField): StructType = {
    val key = field.name

    StructType(fields :+ field,
      nameToIndex + (key -> fields.length),
      nameToField + (key -> field))
  }

  def select(fieldNames: String *): Try[StructType] = {
    tryIndicesOf(fieldNames: _*).map(selectIndices(_: _*))
  }

  /**
    * Returns a new {@code StructType} containing the fields at the desired indices.
    * @param indices The indices of the fields to select.
    * @return StructType with the selected fields.
    */
  def selectIndices(indices: Int *): StructType = {
    val selection = indices.map(fields)

    StructType(selection)
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
    StructType(fields.take(index) ++ fields.drop(index + 1))
  }

  def tryIndexOf(name: String): Try[Int] = {
    if(contains(name)) {
      Success(indexOf(name))
    } else {
      Failure(new Error(s"Field $name does not exist"))
    }
  }
}
