package edu.rutgers.cs417.task4;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.Iterator;

public class RequestSuccessCountReducer extends MapReduceBase implements Reducer<Text,IntWritable,Text, IntWritable > {
    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter
            reporter)throws IOException{

        int success = 0;
        while(values.hasNext()){
            int status = values.next().get();
            if ( 200 <= status && status <= 299){
                success ++;
            }
        }
        output.collect(key, new IntWritable(success));
    }

}
