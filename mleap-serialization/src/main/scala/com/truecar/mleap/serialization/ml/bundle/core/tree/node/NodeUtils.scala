package com.truecar.mleap.serialization.ml.bundle.core.tree.node

import com.truecar.mleap.core.tree.{LeafNode, InternalNode, Node}
import ml.core.tree.InternalNodeData.InternalNodeData
import ml.core.tree.LeafNodeData.LeafNodeData
import ml.core.tree.NodeData.NodeData
import com.truecar.mleap.serialization.ml.Converters._

/**
  * Created by hwilkins on 3/6/16.
  */
object NodeUtils {
  def nodeDataForNode(node: Node): NodeData = node match {
    case node: InternalNode =>
      NodeData(internal = Some(InternalNodeData(node.prediction,
        node.gain,
        node.impurity,
        node.split)))
    case node: LeafNode =>
      NodeData(leaf = Some(LeafNodeData(node.prediction, node.impurity)))
  }
}
