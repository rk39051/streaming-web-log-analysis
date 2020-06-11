#start zookeeper server
zookeeper-server-start.sh config/zookeeper.properties

#start kafka server
kafka-server-start.sh config/server.properties

#create topic
kafka-topics.sh --create --bootstrap-server hadoop-master:9092,hadoop-slave-1:9092 --replication-factor 2 --partitions 3 --topic web-log

#list the topics
kafka-topics.sh --list --bootstrap-server hadoop-master:9092,hadoop-slave-1:9092 

#describe the topics
kafka-topics.sh --describe --bootstrap-server hadoop-master:9092,hadoop-slave-1:9092

#run flume agent kafka
flume-ng agent --conf /home/rupesh/Documents/flume --conf-file /home/rupesh/Documents/flume/flumekafka.conf --name kafka

#verify the data
kafka-console-consumer.sh --bootstrap-server hadoop-master:9092,hadoop-slave-1:9092 --topic web-log --from-beginning

#start log generator
start_log.sh

#develop your own fat jar and run that jar using spark submit
spark-submit --class com.flafka.Error404 --master local /home/rupesh/Documents/spark_kafka-0.0.1-SNAPSHOT.jar local[*]

spark-submit --class com.flafka.WebTrafficAnalysis --master local  /home/rupesh/Documents/spark_kafka-0.0.1-SNAPSHOT.jar local[*]
