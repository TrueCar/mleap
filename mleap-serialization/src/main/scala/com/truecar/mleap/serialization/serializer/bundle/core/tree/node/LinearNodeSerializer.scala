package com.truecar.mleap.serialization.serializer.bundle.core.tree.node

import java.io.{InputStream, OutputStream}

import com.truecar.mleap.bundle.{StreamSerializer, Bundle, BundleSerializer}
import com.truecar.mleap.core.tree.{LeafNode, InternalNode, Node}
import ml.core.tree.NodeData.NodeData
import ml.core.tree.NodeMetaData.NodeMetaData
import ml.core.tree.NodeMetaData.NodeMetaData.NodeFormat
import com.truecar.mleap.serialization.Converters._

/**
  * Created by hwilkins on 3/6/16.
  */
case class LinearNodeSerializer(nodeMetaDataSerializer: StreamSerializer[NodeMetaData],
                                nodeDataSerializer: StreamSerializer[NodeData]) extends BundleSerializer[Node] {
  override val key: String = "ml.core.tree.Node"

  override def serialize(obj: Node, bundle: Bundle): Unit = {
    val meta = bundle.contentWriter("meta")
    nodeMetaDataSerializer.serialize(NodeMetaData(NodeFormat.LINEAR), meta)
    meta.close()

    val nodes = bundle.contentWriter("nodes")
    serialize(obj, nodes)
    nodes.close()
  }

  override def deserialize(bundle: Bundle): Node = {
    val meta = bundle.contentReader("meta")
    val metaData = nodeMetaDataSerializer.deserialize(meta)
    meta.close()

    metaData match {
      case NodeMetaData(NodeFormat.LINEAR) =>
        val nodes = bundle.contentReader("nodes")
        val node = deserialize(nodes)
        nodes.close()
        node
      case _ => throw new Error("Can only deserialize nodes stored with NodeFormat.Linear")
    }
  }

  private def serialize(node: Node, out: OutputStream): Unit = {
    node match {
      case node: InternalNode =>
        nodeDataSerializer.serialize(NodeUtils.nodeDataForNode(node), out)

        serialize(node.leftChild, out)
        serialize(node.rightChild, out)
      case node: LeafNode =>
        nodeDataSerializer.serialize(NodeUtils.nodeDataForNode(node), out)
    }
  }

  private def deserialize(in: InputStream): Node = {
    val nodeData = nodeDataSerializer.deserialize(in)

    nodeData.internal match {
      case Some(data) =>
        InternalNode(data.prediction,
          data.impurity,
          data.gain,
          deserialize(in),
          deserialize(in),
          data.split)
      case None =>
        nodeData.leaf match {
          case Some(data) =>
            LeafNode(data.prediction, data.impurity)
          case None =>
            throw new Error("Could not deserialize node")
        }
    }
  }
}
