#!/bin/bash
#find_jaccard_match_mbr.java

rm *.class
rm find_jaccard_match_mbr.jar
rm jaccard_match_mbrs.txt

javac find_jaccard_match_mbr.java

jar -cvf find_jaccard_match_mbr.jar *.class

java -classpath find_jaccard_match_mbr.jar find_jaccard_match_mbr

rm *.class
rm *.jar


echo "  *********  [ALL ok, result in jaccard_match_mbrs.txt]  *********  "

cat jaccard_match_mbrs.txt