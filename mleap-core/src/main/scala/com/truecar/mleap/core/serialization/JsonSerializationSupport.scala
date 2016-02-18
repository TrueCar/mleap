package com.truecar.mleap.core.serialization

import com.truecar.mleap.core.serialization.JsonSerializationSupport._
import java.io._
import java.nio.charset.Charset
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import spray.json._

import scala.io.Source
import scala.util.Try

/**
  * Created by hwilkins on 11/19/15.
  */
sealed trait PrettyPrint
object PrettyPrint {
  object False extends PrettyPrint
  object True extends PrettyPrint
  val Default = True
}

sealed trait Compression
object Compression {
  object Gzip extends Compression
  object None extends Compression
  val Default = None
}


case class JsonStringParser(string: String) {
  def parseTo[T](implicit compression: Compression, reader: JsonReader[T]): Try[T] = {
    string.getBytes(JsonSerializationSupport.DefaultJsonCharset).parseTo[T]
  }
}

case class JsonByteArrayParser(bytes: Array[Byte]) {
  def parseTo[T](implicit compression: Compression, reader: JsonReader[T]): Try[T] = {
    new ByteArrayInputStream(bytes).parseTo[T]
  }
}

case class JsonFileParser(file: File) {
  def parseTo[T](implicit compression: Compression, reader: JsonReader[T]): Try[T] = {
    new FileInputStream(file).parseTo[T]
  }
}

case class JsonStreamParser(stream: InputStream) {
  def parseTo[T](implicit compression: Compression, reader: JsonReader[T]): Try[T] = {
    Try({
      val realStream = compression match {
        case Compression.Gzip => new GZIPInputStream(stream)
        case Compression.None => stream
      }

      val source = Source.fromInputStream(realStream)
      val bytes = source.map(_.toByte).toArray
      new String(bytes, JsonSerializationSupport.DefaultJsonCharset).parseJson.convertTo[T]
    })
  }
}

case class JsonSerializer(json: JsValue) {
  def serializeToString(implicit compression: Compression,
                        pretty: PrettyPrint): String = {
    new String(serializeToBytes, JsonSerializationSupport.DefaultJsonCharset)
  }

  def serializeToBytes(implicit compression: Compression,
                       pretty: PrettyPrint = PrettyPrint.Default): Array[Byte] = {
    val stream = new ByteArrayOutputStream()
    serializeToStream(stream)
    stream.toByteArray
  }

  def serializeToFile(file: File)
                     (implicit compression: Compression,
                      pretty: PrettyPrint) = {
    serializeToStream(new FileOutputStream(file))
  }

  def serializeToStream(stream: OutputStream)
                       (implicit compression: Compression,
                        pretty: PrettyPrint) = {
    val bytes: Array[Byte] = pretty match {
      case PrettyPrint.True => json.prettyPrint.getBytes(JsonSerializationSupport.DefaultJsonCharset)
      case PrettyPrint.False => json.compactPrint.getBytes(JsonSerializationSupport.DefaultJsonCharset)
    }
    val realStream = compression match {
      case Compression.Gzip => new GZIPOutputStream(stream)
      case Compression.None => stream
    }
    realStream.write(bytes)
  }

  def toOutputStream(implicit compression: Compression,
                     pretty: PrettyPrint) = {
    val stream = new ByteArrayOutputStream()
    stream.write(serializeToBytes)
    stream
  }
}

trait JsonSerializationSupport {
  import scala.language.implicitConversions

  implicit val mleapJsonDefaultCompression = Compression.Default
  implicit val mleapJsonDefaultPretty = PrettyPrint.Default

  implicit def jsonSerializer[T](t: T)(implicit writer: JsonWriter[T]): JsonSerializer = JsonSerializer(writer.write(t))

  implicit def jsonStringParser(string: String): JsonStringParser = JsonStringParser(string)
  implicit def jsonByteArrayParser(bytes: Array[Byte]): JsonByteArrayParser = JsonByteArrayParser(bytes)
  implicit def jsonStreamParser(stream: InputStream): JsonStreamParser = JsonStreamParser(stream)
  implicit def jsonFileParser(file: File): JsonFileParser = JsonFileParser(file)
}

object JsonSerializationSupport extends JsonSerializationSupport {
  val DefaultJsonCharset = Charset.forName("UTF-8")
}
