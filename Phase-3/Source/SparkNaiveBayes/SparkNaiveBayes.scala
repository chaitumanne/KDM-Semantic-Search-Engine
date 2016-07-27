/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mlpipeline

import ontInterface.OwlMovie
import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature._
import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}
import java.io.File

import scala.collection.immutable.HashMap
import scala.tools.nsc.Global

object SparkNaiveBayes {
  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }


  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "C:\\winutils")

    val trainFolder = "data/Categories/*"
    val conf = new SparkConf().setAppName(s"NBExample").setMaster("local[*]").set("spark.driver.memory", "5g").set("spark.executor.memory", "5g")
    val sc = new SparkContext(conf)

    Logger.getRootLogger.setLevel(Level.WARN)


    // Load documents, and prepare them for NB.
    val preprocessStart = System.nanoTime()
    val (input, corpus) =
      preprocess(sc, trainFolder)


    input.collect.foreach(f => {
      val location_array = f._1.split("/")
      val class_name = location_array(location_array.length - 1)
    })

    var hm = new HashMap[String, Int]()
    val path ="data/categories"
    val files = getListOfFiles(path)
    var CATEGORIES = List.empty[String]
    files.foreach(f =>{CATEGORIES :+= f.getName.substring(0, f.getName.length - 4) })
    var index = 0
    CATEGORIES.foreach(f => {
      hm += CATEGORIES(index) -> index
      index += 1
    })
    val mapping = sc.broadcast(hm)
    val data = input.zip(corpus)
    val featureVector = data.map(f => {
      val location_array = f._1._1.split("/")
      val class_name = location_array(location_array.length - 1).substring(0,location_array(location_array.length - 1).length-4)

      new LabeledPoint(hm.get(class_name).get.toDouble, f._2)
    })

    val model = NaiveBayes.train(featureVector, lambda = 3.0, modelType = "multinomial")

    val testDir = "data/test/*"

    val topicData = SparkLDAMain.main(sc, testDir, 5, "em")

    val testFV = getTFIDFVector(sc, topicData)

    val result = model.predict(testFV)

    //result.foreach(f => println(f))
    result.foreach(f => println(f))


    //Ontology creation
    val owl = new OwlMovie()

    CATEGORIES.foreach(f => {
      owl.createClass(f)
    })

    input.collect.foreach(f => {
      val location_array = f._1.split("/")
      //println(f)
      val class_name = location_array(location_array.length - 1)
      val nclass_name =class_name.substring(0,class_name.length - 4)
      //println(class_name)
      val array = f._2.split(" ")
      array.foreach(ff => {
        //println(class_name,ff)
        owl.createIndividual(":" + ff, nclass_name)
      })

    })

    owl.createClass(":Topic")



    val resultData = topicData.zip(result.collect())
    var i = 1
    resultData.map(f => {
      owl.createIndividual(":Topic" + i, ":Topic")
      println(f._2)
      val inter =f._2.toInt
      var className = CATEGORIES(inter)
      println(className)
      val array = f._1.split(" ")
      array.foreach(ff => {
        owl.createIndividual(":" + ff, className)
        owl.createObjectProperty(":" + ff, "hasTopic", ":Topic" + i)
      })
      i = i + 1

    })

    owl.saveOntology()

    sc.stop()
  }

  private def preprocess(sc: SparkContext,
                         paths: String): (RDD[(String, String)], RDD[Vector]) = {

    val sqlContext = SQLContext.getOrCreate(sc)
    import sqlContext.implicits._
    val df = sc.wholeTextFiles(paths).map(f => {
      var ff = f._2.replaceAll("[^a-zA-Z\\s:]", " ")
      ff = ff.replaceAll(":", "")
      // println(ff)
      (f._1, CoreNLP.returnLemma(ff))
    }).toDF("location", "docs")


    val tokenizer = new RegexTokenizer()
      .setInputCol("docs")
      .setOutputCol("rawTokens")
    val stopWordsRemover = new StopWordsRemover()
      .setInputCol("rawTokens")
      .setOutputCol("tokens")

    val tf = new org.apache.spark.ml.feature.HashingTF()
      .setInputCol("tokens")
      .setOutputCol("features")
    val idf = new org.apache.spark.ml.feature.IDF()
      .setInputCol("features")
      .setOutputCol("idfFeatures")

    val pipeline = new Pipeline()
      .setStages(Array(tokenizer, stopWordsRemover, tf, idf))

    val model = pipeline.fit(df)
    val documents = model.transform(df)
      .select("idfFeatures")
      .rdd
      .map { case Row(features: Vector) => features }

    val input = model.transform(df).select("location", "docs").rdd.map { case Row(location: String, docs: String) => (location, docs) }
    println(model.transform(df).printSchema())
    (input, documents)
  }

  def getTFIDFVector(sc: SparkContext, input: Array[String]): RDD[Vector] = {

    val sqlContext = SQLContext.getOrCreate(sc)
    import sqlContext.implicits._
    val df = sc.parallelize(input.toSeq).toDF("docs")


    val tokenizer = new RegexTokenizer()
      .setInputCol("docs")
      .setOutputCol("rawTokens")
    val stopWordsRemover = new StopWordsRemover()
      .setInputCol("rawTokens")
      .setOutputCol("tokens")

    val tf = new org.apache.spark.ml.feature.HashingTF()
      .setInputCol("tokens")
      .setOutputCol("features")
    val idf = new org.apache.spark.ml.feature.IDF()
      .setInputCol("features")
      .setOutputCol("idfFeatures")

    val pipeline = new Pipeline()
      .setStages(Array(tokenizer, stopWordsRemover, tf, idf))

    val model = pipeline.fit(df)
    val documents = model.transform(df)
      .select("idfFeatures")
      .rdd
      .map { case Row(features: Vector) => features }

    documents
  }
}
