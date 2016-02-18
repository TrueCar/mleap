package com.truecar.mleap.spark.converter

import com.truecar.mleap.core.tree
import com.truecar.mleap.spark.MleapSparkSupport._
import org.apache.spark.ml.tree.{LeafNode, InternalNode, Node}

/**
  * Created by hwilkins on 11/18/15.
  */
case class NodeToMleap(node: Node) {
  def toMleap: tree.Node = {
    node match {
      case node: InternalNode =>
        tree.InternalNode(node.prediction,
          node.impurity,
          node.gain,
          node.leftChild.toMleap,
          node.rightChild.toMleap,
          node.split.toMleap)
      case node: LeafNode =>
        tree.LeafNode(node.prediction, node.impurity)
    }
  }
}