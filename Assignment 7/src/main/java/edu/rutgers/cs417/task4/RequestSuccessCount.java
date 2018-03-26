package edu.rutgers.cs417.task4;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;


// Count the percentage of 2XX request.
public class RequestSuccessCount {

    public static void main(String[] args) throws Exception {

        // Job 1: Produce (Method: [Number of success call, number of times invoked])
        JobConf job1 = new JobConf(RequestSuccessCount.class);
        job1.setJobName("RequestSuccessCount");

        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(IntWritable.class);

        job1.setMapperClass(RequestSuccessCountMapper.class);
        job1.setCombinerClass(RequestSuccessCountReducer.class);
        job1.setReducerClass(RequestSuccessCountReducer.class);

        job1.setInputFormat(TextInputFormat.class);
        job1.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(job1, new Path(args[0]));
        FileOutputFormat.setOutputPath(job1, new Path(args[1]));

        JobClient.runJob(job1);
/*
        // Job 2: Produce (Method: SuccessRate)

        JobConf job2 = new JobConf(RequestSuccessCount.class);
        job2.setJobName("RequestSuccessCount");

        job2.setOutputKeyClass(IntWritable.class);
        job2.setOutputValueClass(IntWritable.class);

        job2.setMapperClass(RequestCountMapper.class);
        job2.setCombinerClass(RequestCountReducer.class);
        job2.setReducerClass(RequestCountReducer.class);

        job2.setInputFormat(TextInputFormat.class);
        job2.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(job2, new Path(args[1]));
        FileOutputFormat.setOutputPath(job2, new Path(args[2]));

        JobClient.runJob(job2);
*/
    }
}