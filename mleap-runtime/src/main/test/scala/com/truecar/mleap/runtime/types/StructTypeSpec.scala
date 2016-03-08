package com.truecar.mleap.runtime.types

import org.scalatest.FunSuite

/**
  * Created by pahsan on 3/8/16.
  */
class StructTypeSpec extends FunSuite{
  val fields = Seq(StructField("first", DataType.fromName("string")),
                   StructField("second", DataType.fromName("string")),
                   StructField("third", DataType.fromName("string")),
                   StructField("fourth", DataType.fromName("string")),
                   StructField("fifth", DataType.fromName("string"))
  )

  val testStruct = StructType(fields)

  test("getField should return the desired field wrapped in an Option"){
    assert(testStruct.getField("first").get.name == "first")
  }

  test("indexOf should return the integer index of the desired field"){
    assert(testStruct.indexOf("first") == 0)
  }

  test("getIndexOf should return the same thing as indexOf, wrapped in an Option"){
    assert(testStruct.getIndexOf("first").get == testStruct.indexOf("first"))
  }

  test("Dropping a field from a StructType should remove the field"){
    assert(testStruct.dropField("first").get.getField("first").isEmpty)
  }
}
