#!/bin/bash
#~/SATO/scripts/
if [ $# != 2 ] ; then 
echo "wrong parameters..." 
exit 1; 
fi

file1=$1
file2=$2
#echo ${file1}","${file1}
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
cd ~/task2/
hadoop fs -get /user/jchen/task2/t2_join_res
echo "  *********  [get join_res ok]  *********  "