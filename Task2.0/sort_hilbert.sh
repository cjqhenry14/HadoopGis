#!/bin/bash
#sort_by_hilbert.java
#input:/user/jchen/task2/t2_join_res
#output:/user/jchen/task2/sorted_hilbert_res
rm *.class
rm sort_by_hilbert.jar
hadoop fs -rm  /user/jchen/task2/t2_join_res/_SUCCESS
hadoop fs -rm -r /user/jchen/task2/sorted_hilbert_res
#javac compile
javac -classpath $HADOOP_HOME/share/hadoop/common/hadoop-common-2.6.0.jar:$HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-client-core-2.6.0.jar:$HADOOP_HOME/share/hadoop/common/lib/commons-cli-1.2.jar   sort_by_hilbert.java  get_hilbert_curve_val.java
echo "  *********  [javac compile ok]  *********  "

#make jar
jar -cvf sort_by_hilbert.jar *.class
echo "  *********  [make jar ok]  *********  "
#run
hadoop jar sort_by_hilbert.jar sort_by_hilbert
echo "  *********  [run ok]  *********  "
#get sorted_hilbert_res
hadoop fs -get /user/jchen/task2/sorted_hilbert_res
echo "  *********  [get sorted_hilbert_res ok]  *********  "