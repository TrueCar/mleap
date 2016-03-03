package com.truecar.mleap.spark.benchmark.util

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}
import com.twitter.chill.ScalaKryoInstantiator

/**
  * Created by hwilkins on 3/2/16.
  */
object SparkSerializer {
  def apply(): SparkSerializer = {
    val kryoInstantiator = new ScalaKryoInstantiator()
    kryoInstantiator.setRegistrationRequired(false)
    val kryo = kryoInstantiator.newKryo()
    kryo.setClassLoader(Thread.currentThread.getContextClassLoader)

    SparkSerializer(kryo)
  }
}

case class SparkSerializer(kryo: Kryo) {
  def write[T](obj: T, output: Output) = {
    kryo.writeClassAndObject(output, obj)
  }

  def read[T](input: Input): T = {
    kryo.readClassAndObject(input).asInstanceOf[T]
  }
}
