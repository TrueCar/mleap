package org.apache.spark.ml.mleap.runtime

import com.truecar.mleap.core.classification.DecisionTreeClassification
import com.truecar.mleap.core.classification
import com.truecar.mleap.spark.MleapSparkSupport._
import org.apache.spark.ml.classification.DecisionTreeClassificationModel

/**
  * Created by hwilkins on 11/18/15.
  */
case class DecisionTreeClassificationModelToMleap(tree: DecisionTreeClassificationModel) {
  def toMleap: DecisionTreeClassification = {
    classification.DecisionTreeClassification(tree.rootNode.toMleap(true),
      tree.numFeatures,
      tree.numClasses)
  }
}
