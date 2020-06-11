package com.flafka

import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger

object SparkKafkaStructStrm {
  def main(args:Array[String]){
   Logger.getLogger("org").setLevel(Level.ERROR);
   val spark=new SparkSession.Builder().appName("flafka").master(args(0)).getOrCreate();
   import spark.implicits._
   
   val strm_df=spark.readStream.format("kafka")
   .option("kafka.bootstrap.servers", "localhost:9092")
   .option("subscribe", "logdata")
   .load()
   .selectExpr("cast(value as string)","timestamp");
   
   val strm_df1=strm_df.where(split(split($"value"," ")(6), "/")(1)==="department")
   .select(split(split($"value"," ")(6), "/")(2).alias("department"),$"timestamp");
   
   
   val strm_df2=strm_df1.groupBy(window($"timestamp","20 seconds","20 seconds"),$"department").agg(count($"department") as "total");
   
   
   
   val query=strm_df2.writeStream.format("console").outputMode("update")
   .trigger(Trigger.ProcessingTime("20 seconds")).start();
   query.awaitTermination();   
   }

     
}