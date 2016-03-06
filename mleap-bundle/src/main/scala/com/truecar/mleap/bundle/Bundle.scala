package com.truecar.mleap.bundle

import java.io.{InputStream, OutputStream}

/**
  * Created by hwilkins on 3/4/16.
  */
trait Bundle {
  def contentWriter(name: String): OutputStream
  def createBundle(name: String): Bundle

  def contentReader(name: String): InputStream
  def getBundle(name: String): Bundle
}
