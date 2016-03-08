package com.truecar.mleap.serialization.ml.bundle.core.regression

import ml.bundle.{BundleReader, BundleWriter, BundleSerializer}
import com.truecar.mleap.core.regression.DecisionTreeRegression
import com.truecar.mleap.core.tree.Node

/**
  * Created by hwilkins on 3/6/16.
  */
case class DecisionTreeRegressionSerializer(nodeSerializer: BundleSerializer[Node]) extends BundleSerializer[DecisionTreeRegression] {
  override val key: String = "ml.core.regression.DecisionTreeRegression"

  override def serialize(obj: DecisionTreeRegression, bundle: BundleWriter): Unit = {
    nodeSerializer.serialize(obj.rootNode, bundle.createBundle("rootNode"))
  }

  override def deserialize(bundle: BundleReader): DecisionTreeRegression = {
    val rootNode = nodeSerializer.deserialize(bundle.getBundle("rootNode"))
    DecisionTreeRegression(rootNode)
  }
}
