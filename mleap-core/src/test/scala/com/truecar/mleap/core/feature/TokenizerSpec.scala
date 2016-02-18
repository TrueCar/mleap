package com.truecar.mleap.core.feature

import org.scalatest.FunSpec

/**
  * Created by hwilkins on 1/21/16.
  */
class TokenizerSpec extends FunSpec {
  describe("#apply") {
    val tokenizer = Tokenizer()

    assert(tokenizer("hello there dude").sameElements(Array("hello", "there", "dude")))
  }
}
