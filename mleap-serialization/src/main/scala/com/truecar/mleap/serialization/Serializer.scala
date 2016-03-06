package com.truecar.mleap.serialization

import java.io.{BufferedReader, BufferedWriter}

/**
  * Created by hwilkins on 3/4/16.
  */
trait Serializer[Obj] {
  val klazz: Class[Obj]
  lazy val key: String = klazz.getCanonicalName

  def serializeAny(obj: Any, writer: BufferedWriter): Unit = serialize(obj.asInstanceOf[Obj], writer)
  def deserializeAny(reader: BufferedReader): Any = deserialize(reader)

  def serialize(obj: Obj, writer: BufferedWriter): Unit
  def deserialize(reader: BufferedReader): Obj
}
