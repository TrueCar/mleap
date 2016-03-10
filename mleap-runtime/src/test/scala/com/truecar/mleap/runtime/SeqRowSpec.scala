package com.truecar.mleap.runtime

import org.scalatest.FunSuite

/**
  * Created by hollinwilkins on 3/10/16.
  */
class SeqRowSpec extends FunSuite {
  val row = SeqRow(Seq(23.0, 54.0, 887.0, 23.0, "hello"))

  test("withValue prepends the value to the Row") {
    assert(row.withValue("hiya").toSeq.head == "hiya")
  }
}
