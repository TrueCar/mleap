package com.truecar.mleap.serialization.serializer.stream.json.core.tree.node

import com.truecar.mleap.serialization.core.tree.node.{NodeFormat, NodeMetaData}
import com.truecar.mleap.serialization.serializer.stream.json.JsonStreamSerializer
import spray.json.{JsString, JsonFormat, JsValue, RootJsonFormat}
import spray.json.DefaultJsonProtocol._

/**
  * Created by hwilkins on 3/6/16.
  */
object NodeMetaDataSerializer extends JsonStreamSerializer[NodeMetaData] {
  override val klazz: Class[NodeMetaData] = classOf[NodeMetaData]
  implicit object NodeFormatFormat extends JsonFormat[NodeFormat] {
    override def write(obj: NodeFormat): JsValue = obj match {
      case NodeFormat.Linear => JsString("linear")
    }

    override def read(json: JsValue): NodeFormat = json match {
      case JsString("linear") => NodeFormat.Linear
      case _ => throw new Error("Could not find valid node format, found:\n" + json.prettyPrint)
    }
  }
  override implicit val format: RootJsonFormat[NodeMetaData] = jsonFormat1(NodeMetaData)
}
