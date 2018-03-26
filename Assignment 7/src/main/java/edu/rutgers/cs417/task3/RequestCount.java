package edu.rutgers.cs417.task3;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.*;

public class RequestCount {

    public static void main(String[] args) throws Exception {

        JobConf conf = new JobConf(RequestCount.class);
        conf.setJobName("RequestCount");

        conf.setOutputKeyClass(IntWritable.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.setMapperClass(RequestCountMapper.class);
        conf.setCombinerClass(RequestCountReducer.class);
        conf.setReducerClass(RequestCountReducer.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        JobClient.runJob(conf);
    }
}