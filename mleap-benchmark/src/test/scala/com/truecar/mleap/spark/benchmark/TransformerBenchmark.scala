package com.truecar.mleap.spark.benchmark

import java.io.{FileInputStream, File}

import com.truecar.mleap.bundle.zip.ZipBundleReader
import com.truecar.mleap.runtime.LocalLeapFrame
import com.truecar.mleap.runtime.transformer.Transformer
import com.truecar.mleap.serialization.ml.json.DefaultJsonMlSerializer
import com.truecar.mleap.serialization.mleap.json.DefaultJsonMleapSerializer
import org.scalameter.api._
import org.scalameter.picklers.Implicits._

/**
  * Created by hwilkins on 2/23/16.
  */
object TransformerBenchmark extends Bench.ForkedTime {
  lazy override val executor = {
    SeparateJvmsExecutor(
      Executor.Warmer.Zero,
      Aggregator.min[Double],
      new Measurer.Default)
  }

  val mleapSerializer = DefaultJsonMleapSerializer.createSerializer()
  val mlSerializer = DefaultJsonMlSerializer.createSerializer()
  val classLoader = getClass.getClassLoader
  val regressionFile = new File("/tmp/transformer.mleap")
  val frameFile = new File("/tmp/frame.json")

  val bundleReader = ZipBundleReader(regressionFile)
  val regression = mlSerializer.deserializeWithClass(bundleReader).asInstanceOf[Transformer]
  bundleReader.in.close()

  val frameInputStream = new FileInputStream(frameFile)
  val frame = mleapSerializer.deserialize[LocalLeapFrame](frameInputStream)
  frameInputStream.close()

  val ranges = for {
    size <- Gen.range("size")(1000, 10000, 1000)
  } yield 0 until size

  measure method "transform" in {
    using(ranges) in {
      size =>
        size.foreach {
          _ => regression.transform(frame)
        }
    }
  }
}
