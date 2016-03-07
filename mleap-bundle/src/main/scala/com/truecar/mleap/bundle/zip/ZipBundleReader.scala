package com.truecar.mleap.bundle.zip

import java.io.{FileInputStream, InputStream, Closeable, File}
import java.util.zip.ZipInputStream

import com.truecar.mleap.bundle.BundleReader

/**
  * Created by hwilkins on 3/7/16.
  */
object ZipBundleReader {
  def apply(path: File): ZipBundleReader = {
    ZipBundleReader(new ZipInputStream(new FileInputStream(path)))
  }
}

case class ZipBundleReader(in: ZipInputStream, path: File = new File("bundle")) extends BundleReader {
  override def contentReader(name: String): InputStream = {
    val entry = in.getNextEntry
    if(entry.getName != new File(path, name).toString) {
      throw new Error("Attempted to read zip file out of order")
    }
    in
  }

  override def getBundle(name: String): BundleReader = {
    ZipBundleReader(in, new File(path, name))
  }

  override def close(stream: Closeable): Unit = {
    in.closeEntry()
  }
}
