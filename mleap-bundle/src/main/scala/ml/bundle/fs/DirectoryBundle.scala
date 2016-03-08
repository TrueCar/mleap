package ml.bundle.fs

import java.io._

import ml.bundle.{BundleWriter, BundleReader}

/**
  * Created by hwilkins on 3/4/16.
  */
case class DirectoryBundle(path: File) extends BundleReader with BundleWriter {
  override def contentWriter(name: String): OutputStream = {
    new FileOutputStream(new File(path, name))
  }

  override def contentReader(name: String): InputStream = {
    new FileInputStream(new File(path, name))
  }

  override def createBundle(name: String): DirectoryBundle = {
    val file = new File(path, name)
    file.mkdirs()

    DirectoryBundle(file)
  }

  override def getBundle(name: String): DirectoryBundle = {
    DirectoryBundle(new File(path, name))
  }

  override def close(stream: Closeable): Unit = stream.close()
}
