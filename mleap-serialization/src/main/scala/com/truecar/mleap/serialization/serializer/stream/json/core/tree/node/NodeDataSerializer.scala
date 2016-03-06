package com.truecar.mleap.serialization.serializer.stream.json.core.tree.node

import java.io.{InputStream, OutputStream}

import com.truecar.mleap.bundle.StreamSerializer
import com.truecar.mleap.core.tree.{ContinuousSplit, CategoricalSplit, Split}
import com.truecar.mleap.serialization.core.tree.node.{InternalNodeData, LeafNodeData, NodeData}
import com.truecar.mleap.serialization.serializer.stream.json.JsonStreamSerializer
import spray.json.DefaultJsonProtocol._
import spray.json._

/**
  * Created by hwilkins on 3/6/16.
  */
object InternalNodeDataSerializer extends JsonStreamSerializer[InternalNodeData] {
  implicit val categoricalSplitFormat = jsonFormat3(CategoricalSplit)
  implicit val continuousSplitFormat = jsonFormat2(ContinuousSplit)

  implicit object SplitFormat extends RootJsonFormat[Split] {
    override def write(obj: Split): JsValue = obj match {
      case split: CategoricalSplit =>
        val fields = split.toJson.asJsObject.fields + ("type" -> JsString(Split.categoricalSplitName))
        JsObject(fields)
      case split: ContinuousSplit =>
        val fields = split.toJson.asJsObject.fields + ("type" -> JsString(Split.continuousSplitName))
        JsObject(fields)
    }

    override def read(json: JsValue): Split = {
      val typeName = json.asJsObject.fields("type") match {
        case JsString(name) => name
        case _ => throw new Error("Could not parse split")
      }

      typeName match {
        case Split.categoricalSplitName => json.convertTo[CategoricalSplit]
        case Split.continuousSplitName => json.convertTo[ContinuousSplit]
      }
    }
  }

  override val klazz: Class[InternalNodeData] = classOf[InternalNodeData]
  override implicit val format: RootJsonFormat[InternalNodeData] = jsonFormat4(InternalNodeData)
}

object LeafNodeDataSerializer extends JsonStreamSerializer[LeafNodeData] {
  override val klazz: Class[LeafNodeData] = classOf[LeafNodeData]
  override implicit val format: RootJsonFormat[LeafNodeData] = jsonFormat2(LeafNodeData)
}

object NodeDataSerializer extends StreamSerializer[NodeData] {
  override val klazz: Class[NodeData] = classOf[NodeData]

  override def serialize(obj: NodeData, out: OutputStream): Unit = {
    obj match {
      case node: InternalNodeData =>
        out.write(0)
        InternalNodeDataSerializer.serialize(node, out)
      case node: LeafNodeData =>
        out.write(1)
        LeafNodeDataSerializer.serialize(node, out)
    }
  }

  override def deserialize(in: InputStream): NodeData = {
    val nodeType = in.read()

    nodeType match {
      case 0 =>
        InternalNodeDataSerializer.deserialize(in)
      case 1 =>
        LeafNodeDataSerializer.deserialize(in)
    }
  }
}
