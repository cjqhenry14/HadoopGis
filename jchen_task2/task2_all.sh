#!/bin/bash
#~/SATO/scripts/
if [ $# != 2 ] ; then 
echo "wrong parameters..." 
exit 1; 
fi

file1=$1
file2=$2
#echo ${file1}","${file1}
#clean previus result files
rm -r t2_join_res
#-----------------------------MR: get spatial join result----------------------------------
hadoop fs -rm -r /user/jchen/task2/*
echo "  *********  [clean ok]  *********  "

hadoop fs -mkdir /user/jchen/task2/input_data1
hadoop fs -mkdir /user/jchen/task2/input_data2

hadoop fs -mkdir /user/jchen/task2/loaded_data1
hadoop fs -mkdir /user/jchen/task2/loaded_data2
echo "  *********  [mkdir ok]  *********  "
#put file1 file2 to hadoop file system
hadoop fs -put ${file1} /user/jchen/task2/input_data1
hadoop fs -put ${file2} /user/jchen/task2/input_data2
echo "  *********  [input ok]  *********  "
#load data
cd ~/SATO/scripts/
./loaddata.sh --data=/user/jchen/task2/input_data1/${file1} --prefix=/user/jchen/task2/loaded_data1 --geomid=2
./loaddata.sh --data=/user/jchen/task2/input_data2/${file2} --prefix=/user/jchen/task2/loaded_data2 --geomid=2
echo "  *********  [load ok]  *********  " 
#run spatial join
#/user/jchen/task2/t2_join_res
./spatialjoin.sh --inputa=/user/jchen/task2/loaded_data1 --inputb=/user/jchen/task2/loaded_data2 --predicate=intersects --destination=/user/jchen/task2/t2_join_res -n 2 -s true -t true
echo "  *********  [join ok]  *********  " 
#get spatial join result
########!!!#############
cd ~/jchen_task2/
####################hadoop fs -get /user/jchen/task2/t2_join_res
echo "  *********  [get join_res ok]  *********  "

#------------------------MR: get sort_by_hilbert result---------------------------------------

#sort_by_hilbert.java
#input:/user/jchen/task2/t2_join_res
#output:/user/jchen/task2/sorted_hilbert_res
rm *.class
rm sort_by_hilbert.jar
rm -r sorted_hilbert_res

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
####!!!!!!!!!!
hadoop fs -get /user/jchen/task2/sorted_hilbert_res
echo "  *********  [get sorted_hilbert_res ok]  *********  "

#------------------------non-MR: get sort_by_hilbert result---------------------------------------
#find_jaccard_match_mbr.java

rm *.class
rm find_jaccard_match_mbr.jar
#clean result file
rm jaccard_match_mbrs.txt

javac find_jaccard_match_mbr.java

jar -cvf find_jaccard_match_mbr.jar *.class

java -classpath find_jaccard_match_mbr.jar find_jaccard_match_mbr

rm *.class
rm *.jar

echo "  *********  [ALL ok, result in jaccard_match_mbrs.txt]  *********  "
cat jaccard_match_mbrs.txt







