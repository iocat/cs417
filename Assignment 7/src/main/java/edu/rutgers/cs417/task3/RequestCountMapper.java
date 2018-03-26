package edu.rutgers.cs417.task3;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class RequestCountMapper extends MapReduceBase implements
        Mapper<LongWritable, Text, IntWritable, IntWritable> {
    // returns true if the text is of format XX:XX:XX
    private static int getHour(String text) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date = format.parse(text);
        return date.getHours();
    }

    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    public void map(LongWritable key, Text value,
                    OutputCollector<IntWritable, IntWritable> output, Reporter reporter)
            throws IOException {
        String line = value.toString();
        StringTokenizer tokenizer = new StringTokenizer(line);
        while (tokenizer.hasMoreTokens()) {
            word.set(tokenizer.nextToken());
            try{
                int h = getHour(word.toString());
                output.collect(new IntWritable(h),one);
            }catch (ParseException e){
                continue;
            }
        }
    }
}