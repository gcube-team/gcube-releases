package gr.cite.geoanalytics.functions.experiments.tests;

import org.apache.spark.api.java.*;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.function.Function;

public class SimpleApp {

	public static void main(String[] args) {
		
		SparkConf conf = new SparkConf()
				  .setMaster("spark://datanode3.cluster2.madgik.di.uoa.gr:7077")
				  .setJars(new String[] {"D:\\Workspace\\Projects\\code-bluebridge\\geospatial-platform\\trunk\\geoanalytics-functions\\target\\geoanalytics-functions-1.0.0-SNAPSHOT.jar"})
				  .setAppName("My-spark-app");
		SparkContext sc = new SparkContext(conf);
		
//		String logFile = "/tmp/file-sample";
		String logFile = "hdfs://datanode1.cluster2.madgik.di.uoa.gr:50050/OriginOfSpecies.txt";
		
		JavaRDD<String> logData = sc.textFile(logFile, 1).toJavaRDD();

//		System.out.println(logData.count());
		
		long numAs = logData.filter(new Function<String, Boolean>() {
			public Boolean call(String s) {
				return s.contains("a");
			}
		}).count();
		
		long numBs = logData.filter(new Function<String, Boolean>() {
			public Boolean call(String s) {
				return s.contains("b");
			}
		}).count();

		System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);
	}
}
