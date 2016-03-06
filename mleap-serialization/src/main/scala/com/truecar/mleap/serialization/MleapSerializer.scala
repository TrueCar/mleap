package com.truecar.mleap.serialization

import java.io._

import com.truecar.mleap.bundle.{BundleSerializer, Bundle, StreamSerializer}

/**
  * Created by hwilkins on 3/5/16.
  */
class MleapSerializer {
  var serializers: Map[String, StreamSerializer[_]] = Map()
  var bundleSerializers: Map[String, BundleSerializer[_]] = Map()

  def addSerializer[T](serializer: StreamSerializer[T]) = {
    serializers += (serializer.key -> serializer)
  }

  def addBundleSerializer[T](serializer: BundleSerializer[T]) = {
    bundleSerializers += (serializer.key -> serializer)
  }

  def getSerializer(key: String): Option[StreamSerializer[_]] = serializers.get(key)
  def getBundleSerializer(key: String): Option[BundleSerializer[_]] = bundleSerializers.get(key)

  def serializeToStream(obj: Any, out: OutputStream): Unit = {
    val key = obj.getClass.getCanonicalName
    val bytes = key.getBytes
    val dataOut = new DataOutputStream(out)
    dataOut.writeInt(key.length)
    dataOut.write(bytes)
    serializers(key).serializeAny(obj, out)
  }

  def deserializeFromStream(in: InputStream): Any = {
    val dataIn = new DataInputStream(in)
    val size = dataIn.readInt()
    val bytes = new Array[Byte](size)
    val key = new String(bytes)
    serializers(key).deserializeAny(in)
  }

  def serializeToBundle(obj: Any, bundle: Bundle): Unit = {
    val key = obj.getClass.getCanonicalName
    serializers.get(key) match {
      case Some(serializer) =>
        val contentWriter = bundle.contentWriter("content")
        serializer.serializeAny(obj, contentWriter)
        contentWriter.close()
      case None =>
        bundleSerializers.get(key) match {
          case Some(serializer) =>
            serializer.serializeAny(obj, bundle.createBundle("content"))
          case None =>
            throw new Error("Could not serialize to bundle: " + key)
        }
    }

    val metaWriter = bundle.contentWriter("meta")
    metaWriter.write(key.getBytes)
    metaWriter.write('\n')
    metaWriter.close()
  }

  def deserializeFromBundle(bundle: Bundle): Any = {
    val metaReader = new BufferedReader(new InputStreamReader(bundle.contentReader("meta")))
    val key = metaReader.readLine()
    metaReader.close()

    serializers.get(key) match {
      case Some(serializer) =>
        val contentReader = bundle.contentReader("content")
        val obj = serializer.deserializeAny(contentReader)
        contentReader.close()
        obj
      case None =>
        bundleSerializers.get(key) match {
          case Some(serializer) =>
            serializer.deserializeAny(bundle.getBundle("content"))
          case None =>
            throw new Error("Could not deserialize: " + key)
        }
    }
  }
}
