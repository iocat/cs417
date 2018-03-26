package edu.rutgers.cs417.task4;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.text.ParseException;
import java.util.StringTokenizer;



public class RequestSuccessCountMapper extends MapReduceBase implements
        Mapper<LongWritable, Text, Text, IntWritable> {
    Text method = new Text();
    IntWritable status = new IntWritable();
    public void map(LongWritable lineIndex, Text line,
                    OutputCollector<Text, IntWritable> output, Reporter reporter)
            throws IOException {
        StringTokenizer tokenizer = new StringTokenizer(line.toString());
        while(tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken();
            if(token.equals("GET") || token.equals("POST") || token.equals("PUT") || token.equals("HEAD") || token
                    .equals("OPTIONS")){
                this.method.set(token);
            }else {
                try{
                    int code = Integer.parseInt(token);
                    if (100 <=code && code < 600){
                        this.status.set(code);
                    }
                }catch (NumberFormatException e){
                    continue;
                }
            }
        }

        if(this.status.get() != 0 && this.method.getLength() != 0){
            output.collect(this.method, this.status);
        }


    }

}
