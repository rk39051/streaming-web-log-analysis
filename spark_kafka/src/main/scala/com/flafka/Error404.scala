package com.flafka

import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.PreferConsistent
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.StreamingContext
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.Seconds
import org.apache.spark.sql.SparkSession

object Error404 {
  def main(args : Array[String]){
    
   // Logger.getLogger("org").setLevel(Level.ERROR)
    val sc=new SparkConf().setAppName("LogFileAnalysis")setMaster(args(0));
    val ssc=new StreamingContext(sc,Seconds(60));
    val kafkaParams = Map[String, Object](
  "bootstrap.servers"  -> "hadoop-master:9092,hadoop-slave-1:9092",
  "key.deserializer"   -> classOf[StringDeserializer],
  "value.deserializer" -> classOf[StringDeserializer],
  "group.id"           -> "0001",
  "auto.offset.reset"  -> "earliest",
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
val error404=log1.filter(x=>x.split(" ")(8)=="404");
error404.repartition(1).saveAsTextFiles("file:///home/rupesh/Documents/error404", "log")
ssc.start();
ssc.awaitTermination();
}
}