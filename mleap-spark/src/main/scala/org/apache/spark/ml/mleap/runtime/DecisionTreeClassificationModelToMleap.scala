package org.apache.spark.ml.mleap.runtime

import com.truecar.mleap.core.classifier.DecisionTreeClassification
import com.truecar.mleap.core.classifier
import com.truecar.mleap.spark.MleapSparkSupport._
import org.apache.spark.ml.classification.DecisionTreeClassificationModel

/**
  * Created by hwilkins on 11/18/15.
  */
case class DecisionTreeClassificationModelToMleap(tree: DecisionTreeClassificationModel) {
  def toMleap: DecisionTreeClassification = {
    classifier.DecisionTreeClassification(tree.rootNode.toMleap,
      tree.numFeatures,
      tree.numClasses)
  }
}
