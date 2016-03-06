package com.truecar.mleap.serialization.json

import java.io.{BufferedReader, BufferedWriter}

import com.truecar.mleap.serialization.Serializer
import spray.json._

/**
  * Created by hwilkins on 3/5/16.
  */
trait JsonSerializer[Obj] extends Serializer[Obj] {
  implicit val format: RootJsonFormat[Obj]

  override def serialize(obj: Obj, writer: BufferedWriter): Unit = {
    writer.write(format.write(obj).compactPrint)
    writer.write('\n')
  }

  override def deserialize(reader: BufferedReader): Obj = {
    reader.readLine().parseJson.convertTo[Obj]
  }
}
