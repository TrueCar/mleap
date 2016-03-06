package com.truecar.mleap.bundle.fs

import java.io._

import com.truecar.mleap.bundle.Bundle

/**
  * Created by hwilkins on 3/4/16.
  */
case class DirectoryBundle(path: File) extends Bundle {
  override def contentWriter(name: String): OutputStream = {
    new FileOutputStream(new File(path, name))
  }

  override def contentReader(name: String): InputStream = {
    new FileInputStream(new File(path, name))
  }

  override def createBundle(name: String): Bundle = {
    val file = new File(path, name)
    file.mkdirs()

    DirectoryBundle(file)
  }

  override def getBundle(name: String): Bundle = {
    DirectoryBundle(new File(path, name))
  }
}
