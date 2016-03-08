package com.truecar.mleap.bundle

import java.io._

import scala.reflect.ClassTag

/**
  * Created by hwilkins on 3/5/16.
  */
class MleapSerializer {
  var serializers: Map[String, StreamSerializer[_]] = Map()
  var bundleSerializers: Map[String, BundleSerializer[_]] = Map()
  var mlNameLookup: Map[String, String] = Map()
  var canonicalNameLookup: Map[String, String] = Map()

  def addSerializer[T: ClassTag](serializer: StreamSerializer[T]) = {
    serializers += (serializer.key -> serializer)
    val name = implicitly[ClassTag[T]].runtimeClass.getCanonicalName
    mlNameLookup += (name -> serializer.key)
    canonicalNameLookup += (serializer.key -> name)
  }

  def addBundleSerializer[T: ClassTag](serializer: BundleSerializer[T]) = {
    bundleSerializers += (serializer.key -> serializer)
    val name = implicitly[ClassTag[T]].runtimeClass.getCanonicalName
    mlNameLookup += (name -> serializer.key)
    canonicalNameLookup += (serializer.key -> name)
  }

  def getSerializer(key: String): Option[StreamSerializer[_]] = serializers.get(key)
  def getBundleSerializer(key: String): Option[BundleSerializer[_]] = bundleSerializers.get(key)

  def getMlName(key: String): String = mlNameLookup(key)
  def getCanonicalName(key: String): String = canonicalNameLookup(key)

  def serializeWithClass(obj: Any, out: OutputStream): Unit = {
    val key = mlNameLookup(obj.getClass.getCanonicalName)
    val bytes = key.getBytes
    val dataOut = new DataOutputStream(out)
    dataOut.writeInt(key.length)
    dataOut.write(bytes)
    serializers(key).serializeAny(obj, out)
  }

  def serialize(obj: Any, out: OutputStream): Unit = {
    val key = mlNameLookup(obj.getClass.getCanonicalName)
    serializers(key).serializeAny(obj, out)
  }

  def deserializeWithClass(in: InputStream): Any = {
    val dataIn = new DataInputStream(in)
    val size = dataIn.readInt()
    val bytes = new Array[Byte](size)
    val key = new String(bytes)
    serializers(key).deserializeAny(in)
  }

  def deserialize[T: ClassTag](in: InputStream): T = {
    val key = mlNameLookup(implicitly[ClassTag[T]].runtimeClass.getCanonicalName)
    serializers(key).deserializeAny(in).asInstanceOf[T]
  }

  def serializeWithClass(obj: Any, bundle: BundleWriter): Unit = {
    val key = mlNameLookup(obj.getClass.getCanonicalName)

    val metaWriter = bundle.contentWriter("meta.mleap")
    metaWriter.write(key.getBytes)
    metaWriter.write('\n')
    bundle.close(metaWriter)

    serializers.get(key) match {
      case Some(serializer) =>
        val contentWriter = bundle.contentWriter("content")
        serializer.serializeAny(obj, contentWriter)
        bundle.close(contentWriter)
      case None =>
        bundleSerializers.get(key) match {
          case Some(serializer) =>
            serializer.serializeAny(obj, bundle)
          case None =>
            throw new Error("Could not serialize to bundle: " + key)
        }
    }
  }

  def deserializeWithClass(bundle: BundleReader): Any = {
    val metaReader = new BufferedReader(new InputStreamReader(bundle.contentReader("meta.mleap")))
    val key = metaReader.readLine()
    bundle.close(metaReader)

    serializers.get(key) match {
      case Some(serializer) =>
        val contentReader = bundle.contentReader("content")
        val obj = serializer.deserializeAny(contentReader)
        bundle.close(contentReader)
        obj
      case None =>
        bundleSerializers.get(key) match {
          case Some(serializer) =>
            serializer.deserializeAny(bundle)
          case None =>
            throw new Error("Could not deserialize: " + key)
        }
    }
  }
}
