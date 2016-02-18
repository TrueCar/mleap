package com.truecar.mleap.core.linalg

import org.scalatest.FunSpec

/**
  * Created by hwilkins on 1/21/16.
  */
class DenseVectorSpec extends FunSpec {
  describe("#toBreeze") {}

  describe("#size") {
    it("returns the size of underlying array") {
      assert(DenseVector(Array(0, 4.4, 5.5)).size == 3)
    }
  }

  describe("#toArray") {
    it("returns the underlying array") {
      val data = Array(4.5, 6.6, 32.3, 88.9)
      assert(DenseVector(data).toArray.sameElements(data))
    }
  }

  describe("#toSparse") {
    it("returns a sparse vector") {
      val data = new Array[Double](100)
      data(2) = 1.3
      data(3) = 4.6
      data(6) = 7.9
      val dense = DenseVector(data)
      val sparse = dense.toSparse
      val expectedSparse = SparseVector(100, Array(2, 3, 6), Array(1.3, 4.6, 7.9))

      assert(sparse.size == expectedSparse.size)
      assert(sparse.indices.sameElements(expectedSparse.indices))
      assert(sparse.values.sameElements(expectedSparse.values))
    }
  }

  describe("#numActives") {
    it("returns the size of the vector") {
      val vector = DenseVector(Array(1.0, 4.5, 6.7))

      assert(vector.numActives == vector.size)
    }
  }

  describe("#numNonzeros") {
    it("returns the number of non-zero values") {
      assert(DenseVector(Array(0.0, 9.7, 0.0, 7.6, 7.8, 7.9)).numNonzeros == 4)
    }
  }

  describe("#foreachActive") {
    it("iterates through every value") {
      val dense = DenseVector(Array(3.4, 5.6, 7.8))
      val indices = new Array[Int](3)
      val values = new Array[Double](3)
      val expectedIndices = Array(0, 1, 2)
      val expectedValues = Array(3.4, 5.6, 7.8)

      var i = 0
      dense.foreachActive {
        (index, value) =>
          indices(i) = index
          values(i) = value
          i = i + 1
      }

      assert(indices.sameElements(expectedIndices))
      assert(values.sameElements(expectedValues))
    }
  }
}

class SparseVectorSpec extends FunSpec {
  describe("#toBreeze") {}

  describe("#size") {
    it("returns the size of the vector") {
      assert(SparseVector(200, Array[Int](), Array[Double]()).size == 200)
    }
  }

  describe("#toArray") {
    it("returns an array of the data") {
      val expectedArray = new Array[Double](50)
      expectedArray(1) = 3.4
      expectedArray(5) = 5.6
      assert(SparseVector(50, Array(1, 5), Array(3.4, 5.6)).toArray.sameElements(expectedArray))
    }
  }

  describe("#toSparse") {
    it("returns a sparse vector") {
      val sparse = SparseVector(200, Array(1, 5, 6), Array(3.4, 5.6, 8.7))
      val sparse2 = sparse.toSparse

      assert(sparse.size == sparse2.size)
      assert(sparse.indices.sameElements(sparse2.indices))
      assert(sparse.values.sameElements(sparse2.values))
    }
  }

  describe("#numActives") {
    it("returns the size of the vector that is active") {
      val vector = SparseVector(200, Array(2, 4, 7), Array(.45, .77, .22))

      assert(vector.numActives == 3)
    }
  }

  describe("#numNonzeros") {
    it("returns the number of non-zero values") {
      assert(SparseVector(300, Array(1, 2, 6, 7, 9, 12), Array(0.0, 9.7, 0.0, 7.6, 7.8, 7.9)).numNonzeros == 4)
    }
  }

  describe("#foreachActive") {
    it("iterates through every value") {
      val dense = SparseVector(400, Array(20, 45, 78), Array(3.4, 5.6, 7.8))
      val indices = new Array[Int](3)
      val values = new Array[Double](3)
      val expectedIndices = Array(20, 45, 78)
      val expectedValues = Array(3.4, 5.6, 7.8)

      var i = 0
      dense.foreachActive {
        (index, value) =>
          indices(i) = index
          values(i) = value
          i = i + 1
      }

      assert(indices.sameElements(expectedIndices))
      assert(values.sameElements(expectedValues))
    }
  }
}