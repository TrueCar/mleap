package com.truecar.mleap.core.feature

import com.truecar.mleap.core.linalg
import org.scalatest.FunSpec

/**
  * Created by hwilkins on 1/21/16.
  */
class StandardScalerSpec extends FunSpec {
  describe("#apply") {
    describe("with mean") {
      it("scales based off of the mean") {
        val scaler = StandardScaler(None, Some(linalg.Vector.dense(Array(50.0, 20.0, 30.0))))
        val expectedVector = Array(5.0, 5.0, 3.0)

        assert(scaler(linalg.Vector.dense(Array(55.0, 25.0, 33.0))).toArray.sameElements(expectedVector))
      }
    }

    describe("with stdev") {
      it("scales based off the standard deviation") {
        val scaler = StandardScaler(Some(linalg.Vector.dense(Array(2.5, 8.0, 10.0))), None)
        val expectedVector = Array(1.6, .4375, 1.0)

        assert(scaler(linalg.Vector.dense(Array(4.0, 3.5, 10.0))).toArray.sameElements(expectedVector))
      }
    }

    describe("with mean and stdev") {
      it("scales based off the mean and standard deviation") {
        val scaler = StandardScaler(Some(linalg.Vector.dense(Array(2.5, 8.0, 10.0))),
          Some(linalg.Vector.dense(Array(50.0, 20.0, 30.0))))
        val expectedVector = Array(1.6, .4375, 1.0)

        assert(scaler(linalg.Vector.dense(Array(54.0, 23.5, 40.0))).toArray.sameElements(expectedVector))
      }
    }
  }
}
