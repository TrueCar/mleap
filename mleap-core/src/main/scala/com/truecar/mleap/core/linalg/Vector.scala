package com.truecar.mleap.core.linalg

/**
 * Created by hwilkins on 11/5/15.
 */
object Vector {
  import scala.language.implicitConversions

  def dense(values: Array[Double]): DenseVector = DenseVector(values)
  def sparse(size: Int, indices: Array[Int], values: Array[Double]): SparseVector = SparseVector(size, indices, values)

  def sparse(size: Int, elements: Seq[(Int, Double)]): Vector = {
    require(size > 0, "The size of the requested sparse vector must be greater than 0.")

    val (indices, values) = elements.sortBy(_._1).unzip
    var prev = -1
    indices.foreach { i =>
      require(prev < i, s"Found duplicate indices: $i.")
      prev = i
    }
    require(prev < size, s"You may not write an element to index $prev because the declared " +
      s"size of your vector is $size")

    new SparseVector(size, indices.toArray, values.toArray)
  }

  def zeros(size: Int): Vector = {
    new DenseVector(new Array[Double](size))
  }

  def fromBreeze(breezeVector: breeze.linalg.Vector[Double]): Vector = {
    breezeVector match {
      case v: breeze.linalg.DenseVector[Double] =>
        if (v.offset == 0 && v.stride == 1 && v.length == v.data.length) {
          new DenseVector(v.data)
        } else {
          new DenseVector(v.toArray)  // Can't use underlying array directly, so make a new one
        }
      case v: breeze.linalg.SparseVector[Double] =>
        if (v.index.length == v.used) {
          new SparseVector(v.length, v.index, v.data)
        } else {
          new SparseVector(v.length, v.index.slice(0, v.used), v.data.slice(0, v.used))
        }
      case v: breeze.linalg.Vector[_] =>
        sys.error("Unsupported Breeze vector type: " + v.getClass.getName)
    }
  }

  implicit def vectorToBreeze(vector: Vector): breeze.linalg.Vector[Double] = vector.toBreeze
}

trait Vector extends Serializable {
  def apply(index: Int): Double = toBreeze(index)
  def toBreeze: breeze.linalg.Vector[Double]
  def foreachActive(f: (Int, Double) => Unit)

  def size: Int

  def toArray: Array[Double]
  def toSparse: SparseVector
  def toDense: DenseVector = DenseVector(toArray)

  def numActives: Int
  def numNonzeros: Int
  def argmax: Int

  def compressed: Vector = {
    val nnz = numNonzeros
    // A dense vector needs 8 * size + 8 bytes, while a sparse vector needs 12 * nnz + 20 bytes.
    if (1.5 * (nnz + 1.0) < size) {
      toSparse
    } else {
      toDense
    }
  }
}

case class SparseVector(size: Int,
                        indices: Array[Int],
                        values: Array[Double]) extends Vector {
  override def toBreeze: breeze.linalg.SparseVector[Double] = new breeze.linalg.SparseVector[Double](indices, values, size)

  override def toArray: Array[Double] = {
    val data = new Array[Double](size)
    var i = 0
    val nnz = indices.length
    while (i < nnz) {
      data(indices(i)) = values(i)
      i += 1
    }
    data
  }

  override def toSparse: SparseVector = {
    val nnz = numNonzeros
    if (nnz == numActives) {
      this
    } else {
      val ii = new Array[Int](nnz)
      val vv = new Array[Double](nnz)
      var k = 0
      foreachActive { (i, v) =>
        if (v != 0.0) {
          ii(k) = i
          vv(k) = v
          k += 1
        }
      }
      new SparseVector(size, ii, vv)
    }
  }

  override def numActives: Int = values.length

  override def numNonzeros: Int = {
    var nnz = 0
    values.foreach { v =>
      if (v != 0.0) {
        nnz += 1
      }
    }
    nnz
  }

  override def argmax: Int = {
    if (size == 0) {
      -1
    } else {
      // Find the max active entry.
      var maxIdx = indices(0)
      var maxValue = values(0)
      var maxJ = 0
      var j = 1
      val na = numActives
      while (j < na) {
        val v = values(j)
        if (v > maxValue) {
          maxValue = v
          maxIdx = indices(j)
          maxJ = j
        }
        j += 1
      }

      // If the max active entry is nonpositive and there exists inactive ones, find the first zero.
      if (maxValue <= 0.0 && na < size) {
        if (maxValue == 0.0) {
          // If there exists an inactive entry before maxIdx, find it and return its index.
          if (maxJ < maxIdx) {
            var k = 0
            while (k < maxJ && indices(k) == k) {
              k += 1
            }
            maxIdx = k
          }
        } else {
          // If the max active value is negative, find and return the first inactive index.
          var k = 0
          while (k < na && indices(k) == k) {
            k += 1
          }
          maxIdx = k
        }
      }

      maxIdx
    }
  }

  override def foreachActive(f: (Int, Double) => Unit): Unit = {
    var i = 0
    val localValuesSize = values.length
    val localIndices = indices
    val localValues = values

    while (i < localValuesSize) {
      f(localIndices(i), localValues(i))
      i += 1
    }
  }
}
case class DenseVector(values: Array[Double]) extends Vector {
  override def toBreeze: breeze.linalg.DenseVector[Double] = new breeze.linalg.DenseVector[Double](values)

  override def size: Int = values.length

  override def toArray: Array[Double] = values

  override def toSparse: SparseVector = {
    val nnz = numNonzeros
    val ii = new Array[Int](nnz)
    val vv = new Array[Double](nnz)
    var k = 0
    foreachActive { (i, v) =>
      if (v != 0) {
        ii(k) = i
        vv(k) = v
        k += 1
      }
    }
    SparseVector(size, ii, vv)
  }

  override def numActives: Int = size

  override def numNonzeros: Int = {
    // same as values.count(_ != 0.0) but faster
    var nnz = 0
    values.foreach { v =>
      if (v != 0.0) {
        nnz += 1
      }
    }
    nnz
  }

  override def argmax: Int = {
    if (size == 0) {
      -1
    } else {
      var maxIdx = 0
      var maxValue = values(0)
      var i = 1
      while (i < size) {
        if (values(i) > maxValue) {
          maxIdx = i
          maxValue = values(i)
        }
        i += 1
      }
      maxIdx
    }
  }

  override def foreachActive(f: (Int, Double) => Unit): Unit = {
    var i = 0
    val localValuesSize = values.length
    val localValues = values

    while (i < localValuesSize) {
      f(i, localValues(i))
      i += 1
    }
  }
}
