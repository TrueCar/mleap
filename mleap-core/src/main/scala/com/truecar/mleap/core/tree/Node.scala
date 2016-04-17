package com.truecar.mleap.core.tree

import com.truecar.mleap.core.linalg.Vector

/**
  * Created by hwilkins on 11/8/15.
  */
object Node {
  val leafNodeName = "LeafNode"
  val internalNodeName = "InternalNode"
}

sealed trait Node extends Serializable {
  def prediction: Double
  def impurity: Double

  def predictImpl(features: Vector): LeafNode
}

final case class LeafNode(prediction: Double,
                          impurity: Double,
                          impurityStats: Option[Vector] = None) extends Node {
  override def predictImpl(features: Vector): LeafNode = this
}

final case class InternalNode(prediction: Double,
                              impurity: Double,
                              gain: Double,
                              leftChild: Node,
                              rightChild: Node,
                              split: Split) extends Node {
  override def predictImpl(features: Vector): LeafNode = {
    if(split.shouldGoLeft(features)) {
      leftChild.predictImpl(features)
    } else {
      rightChild.predictImpl(features)
    }
  }
}
