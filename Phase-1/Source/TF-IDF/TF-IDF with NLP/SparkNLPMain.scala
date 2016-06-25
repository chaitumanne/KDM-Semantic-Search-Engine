
import org.apache.spark.ml.feature.{StopWordsRemover, HashingTF, IDF, Tokenizer}
import org.apache.spark.sql.SparkSession


/**
  * Created by Mayanka on 17-Jun-16.
  */
object SparkNLPMain {

  def main(args: Array[String]) {
    val spark = SparkSession
      .builder
      .appName("TfIdfExample")
      .master("local[*]")
      .getOrCreate()

    // $example on$
    val sentenceData = spark.createDataFrame(Seq(
      (0, "The third movie produced by Howard Hughes, this gem was thought to be lost."),
      (1, "It was recently restored and shown on TCM (12/15/04)."),
      (2, "The plot is a familiar one - two WW I soldiers escape from a German prison camp (guarded by an extremely lethargic German shepherd, who practically guides them out of the camp), stow away on a ship, and end up in \"Arabia\", where they rescue the lovely Mary Astor."),
      (3, "The restoration is very good overall, although there are two or three very rough sequences."),
      (4, "The restoration is very good overall, although there are two or three very rough sequences."),
      (5, "And did I mention that Mary Astor is in it? The film won an Academy Award for the now-defunct category of \"Best Direction of a Comedy\".")

    )).toDF("label", "sentence")

    val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
    val wordsData = tokenizer.transform(sentenceData)

    val remover = new StopWordsRemover()
      .setInputCol("words")
      .setOutputCol("filteredWords")
    val processedWordData= remover.transform(wordsData)

    val hashingTF = new HashingTF()
      .setInputCol("filteredWords").setOutputCol("rawFeatures").setNumFeatures(20)
    val featurizedData = hashingTF.transform(processedWordData)
    // alternatively, CountVectorizer can also be used to get term frequency vectors

    val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
    val idfModel = idf.fit(featurizedData)
    val rescaledData = idfModel.transform(featurizedData)
    rescaledData.select("filteredWords","features", "label").take(6).foreach(println)


    spark.stop()

  }

}
