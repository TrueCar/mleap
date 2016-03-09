package com.truecar.mleap.core.tree

import org.scalatest.FunSpec
import com.truecar.mleap.core.linalg

/**
  * Created by hwilkins on 1/21/16.
  */
class InternalNodeSpec extends FunSpec {
  describe("#typeName") {
    it("is InternalNode") {  }
  }

  describe("#predictImpl") {
    val leftNode = LeafNode(.45, 5.6)
    val rightNode = LeafNode(.33, 3.5)
    val features = linalg.Vector.dense(Array(0.3))

    describe("when split goes left") {
      it("returns the left node") {
        val node = InternalNode(.37, 5.5, .88, leftNode, rightNode, ContinuousSplit(0, 0.4))
        assert(node.predictImpl(features) == leftNode)
      }
    }

    describe("when split goes right") {
      it("returns the right node") {
        val node = InternalNode(.37, 5.5, .88, leftNode, rightNode, ContinuousSplit(0, 0.2))
        assert(node.predictImpl(features) == rightNode)
      }
    }
  }
}

class LeafNodeSpec extends FunSpec {
  describe("#predictImpl") {
    it("returns itself") {
      val node = LeafNode(.45, 5.6)
      assert(node.predictImpl(linalg.Vector.dense(Array(.67))) == node)
    }
  }
}
