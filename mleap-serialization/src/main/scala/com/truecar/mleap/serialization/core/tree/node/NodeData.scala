package com.truecar.mleap.serialization.core.tree.node

import com.truecar.mleap.core.tree.{InternalNode, LeafNode, Node, Split}

/**
  * Created by hwilkins on 3/6/16.
  */
object NodeData {
  def fromNode(node: Node): NodeData = node match {
    case node: InternalNode => fromNode(node)
    case node: LeafNode => fromNode(node)
  }

  def fromNode(node: InternalNode): InternalNodeData = {
    InternalNodeData(node.prediction,
      node.gain,
      node.impurity,
      node.split)
  }

  def fromNode(node: LeafNode): LeafNodeData = {
    LeafNodeData(node.prediction, node.impurity)
  }
}

sealed trait NodeData

case class InternalNodeData(prediction: Double,
                            impurity: Double,
                            gain: Double,
                            split: Split) extends NodeData
case class LeafNodeData(prediction: Double,
                        impurity: Double) extends NodeData