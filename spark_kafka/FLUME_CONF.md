#define agent
kafka.sources=k1
kafka.channels=mem
kafka.sinks=ks

#define source
kafka.sources.k1.type=exec
kafka.sources.k1.command= tail -f /opt/gen_logs/logs/access.log

#define sink
kafka.sinks.ks.type=org.apache.flume.sink.kafka.KafkaSink
kafka.sinks.ks.brokerList=hadoop-master:9092,hadoop-slave-1:9092
kafka.sinks.ks.topic=web-log

#define channel
kafka.channels.mem.type=memory
kafka.channels.mem.capacity=1000
kafka.channels.mem.transactionCapacity=100

#bind sources and sink with channel
kafka.sources.k1.channels=mem
kafka.sinks.ks.channel=mem