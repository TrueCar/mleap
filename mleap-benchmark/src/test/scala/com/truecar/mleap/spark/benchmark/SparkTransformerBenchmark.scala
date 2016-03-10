//package com.truecar.mleap.spark.benchmark
//
//import java.io.{FileInputStream, File}
//
//import com.esotericsoftware.kryo.io.Input
//import com.truecar.mleap.runtime.LocalLeapFrame
//import com.truecar.mleap.serialization.mleap.v1.MleapJsonSerializer
//import com.truecar.mleap.spark.benchmark.util.SparkSerializer
//import org.apache.spark.sql.{Row, SQLContext}
//import org.apache.spark.{SparkContext, SparkConf}
//import org.apache.spark.ml.Transformer
//import org.scalameter.Bench
//import scala.collection.JavaConverters._
//import org.scalameter.api._
//import org.scalameter.picklers.Implicits._
//import org.apache.log4j.Logger
//import org.apache.log4j.Level
//import com.truecar.mleap.spark.MleapSparkSupport._
//
///**
//  * Created by hwilkins on 3/3/16.
//  */
//object SparkTransformerBenchmark extends Bench.ForkedTime {
//  lazy override val executor = {
//    SeparateJvmsExecutor(
//      Executor.Warmer.Zero,
//      Aggregator.min[Double],
//      new Measurer.Default)
//  }
//
//  val mleapSerializer = MleapJsonSerializer
//  val classLoader = getClass.getClassLoader
//  val regressionFile = new File("/tmp/spark.transformer.kryo")
//  val frameFile = new File("/tmp/frame.json")
//
//  val inputStream = new FileInputStream(regressionFile)
//  val input = new Input(inputStream)
//
//  val regression: Transformer = SparkSerializer().read(input)
//  val frameInputStream = new FileInputStream(frameFile)
//  val frame = mleapSerializer.deserialize[LocalLeapFrame](frameInputStream)
//  frameInputStream.close()
//
//  Logger.getLogger("org").setLevel(Level.OFF)
//  Logger.getLogger("akka").setLevel(Level.OFF)
//
//  val sparkConf = new SparkConf()
//    .setAppName("Spark Transformer Benchmark")
//    .setMaster("local[1]")
//  val sc = new SparkContext(sparkConf)
//  val sqlContext = new SQLContext(sc)
//
//  val rdd = frame.dataset.data.map(a => Row(a.data: _*)).toList.asJava
//  val schema = frame.schema.toSpark
//  val sparkFrame = sqlContext.createDataFrame(rdd, schema)
//
//  val ranges = for {
//    size <- Gen.range("size")(100, 700, 200)
//  } yield 0 until size
//
//  measure method "transform" in {
//    using(ranges) in {
//      size =>
//        size.foreach {
//          _ => regression.transform(sparkFrame).head
//        }
//    }
//  }
//
////  sc.stop()
//}
