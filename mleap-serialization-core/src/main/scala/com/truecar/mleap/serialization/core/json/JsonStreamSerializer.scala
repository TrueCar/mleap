package com.truecar.mleap.serialization.core.json

import java.io._

import com.truecar.mleap.bundle.StreamSerializer
import spray.json._

/**
  * Created by hwilkins on 3/5/16.
  */
trait JsonStreamSerializer[Obj] extends StreamSerializer[Obj] {
  implicit val format: RootJsonFormat[Obj]

  override def serialize(obj: Obj, out: OutputStream): Unit = {
    val dataOut = new DataOutputStream(out)
    val json = format.write(obj).compactPrint.getBytes
    dataOut.writeInt(json.size)
    dataOut.write(json)
    dataOut.write('\n')
  }

  override def deserialize(in: InputStream): Obj = {
    val dataIn = new DataInputStream(in)
    val size = dataIn.readInt()
    val bytes = new Array[Byte](size)
    dataIn.readFully(bytes)
    dataIn.read()
    val json = new String(bytes)

    json.parseJson.convertTo[Obj]
  }
}
