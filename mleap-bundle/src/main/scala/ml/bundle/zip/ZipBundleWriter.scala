package ml.bundle.zip

import java.io.{FileOutputStream, OutputStream, Closeable, File}
import java.util.zip.{ZipEntry, ZipOutputStream}

import ml.bundle.BundleWriter

/**
  * Created by hwilkins on 3/7/16.
  */
object ZipBundleWriter {
  def apply(path: File): ZipBundleWriter = {
    ZipBundleWriter(new ZipOutputStream(new FileOutputStream(path)))
  }
}

case class ZipBundleWriter(out: ZipOutputStream, path: File = new File("bundle")) extends BundleWriter {
  override def contentWriter(name: String): OutputStream = {
    out.putNextEntry(new ZipEntry(new File(path, name).toString))
    out
  }

  override def createBundle(name: String): BundleWriter = {
    ZipBundleWriter(out, new File(path, name))
  }

  override def close(stream: Closeable): Unit = { out.closeEntry() }
}
