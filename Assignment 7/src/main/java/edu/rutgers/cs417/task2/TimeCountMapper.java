package edu.rutgers.cs417.task2;

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
import java.util.StringTokenizer;

public class TimeCountMapper extends MapReduceBase implements
        Mapper<LongWritable, Text, Text, IntWritable> {
    // returns true if the text is of format XX:XX:XX
    private static boolean isTime(String text) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {
            format.parse(text);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    public void map(LongWritable key, Text value,
                    OutputCollector<Text, IntWritable> output, Reporter reporter)
            throws IOException {
        String line = value.toString();
        StringTokenizer tokenizer = new StringTokenizer(line);
        while (tokenizer.hasMoreTokens()) {
            word.set(tokenizer.nextToken());
            if (isTime(word.toString())) {
                output.collect(word, one);
            }
        }
    }
}