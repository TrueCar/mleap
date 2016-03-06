package com.truecar.mleap.serialization

import java.io._

/**
  * Created by hwilkins on 3/4/16.
  */
case class DirectoryBundle(path: File) extends Bundle {
  override def contentWriter(name: String): BufferedWriter = {
    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path, name))))
  }

  override def contentReader(name: String): BufferedReader = {
    new BufferedReader(new InputStreamReader(new FileInputStream(new File(path, name))))
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
