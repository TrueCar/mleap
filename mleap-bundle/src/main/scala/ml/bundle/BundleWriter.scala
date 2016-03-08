package ml.bundle

import java.io.{Closeable, OutputStream}

/**
  * Created by hwilkins on 3/7/16.
  */
trait BundleWriter {
  def contentWriter(name: String): OutputStream
  def createBundle(name: String): BundleWriter

  def close(stream: Closeable): Unit
}
