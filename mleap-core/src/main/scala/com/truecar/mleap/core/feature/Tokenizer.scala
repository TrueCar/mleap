package com.truecar.mleap.core.feature

/**
  * Created by hwilkins on 12/30/15.
  */
object Tokenizer {
  val defaultTokenizer = Tokenizer("\\s")
}

case class Tokenizer(regex: String = "\\s") {
  def apply(document: String): Array[String] = document.toLowerCase.split(regex)
}
