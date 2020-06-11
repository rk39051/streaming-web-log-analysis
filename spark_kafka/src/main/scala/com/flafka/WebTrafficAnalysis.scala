package com.flafka

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent


object WebTrafficAnalysis {
  def main(args : Array[String]){
    
   // Logger.getLogger("org").setLevel(Level.ERROR)
    val sc=new SparkConf().setAppName("LogFileAnalysis")setMaster(args(0));
    val ssc=new StreamingContext(sc,Seconds(60));
    val kafkaParams = Map[String, Object](
  "bootstrap.servers" -> "hadoop-master:9092,hadoop-slave-1:9092",
  "key.deserializer" -> classOf[StringDeserializer],
  "value.deserializer" -> classOf[StringDeserializer],
  "group.id" -> "0000",
  "auto.offset.reset" -> "earliest",
  "enable.auto.commit" -> (false: java.lang.Boolean)
);

val topics = Array("e-commerce-log");
val stream = KafkaUtils.createDirectStream[String, String](
  ssc,
  PreferConsistent,
  Subscribe[String, String](topics, kafkaParams)
);

val log=stream.map(record => (record.key, record.value));
val log1=log.map(x => x._2);
val log2=log1.filter(x=>(x.split(" ")(6)).split("/")(1)=="department");
val dept=log2.map(x=>((x.split(" ")(6)).split("/")(2),1));
val dept_wise_count_window=dept.reduceByKey(_+_);

dept_wise_count_window.foreachRDD(rdd=>{
     val spark=SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
     import spark.implicits._
     
     val df=rdd.toDF("dept","total");
     //showing output
     df.sort($"total".desc);
     df.show(false);  
     df.repartition(1).write.format("csv").option("header", "true").mode("overwrite").save("file:///home/rupesh/Documents/web_traffic");
}); 


ssc.start();
ssc.awaitTermination();
   
  
}


}