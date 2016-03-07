package com.truecar.mleap.serialization

import java.io.{InputStream, OutputStream}

import com.truecar.mleap.bundle.StreamSerializer

/**
  * Created by hwilkins on 3/6/16.
  */
case class MleapToMlSerializer[Mleap, Ml](serializer: StreamSerializer[Ml])
                                     (implicit convertTo: (Mleap) => Ml,
                                      convertFrom: (Ml) => Mleap) extends StreamSerializer[Mleap] {
  override val key: String = serializer.key



  override def serializeItem(obj: Mleap,
                             out: OutputStream): Unit = serializer.serializeItem(obj, out)
  override def serialize(obj: Mleap,
                         out: OutputStream): Unit = serializer.serialize(obj, out)

  override def deserializeItem(in: InputStream): Mleap = serializer.deserializeItem(in)
  override def deserialize(in: InputStream): Mleap = serializer.deserialize(in)
}
