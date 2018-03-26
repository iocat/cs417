__Name: Thanh Ngo__  
__RUID: 168006827__

All results are in the directory named task1, task2 and task3

## Build instruction
In the project's root directory, run 

```bash
    mvn install
```

All jar files are in the ./target directory ready to be executed by the cluster

## Task 1

1. What is the output of this task? What does each file contain?  
  The output of this task is a \_SUCCESS file and a bunch of files of the format part-0000X where X is from 0 to 6. 
  Each file contains the words and the corresponding number of occurrences in the original data.
2. Explain how this map-reduce job works.  
  Each map worker reads through the text data and spits out a pair of (word,1), and the words are separated by a space. 
  The reduce worker will then process the partitions which contain their preserved key pairs. 
  Reducer takes the sum of many 1s for a single key and produce 
  the number of occurrences of each key in the original data.
3. If the input is just a file containing “hello world hello hadoop”, what are the outputs of the Mapper and Reducer?
    1. Mapper:  
        hello 1  
        world 1  
        hello 1  
        hadoop 1  
    2. Reducer:   
        hadoop 1  
        hello 2  
        world 1
        
## Task 2

For this task, I just had to change the key of the previous task. Instead of spitting out words as key, I spit out 
timestamps as keys. For each line of input file, I tokenize the line into words, and then I check if a word is a 
timestamp. If it is, I collect it as key and the value is one. 

The reducer in this task is the same as the one used in WordCount. 

## Task 3

For this task, I also change the key of the previous task's mapper. The key this time is the hour of the timestamp. 
In the mapper, I check each word on the input line. If the word is a timestamp, then I spit out key as the hour part 
of the timestamp, while the value is one. 

The reducer in this task is similar to the last two except the output key data type is IntWritable, not Text.

## Task 4

In this task, I attempted to count the number of success request (http status: 2XX) corresponding to each request 
method (HEAD, POST, GET, PUT, OPTIONS). 

Unfortunately,
I failed as the results somehow don't come out as useful. I wasn't, however, able to examine the actual data input to 
debug the problem. 

In this task, I wrote the mapper to find a method and a status code for the line the mapper runs on. Then if a method
 and a valid status code, I would spit out the (http_method, http_code) as a (key,value) pair.
 
In the reducer, I wrote the reducer such that for each http_method key, I count the number of success http_code 
(which means the code is in the range [200,299]). Then in the end, I spit out the http_method as key and the success 
code counter as value.

Somehow, my results of this map-reduce job are empty files and there's one file with "GET 0" as a result. I haven't 
seen the input files, so I don't know how to deal with this meaningless results.