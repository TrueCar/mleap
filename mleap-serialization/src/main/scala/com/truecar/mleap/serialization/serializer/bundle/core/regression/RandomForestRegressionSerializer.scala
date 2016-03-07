package com.truecar.mleap.serialization.serializer.bundle.core.regression

import com.truecar.mleap.bundle.{StreamSerializer, Bundle, BundleSerializer}
import com.truecar.mleap.core.regression.{RandomForestRegression, DecisionTreeRegression}
import ml.core.regression.RandomForestRegressionMetaData.RandomForestRegressionMetaData

/**
  * Created by hwilkins on 3/6/16.
  */
case class RandomForestRegressionSerializer(randomForestMetaDataSerialize: StreamSerializer[RandomForestRegressionMetaData],
                                            decisionTreeRegressionSerialize: BundleSerializer[DecisionTreeRegression])
  extends BundleSerializer[RandomForestRegression] {
  override val key: String = "ml.core.regression.RandomForestRegression"

  override def serialize(obj: RandomForestRegression, bundle: Bundle): Unit = {
    val meta = bundle.contentWriter("meta")
    randomForestMetaDataSerialize.serialize(RandomForestRegressionMetaData(obj.treeWeights.size,
      obj.treeWeights), meta)
    meta.close()

    obj.trees.zipWithIndex.foreach {
      case (tree, index) =>
        val treeBundle = bundle.createBundle(s"decisionTree_$index")
        decisionTreeRegressionSerialize.serialize(tree, treeBundle)
    }
  }

  override def deserialize(bundle: Bundle): RandomForestRegression = {
    val meta = bundle.contentReader("meta")
    val metaData = randomForestMetaDataSerialize.deserialize(meta)
    meta.close()

    val trees = metaData.treeWeights.indices.map {
      index =>
        val treeBundle = bundle.getBundle(s"decisionTree_$index")
        decisionTreeRegressionSerialize.deserialize(treeBundle)
    }

    RandomForestRegression(trees, metaData.treeWeights)
  }
}
