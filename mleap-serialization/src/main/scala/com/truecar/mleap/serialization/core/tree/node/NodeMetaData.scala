package com.truecar.mleap.serialization.core.tree.node

/**
  * Created by hwilkins on 3/6/16.
  */
sealed trait NodeFormat
object NodeFormat {
  object Linear extends NodeFormat
}

case class NodeMetaData(format: NodeFormat = NodeFormat.Linear)
