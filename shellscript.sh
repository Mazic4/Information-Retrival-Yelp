#!/bin/bash

processing_path='./Test'
result_path='./Result'
source_path='./Source'
file_num=$1
res_name='testmodel'$file_num

echo start building model of No. $file_num chunk

java -cp HLTA.jar:HLTA-deps.jar tm.text.Convert $processing_path/testcase $source_path 1000 1

java -cp HLTA.jar:HLTA-deps.jar tm.hlta.HLTA $processing_path/testcase.sparse.txt 50 $processing_path/testmodel

java -cp HLTA.jar:HLTA-deps.jar tm.hlta.ExtractTopicTree $processing_path/$res_name $processing_path/testmodel.bif $processing_path/testcase.sparse.txt

mv $processing_path/$res_name.nodes.json $result_path

rm $processing_path/*

rm $source_path/*

echo finish No. $file_num chunk


