package com.truecar.mleap.serialization.ml.bundle.runtime

import java.io.{InputStreamReader, BufferedReader}

import ml.bundle._
import com.truecar.mleap.runtime.transformer.{PipelineModel, Transformer}

import scala.collection.mutable

/**
  * Created by hwilkins on 3/5/16.
  */
case class PipelineModelSerializer(serializer: Serializer) extends BundleSerializer[PipelineModel] {
  override val key: String = "ml.runtime.PipelineModel"

  override def serialize(obj: PipelineModel, bundle: BundleWriter): Unit = {
    val metaWriter = bundle.contentWriter("meta")

    obj.transformers.zipWithIndex.foreach {
      case (stage, index) =>
        val mlKey = serializer.getMlName(stage.getClass.getCanonicalName)

        metaWriter.write(mlKey.getBytes)
        metaWriter.write('\n')
    }

    bundle.close(metaWriter)

    obj.transformers.zipWithIndex.foreach {
      case (stage, index) =>
        val mlKey = serializer.getMlName(stage.getClass.getCanonicalName)

        serializer.getSerializer(mlKey) match {
          case Some(serializer) =>
            val contentWriter = bundle.contentWriter(s"stage_$index")
            serializer.serializeAny(stage, contentWriter)
            bundle.close(contentWriter)
          case None =>
            serializer.getBundleSerializer(mlKey) match {
              case Some(serializer) =>
                val subBundle = bundle.createBundle(s"stage_$index")
                serializer.serializeAny(stage, subBundle)
              case None => throw new Error("Could not serialize: " + mlKey)
            }
        }
    }
  }

  override def deserialize(bundle: BundleReader): PipelineModel = {
    val metaReader = new BufferedReader(new InputStreamReader(bundle.contentReader("meta")))
    var hasLine = true
    var keys = mutable.ArrayBuffer.empty[String]

    while(hasLine) {
      val line = metaReader.readLine()

      if(line != null) {
        val mlKey = line.trim()
        keys += mlKey
      } else {
        hasLine = false
      }
    }

    bundle.close(metaReader)

    val transformers = keys.toSeq.zipWithIndex.map {
      case (mlKey, index) =>
        serializer.getSerializer(mlKey) match {
          case Some(streamSerializer) =>
            val contentInputStream = bundle.contentReader(s"stage_$index")
            val transformer = streamSerializer.deserializeAny(contentInputStream).asInstanceOf[Transformer]
            bundle.close(contentInputStream)
            transformer
          case None =>
            serializer.getBundleSerializer(mlKey) match {
              case Some(bundleSerializer) =>
                bundleSerializer.deserializeAny(bundle.getBundle(s"stage_$index")).asInstanceOf[Transformer]
              case None =>
                throw new Error("Could not deserialize: " + mlKey)
            }
        }
    }

    PipelineModel(transformers)
  }
}
