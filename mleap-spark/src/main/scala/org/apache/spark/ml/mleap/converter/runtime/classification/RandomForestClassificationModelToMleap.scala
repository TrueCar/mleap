package org.apache.spark.ml.mleap.converter.runtime.classification

import com.truecar.mleap.core.classification.RandomForestClassification
import com.truecar.mleap.runtime.transformer
import org.apache.spark.ml.classification.{DecisionTreeClassificationModel, RandomForestClassificationModel}
import org.apache.spark.ml.mleap.converter.runtime.TransformerToMleap

/**
  * Created by hwilkins on 12/18/15.
  */
object RandomForestClassificationModelToMleap extends TransformerToMleap[RandomForestClassificationModel, transformer.RandomForestClassificationModel] {
  override def toMleap(t: RandomForestClassificationModel): transformer.RandomForestClassificationModel = {
    val trees = t.trees.asInstanceOf[Array[DecisionTreeClassificationModel]]
      .map(tree => DecisionTreeClassificationModelToMleap(tree).toMleap)
    val model = RandomForestClassification(trees,
      t.numFeatures,
      t.numClasses)

    transformer.RandomForestClassificationModel(t.getFeaturesCol,
      t.getPredictionCol,
      model)
  }
}
