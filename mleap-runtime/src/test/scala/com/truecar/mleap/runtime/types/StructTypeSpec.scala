package com.truecar.mleap.runtime.types

import org.scalatest.{TryValues, GivenWhenThen, FunSuite}

/**
  * Created by pahsan on 3/8/16.
  */
class StructTypeSpec extends FunSuite with GivenWhenThen with TryValues{
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

  test("The contains operation should return true whenever a field exists"){
    assert(fields.map(f => testStruct.contains(f.name)).forall(identity))
  }

  test("The contains operation should return false when a field doesn't exist"){
    val fieldsPrime = fields:+StructField("sixth", DataType.fromName("string"))

    assert(!fieldsPrime.map(f => testStruct.contains(f.name)).forall(identity))
  }

  test("withField should return a StructType with the field added"){
    val field = StructField("sixth", DataType.fromName("string"))

    assert(testStruct.withField(field).contains(field.name))
  }

  test("Dropping a field from a StructType should remove the field"){
    assert(testStruct.dropField("first").get.getField("first").isEmpty)
  }

  test("select should return a StructType with selected fields"){
    Given("an array of valid String names")
    val selection = Array("first", "second", "third")

    When("a selection is made")
    val selectedFields = testStruct.select(selection:_*)

    Then("the operation should return a success")
    assert(selectedFields.isSuccess)

    And("the StructType should contain the selected fields")
    assert(selection.map(f => selectedFields.success.value.contains(f)).forall(identity))
  }
}
