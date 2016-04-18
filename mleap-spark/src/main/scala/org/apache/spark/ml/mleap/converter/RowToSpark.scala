package org.apache.spark.ml.mleap.converter

import com.truecar.mleap.core.linalg.Vector
import com.truecar.mleap.runtime.{Row => MleapRow}
import com.truecar.mleap.spark.MleapSparkSupport
import org.apache.spark.sql.Row
import MleapSparkSupport._

/**
  * Created by hwilkins on 11/18/15.
  */
case class RowToSpark(row: MleapRow) {
  def toSpark: Row = {
    val values = row.toArray.map {
      case value: Vector => value.toSpark
      case value => value
    }

    Row(values: _*)
  }
}
