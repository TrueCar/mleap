package com.truecar.mleap.serialization

import java.io.{BufferedReader, BufferedWriter}

/**
  * Created by hwilkins on 3/4/16.
  */
trait Bundle {
  def contentWriter(name: String): BufferedWriter
  def createBundle(name: String): Bundle

  def contentReader(name: String): BufferedReader
  def getBundle(name: String): Bundle
}
