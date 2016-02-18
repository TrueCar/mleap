package com.truecar.mleap.core.tree

import com.truecar.mleap.core.linalg.Vector
import com.truecar.mleap.core.serialization.TypeName

/**
  * Created by hwilkins on 11/8/15.
  */
object Node {
  val leafNodeName = "LeafNode"
  val internalNodeName = "InternalNode"
}

sealed trait Node extends TypeName with Serializable {
  def prediction: Double
  def impurity: Double

  def predictImpl(features: Vector): LeafNode
}

final case class LeafNode(prediction: Double,
                          impurity: Double) extends Node {
  override def typeName: String = Node.leafNodeName

  override def predictImpl(features: Vector): LeafNode = this
}

final case class InternalNode(prediction: Double,
                              impurity: Double,
                              gain: Double,
                              leftChild: Node,
                              rightChild: Node,
                              split: Split) extends Node {
  override def typeName: String = Node.internalNodeName

  override def predictImpl(features: Vector): LeafNode = {
    if(split.shouldGoLeft(features)) {
      leftChild.predictImpl(features)
    } else {
      rightChild.predictImpl(features)
    }
  }
}
