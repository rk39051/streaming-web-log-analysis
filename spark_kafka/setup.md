#SET UP HADOOP CLUSTER

#static ip setup
Go to  /etc/netplan/ 
#default

network:
  version: 2
  renderer: NetworkManager

Change it to
# Let NetworkManager manage all devices on this system
network:
  version: 2
  renderer: NetworkManager
  ethernets:
    ens33:
      dhcp4: no
      addresses: [192.168.5.139/24]
      gateway4: 192.168.5.139
      nameservers:
        addresses: [8.8.4.4,8.8.8.8]



1. Install Java
a)download jdk 1.8 tar file from https://www.oracle.com/technetwork/java/javase/downloads/index.html
extract it and rename extracted folder to java.

b)update .bashrc file
#JAVA ENVIORENMENT
export JAVA_HOME=/usr/local/java
export PATH=$PATH:$JAVA_HOME/bin

2.SET UP HADOOP MULTINODE CLUSTER


a.Map the nodes
192.168.5.137 hadoop-master
192.168.5.138 hadoop-slave-1
192.168.5.139 hadoop-slave-2

b. Setup ssh in every node

$ ssh-keygen -t rsa 
$ ssh-copy-id -i ~/.ssh/id_rsa.pub rupesh@hadoop-master 
$ ssh-copy-id -i ~/.ssh/id_rsa.pub rupesh@hadoop-slave-1 
$ ssh-copy-id -i ~/.ssh/id_rsa.pub rupesh@hadoop-slave-2 
$ chmod 0600 ~/.ssh/authorized_keys 
$ exit

c)download tar file from https://hadoop.apache.org/releases.html 
extract it and rename extracted folder to hadoop.

d. update .bashrc file
#HADOOP ENVIORENMNT
export HADOOP_HOME=/usr/local/hadoop
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
export HADOOP_HDFS_HOME=$HADOOP_HOME
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export HADOOP_YARN_HOME=$HADOOP_HOME
export PATH=$PATH:$HADOOP_HOME/bin
export PATH=$PATH:$HADOOP_HOME/sbin
   
#HADOOP NATIVE LIBRARY
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
export HADOOP_OPTS="-Djava.library.path=$HADOOP_HOME/lib/native"


d.Configure Hadoop

#core-site.xml
Open the core-site.xml file and edit it as shown below.

<configuration>
   <property> 
      <name>fs.default.name</name> 
      <value>hdfs://hadoop-master:9000/</value> 
   </property> 
<property>
        <name>hadoop.tmp.dir</name>
        <value>/home/rupesh/hadoop/hdata</value>
    </property>
</configuration>
<property>
        <name>dfs.permission</name>
        <value>false</value>
    </property>


#hdfs-site.xml 
Edit configuration file hdfs-site.xml (located in HADOOP_HOME/etc/hadoop) and add following entries:
<configuration>
<property>
<name>dfs.replication</name>
<value>2</value>
</property>
<property>
    <name>dfs.datanode.data.dir</name>
    <value>/home/rupesh/hadoop/datanode</value>
</property>
<property>
    <name>dfs.namenode.name.dir</name>
    <value>/home/rupesh/hadoop/namenode</value>
</property>

</configuration>

#hadoop-env.sh
Edit configuration file hadoop-env.sh (located in HADOOP_HOME/etc/hadoop) and set JAVA_HOME:
export JAVA_HOME=/usr/local/java

# mapred-site.xml on master only
Edit configuration file mapred-site.xml (located in HADOOP_HOME/etc/hadoop) and add following entries:
<configuration>
<property>
<name>mapreduce.framework.name</name>
<value>yarn</value>
</property>
<property>
<name>yarn.app.mapreduce.am.env</name>
<value>HADOOP_MAPRED_HOME=/usr/local/hadoop</value>
</property>
<property>
<name>mapreduce.map.env</name>
<value>HADOOP_MAPRED_HOME=/usr/local/hadoop</value>
</property>
<property>
<name>mapreduce.reduce.env</name>
<value>HADOOP_MAPRED_HOME=/usr/local/hadoop</value>
</property>
</configuration>

#yarn-site.xml
configuration>

<!-- Site specific YARN configuration properties -->
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.nodemanager.env-whitelist</name>
        <value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,CLASSPATH_PREPEND_DISTCACHE,HADOOP_YARN_HOME,HADOOP_MAPRED_HOME                                </value>                                                                                                                                   </property>
</configuration>

3.Setup Kafka Cluster
a)download tar file from https://kafka.apache.org/downloads extract it and rename extracted folder to kafka.

Now, follow several steps to set up Kafka Cluster:

b) Make a folder of name “logs”. In this folder, all the Kafka logs will be stored.

c) Then, open the server.properties file, on going to the config directory. Here, we will find the file, which contains Kafka broker configurations.
Further, set broker.id to 1. Make sure it is the id of the broker in a Kafka Cluster, so for each broker, it must be unique.

d) Then, uncomment the listener’s configuration and also set it to PLAINTEXT://localhost:9091. It says, for connection requests, the Kafka broker will be listening on port 9091.
Moreover, with the logs folder path, set the log.dirs configuration that we created in step 1.

e) Also, set the Apache Zookeeper address, in the zookeeper.connect configuration. However, if Zookeeper is running in a Kafka cluster, then ensure to give the address as a comma-separated list, i.e.:localhost:2181, localhost:2182.

Basically, these are some general configurations that we need to be set up for the development environment.
In this way, our first Kafka broker configuration is ready. Now, follow the same steps with the following changes, for the other two folders or brokers.

f)update .bashrc file
#KAFKA ENVIORENMENT
export KAFKA_HOME=/usr/local/kafka
export PATH=$PATH:$KAFKA_HOME/bin
export PATH=$PATH:$KAFKA_HOME/config

* properties to be added in server.properties
log.dirs=/usr/local/kafka/logs/
num.partitions=3
offsets.topic.replication.factor=2
default.replication.factor=2
min.insync.replicas=2

* properties to be added in zookeeper.properties
dataDir=/home/rupesh/zookeeper/logs/

4.Install Spark
a)download tar file from https://spark.apache.org/downloads.html extract it and rename extracted folder to spark.

b)Edit spark-env.sh
export JAVA_HOME=/usr/local/java

c)Add Salves
Create configuration file slaves (in $SPARK_HOME/conf/) and add following entries in workers directory:

hadoop-slave-1
hadoop-slave-2

d)Install Spark On Slaves
Copy setups from master to all the slaves

e)update .bashrc file
#SPARK ENVIORENMENT
export SPARK_HOME=/usr/local/spark
export PATH=$PATH:$SPARK_HOME/bin
export PATH=$PATH:$SPARK_HOME/conf
export PATH=$PATH:$SPARK_HOME/sbin

5.set up flume on hadoop-master node

a)download tar file from https://flume.apache.org/download.html extract it and rename extracted folder to flume.

b)update .bashrc file
#FLUME ENVIORENMENT
export FLUME_HOME=/usr/local/flume
export PATH=$PATH:$FLUME_HOME/bin
export PATH=$PATH:$FLUME_HOME/conf

6.set up log generator 
a)visit https://github.com/dgadiraju/gen_logs and folllow instruction.
b)update .bashrc file
#GEN_LOGS ENVIORENMENT
export GEN_LOGS=/opt/gen_logs
export PATH=$PATH:$GEN_LOGS
