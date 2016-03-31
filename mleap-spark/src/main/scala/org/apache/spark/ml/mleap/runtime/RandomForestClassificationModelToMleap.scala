package org.apache.spark.ml.mleap.runtime

import com.truecar.mleap.core.classification.RandomForestClassification
import com.truecar.mleap.spark.MleapSparkSupport._
import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.classification.{DecisionTreeClassificationModel, RandomForestClassificationModel}

/**
  * Created by hwilkins on 12/18/15.
  */
object RandomForestClassificationModelToMleap extends TransformerToMleap[RandomForestClassificationModel] {
  override def toMleap(t: RandomForestClassificationModel): transformer.RandomForestClassificationModel = {
    val model = RandomForestClassification(t.trees.asInstanceOf[Array[DecisionTreeClassificationModel]].map(_.toMleap),
      t.numFeatures,
      t.numClasses)

    transformer.RandomForestClassificationModel(t.getFeaturesCol,
      t.getPredictionCol,
      model)
  }
}
