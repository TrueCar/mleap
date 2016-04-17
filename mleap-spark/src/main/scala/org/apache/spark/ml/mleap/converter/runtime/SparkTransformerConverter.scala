package org.apache.spark.ml.mleap.converter.runtime

import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.Transformer

import scala.reflect.ClassTag

/**
  * Created by hollinwilkins on 4/17/16.
  */
trait SparkTransformerConverter {
  var converters: Map[String, TransformerToMleap[_ <: Transformer, _ <: transformer.Transformer]] = Map()

  def addConverter[T <: Transformer, MT <: transformer.Transformer](converter: TransformerToMleap[T, MT])
                                                                   (implicit ct: ClassTag[T]): TransformerToMleap[T, MT] = {
    val name = ct.runtimeClass.getCanonicalName
    converters += (name -> converter)
    converter
  }

  def getConverter(key: String): TransformerToMleap[_ <: Transformer, _ <: transformer.Transformer] = {
    converters(key)
  }

  def convert(t: Transformer): transformer.Transformer = {
    getConverter(t.getClass.getCanonicalName).toMleapLifted(t)
  }
}
