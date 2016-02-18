package com.truecar.mleap.core.feature

import com.truecar.mleap.core.linalg
import org.scalatest.FunSpec

/**
  * Created by hwilkins on 1/21/16.
  */
class VectorAssemblerSpec extends FunSpec {
  describe("#apply") {
    it("assembles doubles and vectors into a new vector") {
      val assembler = VectorAssembler.default
      val expectedArray = Array(45.0, 76.8, 23.0, 45.6, 0.0, 22.3, 45.6, 0.0, 99.3)

      assert(assembler(45.0,
        76.8,
        linalg.Vector.dense(Array(23.0, 45.6)),
        linalg.Vector.sparse(5, Array(1, 2, 4), Array(22.3, 45.6, 99.3))).toArray.sameElements(expectedArray))
    }
  }
}
