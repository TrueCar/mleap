package com.truecar.mleap.bundle

import java.io.{InputStream, OutputStream}

/**
  * Created by hwilkins on 3/4/16.
  */
trait StreamSerializer[Obj] {
  val klazz: Class[Obj]
  lazy val key: String = klazz.getCanonicalName

  def serializeAny(obj: Any, out: OutputStream): Unit = serialize(obj.asInstanceOf[Obj], out)
  def deserializeAny(in: InputStream): Any = deserialize(in)

  def serialize(obj: Obj, out: OutputStream): Unit
  def deserialize(in: InputStream): Obj
}
