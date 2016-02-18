package com.truecar.mleap.core.regression

import org.scalatest.FunSpec
import com.truecar.mleap.core.linalg

/**
  * Created by hwilkins on 1/21/16.
  */
class LinearRegressionSpec extends FunSpec {
  describe("#apply") {
    it("applies the linear regression to a feature vector") {
      val linearRegression = LinearRegression(linalg.Vector.dense(Array(0.5, 0.75, 0.25)), .33)
      assert(linearRegression(linalg.Vector.dense(Array(1.0, 0.5, 1.0))) == 1.455)
    }
  }
}
