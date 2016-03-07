package com.truecar.mleap.bundle

import java.io.{Closeable, InputStream}

/**
  * Created by hwilkins on 3/7/16.
  */
trait BundleReader {
  def contentReader(name: String): InputStream
  def getBundle(name: String): BundleReader

  def close(stream: Closeable): Unit
}
