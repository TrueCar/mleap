package org.apache.spark.ml.mleap.converter

import com.truecar.mleap.core.linalg.{DenseVector, Vector}
import com.truecar.mleap.core.tree
import com.truecar.mleap.spark.MleapSparkSupport._
import org.apache.spark.ml.tree.{InternalNode, LeafNode, Node}

/**
  * Created by hwilkins on 11/18/15.
  */
case class NodeToMleap(node: Node) {
  def toMleap(includeImpurityStats: Boolean): tree.Node = {
    node match {
      case node: InternalNode =>
        tree.InternalNode(node.prediction,
          node.impurity,
          node.gain,
          node.leftChild.toMleap(includeImpurityStats),
          node.rightChild.toMleap(includeImpurityStats),
          node.split.toMleap)
      case node: LeafNode =>
        val impurityStats = if(includeImpurityStats) {
          Some(DenseVector(node.impurityStats.stats))
        } else { None }
        tree.LeafNode(node.prediction, node.impurity, impurityStats)
    }
  }
}