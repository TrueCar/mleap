package com.truecar.mleap.bundle.core.json

import java.io._

import com.truecar.mleap.bundle.StreamSerializer
import spray.json._

import scala.reflect.ClassTag

/**
  * Created by hwilkins on 3/5/16.
  */
object JsonStreamSerializer {
  import scala.language.implicitConversions

  implicit class ImplicitJsonStreamSerializer[T: ClassTag](jsonFormat: RootJsonFormat[T]) extends JsonStreamSerializer[T] {
    override val key: String = implicitly[ClassTag[T]].runtimeClass.getCanonicalName
    override implicit val format: RootJsonFormat[T] = jsonFormat
  }
}

trait JsonStreamSerializer[Obj] extends StreamSerializer[Obj] {
  implicit val format: RootJsonFormat[Obj]

  override def serializeItem(obj: Obj, out: OutputStream): Unit = {
    val json = format.write(obj).compactPrint.getBytes
    val dataOut = new DataOutputStream(out)
    dataOut.writeInt(json.length)
    dataOut.write(json)
    dataOut.write('\n')
  }

  override def serialize(obj: Obj, out: OutputStream): Unit = {
    val json = format.write(obj).prettyPrint.getBytes
    out.write(json)
  }

  override def deserializeItem(in: InputStream): Obj = {
    val dataIn = new DataInputStream(in)
    val size = dataIn.readInt()
    val bytes = new Array[Byte](size)
    dataIn.readFully(bytes)
    dataIn.read()
    new String(bytes).parseJson.convertTo[Obj]
  }

  override def deserialize(in: InputStream): Obj = {
    val reader = new BufferedReader(new InputStreamReader(in))
    val sb = new StringBuilder()

    var hasLine = true
    while(hasLine) {
      val line = reader.readLine()

      if(line != null) {
        sb.append(line)
      } else {
        hasLine = false
      }
    }

    sb.toString.parseJson.convertTo[Obj]
  }
}
