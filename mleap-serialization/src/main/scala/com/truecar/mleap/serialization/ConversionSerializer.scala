package com.truecar.mleap.serialization

import java.io.{InputStream, OutputStream}

import com.truecar.mleap.bundle.StreamSerializer

/**
  * Created by hwilkins on 3/6/16.
  */
case class ConversionSerializer[A, B](serializer: StreamSerializer[B])
                                          (implicit convertTo: (A) => B,
                                      convertFrom: (B) => A) extends StreamSerializer[A] {
  override val key: String = serializer.key

  override def serializeItem(obj: A,
                             out: OutputStream): Unit = serializer.serializeItem(obj, out)
  override def serialize(obj: A,
                         out: OutputStream): Unit = serializer.serialize(obj, out)

  override def deserializeItem(in: InputStream): A = serializer.deserializeItem(in)
  override def deserialize(in: InputStream): A = serializer.deserialize(in)
}
