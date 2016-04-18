package com.truecar.mleap.runtime.transformer.builder

import com.truecar.mleap.runtime.types.DataType
import com.truecar.mleap.runtime.Row

import scala.util.{Failure, Try}

/**
  * Created by hwilkins on 11/15/15.
  */
trait TransformBuilder[T] extends Serializable {
  def withInput(t: T, name: String): Try[(T, Int)]
  def withInput(t: T, name: String, dataType: DataType): Try[(T, Int)]

  def withOutput(t: T, name: String, dataType: DataType)
                (o: (Row) => Any): Try[T]

  def withSelect(t: T, fieldNames: Seq[String]): Try[T]
  def withDrop(t: T, name: String): Try[T]
}

object TransformBuilder {
  implicit class Ops[T: TransformBuilder](t: T) {
    def withInput(name: String): Try[(T, Int)] = {
      implicitly[TransformBuilder[T]].withInput(t, name)
    }

    def withInput(name: String, dataType: DataType): Try[(T, Int)] = {
      implicitly[TransformBuilder[T]].withInput(t, name, dataType)
    }

    def withOutput(name: String, dataType: DataType)
                                       (o: (Row) => Any): Try[T] = {
      implicitly[TransformBuilder[T]].withOutput(t, name, dataType)(o)
    }

    def withSelect(fieldNames: Seq[String]): Try[T] = {
      implicitly[TransformBuilder[T]].withSelect(t, fieldNames)
    }
    def withDrop(name: String): Try[T] = {
      implicitly[TransformBuilder[T]].withDrop(t, name)
    }
  }
}
