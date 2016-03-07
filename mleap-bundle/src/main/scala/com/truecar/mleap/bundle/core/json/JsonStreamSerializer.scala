package com.truecar.mleap.bundle.core.json

import java.io._

import com.truecar.mleap.bundle.StreamSerializer
import spray.json._

/**
  * Created by hwilkins on 3/5/16.
  */
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
    val json = format.write(obj).compactPrint.getBytes
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
    new BufferedReader(new InputStreamReader(in))
      .readLine()
      .parseJson
      .convertTo[Obj]
  }
}
