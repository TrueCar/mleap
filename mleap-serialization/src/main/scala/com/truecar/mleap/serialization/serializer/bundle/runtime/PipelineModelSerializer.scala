package com.truecar.mleap.serialization.serializer.bundle.runtime

import java.io.{InputStreamReader, BufferedReader}

import com.truecar.mleap.bundle.{BundleSerializer, Bundle}
import com.truecar.mleap.runtime.transformer.{PipelineModel, Transformer}
import com.truecar.mleap.serialization.MleapSerializer

import scala.collection.mutable

/**
  * Created by hwilkins on 3/5/16.
  */
case class PipelineModelSerializer(mleap: MleapSerializer) extends BundleSerializer[PipelineModel] {
  override val klazz: Class[PipelineModel] = classOf[PipelineModel]

  override def serialize(obj: PipelineModel, bundle: Bundle): Unit = {
    val metaWriter = bundle.contentWriter("meta")
    val contentWriter = bundle.contentWriter("content")

    obj.transformers.zipWithIndex.foreach {
      case (stage, index) =>
        val key = stage.getClass.getCanonicalName

        metaWriter.write(key.getBytes)
        metaWriter.write('\n')

        mleap.getSerializer(key) match {
          case Some(serializer) =>
            serializer.serializeAny(stage, contentWriter)
          case None =>
            mleap.getBundleSerializer(key) match {
              case Some(serializer) =>
                val subBundle = bundle.createBundle(index.toString)
                serializer.serializeAny(stage, subBundle)
              case None => throw new Error("Could not serialize: " + key)
            }
        }
    }

    metaWriter.close()
    contentWriter.close()
  }

  override def deserialize(bundle: Bundle): PipelineModel = {
    val metaReader = new BufferedReader(new InputStreamReader(bundle.contentReader("meta")))
    val contentInputStream = bundle.contentReader("content")

    var hasLine = true
    var index = 0
    var transformers = mutable.ArrayBuffer.empty[Transformer]
    while(hasLine) {
      val line = metaReader.readLine()

      if(line != null) {
        val key = line.trim()

        val transformer = mleap.getSerializer(key) match {
          case Some(serializer) =>
            serializer.deserializeAny(contentInputStream).asInstanceOf[Transformer]
          case None =>
            mleap.getBundleSerializer(key) match {
              case Some(serializer) =>
                serializer.deserializeAny(bundle.getBundle(index.toString)).asInstanceOf[Transformer]
              case None =>
                throw new Error("Could not deserialize: " + key)
            }
        }

        transformers += transformer

        index += 1
      } else {
        hasLine = false
      }
    }

    metaReader.close()
    contentInputStream.close()

    PipelineModel(transformers)
  }
}
