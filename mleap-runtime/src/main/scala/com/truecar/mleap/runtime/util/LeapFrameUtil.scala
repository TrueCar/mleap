package com.truecar.mleap.runtime.util

import com.truecar.mleap.runtime.{Row, LocalDataset, LocalLeapFrame}
import com.truecar.mleap.runtime.types.{StructType, StructField}

/**
  * Created by hwilkins on 12/28/15.
  */
trait LeapFrameUtil {
  def fromMap(map: Map[String, Any]): LocalLeapFrame = {
    val (fields, rowData) = map.foldLeft((Seq[StructField](), Seq[Any]())) {
      case ((f, r), (name, value)) =>
        val f2 = f
        val r2 = r

        (f2, r2)
    }

    val schema = StructType(fields)
    val localDataset = LocalDataset(Array(Row(rowData: _*)))
    LocalLeapFrame(schema, localDataset)
  }
}
