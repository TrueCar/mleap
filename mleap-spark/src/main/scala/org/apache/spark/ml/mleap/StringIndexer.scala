/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Original code here: https://github.com/apache/spark/blob/master/mllib/src/main/scala/org/apache/spark/ml/feature/StringIndexer.scala

package org.apache.spark.ml.mleap

import org.apache.spark.SparkException
import org.apache.spark.annotation.Experimental
import org.apache.spark.ml.{Estimator, Model}
import org.apache.spark.ml.attribute.{Attribute, NominalAttribute}
import org.apache.spark.ml.param._
import org.apache.spark.ml.param.shared._
import org.apache.spark.ml.Transformer
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.util.collection.OpenHashMap

private[ml] trait HasHandleInvalid extends Params {
  final val handleInvalid: Param[String] = new Param[String](this, "handleInvalid", "how to handle invalid entries. Options are skip (which will filter out rows with bad values), or error (which will throw an errror). More options may be added later.", ParamValidators.inArray(Array("skip", "error")))
  final def getHandleInvalid: String = $(handleInvalid)
}

private[mleap] trait StringIndexerBase extends Params with HasInputCol with HasOutputCol
with HasHandleInvalid {
  protected def validateAndTransformSchema(schema: StructType): StructType = {
    val inputColName = $(inputCol)
    val inputDataType = schema(inputColName).dataType
    require(inputDataType == StringType || inputDataType.isInstanceOf[NumericType],
      s"The input column $inputColName must be either string type or numeric type, " +
        s"but got $inputDataType.")
    val inputFields = schema.fields
    val outputColName = $(outputCol)
    require(inputFields.forall(_.name != outputColName),
      s"Output column $outputColName already exists.")
    val attr = NominalAttribute.defaultAttr.withName($(outputCol))
    val outputFields = inputFields :+ attr.toStructField()
    StructType(outputFields)
  }
}
@Experimental
class StringIndexer(override val uid: String) extends Estimator[StringIndexerModel]
with StringIndexerBase {

  def this() = this(Identifiable.randomUID("strIdx"))

  def setHandleInvalid(value: String): this.type = set(handleInvalid, value)
  setDefault(handleInvalid, "error")

  def setInputCol(value: String): this.type = set(inputCol, value)

  def setOutputCol(value: String): this.type = set(outputCol, value)


  override def fit(dataset: DataFrame): StringIndexerModel = {
    val counts = dataset.select(col($(inputCol)).cast(StringType))
      .map(_.getString(0))
      .countByValue()
    val labels = counts.toSeq.sortBy(-_._2).map(_._1).toArray
    copyValues(new StringIndexerModel(uid, labels).setParent(this))
  }

  override def transformSchema(schema: StructType): StructType = {
    validateAndTransformSchema(schema)
  }

  override def copy(extra: ParamMap): StringIndexer = defaultCopy(extra)
}

@Experimental
class StringIndexerModel (
                           override val uid: String,
                           val labels: Array[String]) extends Model[StringIndexerModel] with StringIndexerBase {

  def this(labels: Array[String]) = this(Identifiable.randomUID("strIdx"), labels)

  private val labelToIndex: OpenHashMap[String, Double] = {
    val n = labels.length
    val map = new OpenHashMap[String, Double](n)
    var i = 0
    while (i < n) {
      map.update(labels(i), i)
      i += 1
    }
    map
  }

  def getLabels: Array[String] = labels

  def setHandleInvalid(value: String): this.type = set(handleInvalid, value)
  setDefault(handleInvalid, "error")

  def setInputCol(value: String): this.type = set(inputCol, value)

  def setOutputCol(value: String): this.type = set(outputCol, value)

  override def transform(dataset: DataFrame): DataFrame = {
    if (!dataset.schema.fieldNames.contains($(inputCol))) {
      logInfo(s"Input column ${$(inputCol)} does not exist during transformation. " +
        "Skip StringIndexerModel.")
      return dataset
    }

    val indexer = udf { label: String =>
      if (labelToIndex.contains(label)) {
        labelToIndex(label)
      } else {
        throw new SparkException(s"Unseen label: $label.")
      }
    }

    val metadata = NominalAttribute.defaultAttr
      .withName($(inputCol)).withValues(labels).toMetadata()
    // If we are skipping invalid records, filter them out.
    val filteredDataset = (getHandleInvalid) match {
      case "skip" => {
        val filterer = udf { label: String =>
          labelToIndex.contains(label)
        }
        dataset.where(filterer(dataset($(inputCol))))
      }
      case _ => dataset
    }
    filteredDataset.select(col("*"),
      indexer(dataset($(inputCol)).cast(StringType)).as($(outputCol), metadata))
  }

  override def transformSchema(schema: StructType): StructType = {
    if (schema.fieldNames.contains($(inputCol))) {
      validateAndTransformSchema(schema)
    } else {
      // If the input column does not exist during transformation, we skip StringIndexerModel.
      schema
    }
  }

  override def copy(extra: ParamMap): StringIndexerModel = {
    val copied = new StringIndexerModel(uid, labels)
    copyValues(copied, extra).setParent(parent)
  }
}

@Experimental
class IndexToString private[ml] (
                                  override val uid: String) extends Transformer
with HasInputCol with HasOutputCol {

  def this() =
    this(Identifiable.randomUID("idxToStr"))

  def setInputCol(value: String): this.type = set(inputCol, value)

  def setOutputCol(value: String): this.type = set(outputCol, value)

  def setLabels(value: Array[String]): this.type = set(labels, value)

  final val labels: StringArrayParam = new StringArrayParam(this, "labels",
    "Optional array of labels specifying index-string mapping." +
      " If not provided or if empty, then metadata from inputCol is used instead.")
  setDefault(labels, Array.empty[String])

  final def getLabels: Array[String] = $(labels)

  override def transformSchema(schema: StructType): StructType = {
    val inputColName = $(inputCol)
    val inputDataType = schema(inputColName).dataType
    require(inputDataType.isInstanceOf[NumericType],
      s"The input column $inputColName must be a numeric type, " +
        s"but got $inputDataType.")
    val inputFields = schema.fields
    val outputColName = $(outputCol)
    require(inputFields.forall(_.name != outputColName),
      s"Output column $outputColName already exists.")
    val outputFields = inputFields :+ StructField($(outputCol), StringType)
    StructType(outputFields)
  }

  override def transform(dataset: DataFrame): DataFrame = {
    val inputColSchema = dataset.schema($(inputCol))
    // If the labels array is empty use column metadata
    val values = if ($(labels).isEmpty) {
      Attribute.fromStructField(inputColSchema)
        .asInstanceOf[NominalAttribute].values.get
    } else {
      $(labels)
    }
    val indexer = udf { index: Double =>
      val idx = index.toInt
      if (0 <= idx && idx < values.length) {
        values(idx)
      } else {
        throw new SparkException(s"Unseen index: $index ??")
      }
    }
    val outputColName = $(outputCol)
    dataset.select(col("*"),
      indexer(dataset($(inputCol)).cast(DoubleType)).as(outputColName))
  }

  override def copy(extra: ParamMap): IndexToString = {
    defaultCopy(extra)
  }
}
