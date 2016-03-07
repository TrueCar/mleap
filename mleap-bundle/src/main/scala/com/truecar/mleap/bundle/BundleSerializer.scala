package com.truecar.mleap.bundle

/**
  * Created by hwilkins on 3/4/16.
  */
trait BundleSerializer[Obj] {
  val key: String

  def serializeAny(obj: Any, bundle: BundleWriter): Unit = serialize(obj.asInstanceOf[Obj], bundle)
  def deserializeAny(bundle: BundleReader): Any = deserialize(bundle)

  def serialize(obj: Obj, bundle: BundleWriter): Unit
  def deserialize(bundle: BundleReader): Obj
}
