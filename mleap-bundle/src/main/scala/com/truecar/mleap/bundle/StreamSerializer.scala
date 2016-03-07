package com.truecar.mleap.bundle

import java.io.{InputStream, OutputStream}

/**
  * Created by hwilkins on 3/4/16.
  */
trait StreamSerializer[Obj] {
  val key: String

  def serializeAnyItem(obj: Any, out: OutputStream): Unit = serializeItem(obj.asInstanceOf[Obj], out)
  def serializeAny(obj: Any, out: OutputStream): Unit = serialize(obj.asInstanceOf[Obj], out)

  def deserializeAnyItem(in: InputStream): Any = deserializeItem(in)
  def deserializeAny(in: InputStream): Any = deserialize(in)

  def serializeItem(obj: Obj, out: OutputStream)
  def serialize(obj: Obj, out: OutputStream): Unit

  def deserializeItem(in: InputStream): Obj
  def deserialize(in: InputStream): Obj
}
