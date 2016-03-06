package com.truecar.mleap.serialization

import java.io.{BufferedWriter, BufferedReader}

/**
  * Created by hwilkins on 3/5/16.
  */
class MleapSerializer {
  var serializers: Map[String, Serializer[_]] = Map()
  var bundleSerializers: Map[String, BundleSerializer[_]] = Map()

  def addSerializer[T](serializer: Serializer[T]) = {
    serializers += (serializer.key -> serializer)
  }

  def addBundleSerializer[T](serializer: BundleSerializer[T]) = {
    bundleSerializers += (serializer.key -> serializer)
  }

  def getSerializer(key: String): Option[Serializer[_]] = serializers.get(key)
  def getBundleSerializer(key: String): Option[BundleSerializer[_]] = bundleSerializers.get(key)

  def serializeToStream(obj: Any, writer: BufferedWriter): Unit = {
    val key = obj.getClass.getCanonicalName
    writer.write(key)
    writer.write('\n')
    serializers(key).serializeAny(obj, writer)
  }

  def deserializeFromStream(reader: BufferedReader): Any = {
    val key = reader.readLine()
    serializers(key).deserializeAny(reader)
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
    metaWriter.write(key)
    metaWriter.write('\n')
    metaWriter.close()
  }

  def deserializeFromBundle(bundle: Bundle): Any = {
    val metaReader = bundle.contentReader("meta")
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
