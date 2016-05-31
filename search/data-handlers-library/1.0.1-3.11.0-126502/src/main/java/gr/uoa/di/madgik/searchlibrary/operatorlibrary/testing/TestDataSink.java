package gr.uoa.di.madgik.searchlibrary.operatorlibrary.testing;

import gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasink.DataSinkOp;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.extjdbc.QueryParser;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.grs2.GRS2Splitter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class TestDataSink {
	public static void main(String[] args) throws Exception {

		URI uri = TestDataSource.testPathSource();
		
//		Thread.sleep(5000);
		
//		uri = new URI(testgrs2Sink(uri));
		
//		testLocalSink(uri);
		uri = new GRS2Splitter(uri, null, new StatsContainer()).compute();
		
		Thread.sleep(1000);
		GRS2Printer.print(uri, 6);
		GRS2Printer.compute();
//		System.out.println(testLocalSink(uri));
	}

	public static String testLocalSink(URI uri) throws Exception {
		String outputType = "PATH";
		String outputValue = "/home/jgerbe/Desktop/input.dat.10";
		new File(outputValue).delete();
		Map<String, String> outputParameters = null;
		StatsContainer stats = new StatsContainer();

		DataSinkOp ds = new DataSinkOp(uri, outputType, outputValue, outputParameters, stats);

		return ds.compute();
	}

	private static String testFTPSink(URI uri) throws Exception {
		String outputType = "FTP";
		String outputValue = "meteora.di.uoa.gr";
		Map<String, String> outputParameters = new HashMap<String, String>();
		outputParameters.put("username", "giannis");
		outputParameters.put("password", "aplagiaftp");
		outputParameters.put("directory", "testArea/dest");
		// outputParameters.put("port", "21");
		StatsContainer stats = new StatsContainer();

		DataSinkOp ds = new DataSinkOp(uri, outputType, outputValue, outputParameters, stats);

		return ds.compute();
	}

	public static String testJDBCSink(URI uri) throws Exception {
		String queryString = new String(
				 "<q>" +
				 "<query>" +
				 "insert into trees values (?, ?, ?)" +
				 "</query>" +
				 "<driverName>" +
				 "org.postgresql.Driver" +
				 "</driverName>" +
				 "<connectionString>" +
				 "jdbc:postgresql://localhost:5432/dellstore2?" +
				 "user=postgres&amp;password=aplagiadb" +
				 "</connectionString>" +
				 "</q>"
		);

		QueryParser queryParser = new QueryParser(queryString);
		
		System.out.println("driver: " + queryParser.getDriverName());
		System.out.println("query: " + queryParser.getQuery());
		System.out.println("connection url: " + queryParser.getConnectionString());
		
		String outputType = "JDBC";
		String outputValue = queryString;
		Map<String, String> outputParameters;
		outputParameters = new HashMap<String, String>();
		StatsContainer stats = new StatsContainer();

		DataSinkOp ds = new DataSinkOp(uri, outputType, outputValue, outputParameters, stats);
		
		return ds.compute();
	}
	
	public static String testgrs2Sink(URI uri) throws Exception {
		String outputType = "GRS2";
//		String outputValue = "/home/jgerbe/testArea/dest/out.txt";
//		new File(outputValue).delete();
		Map<String, String> outputParameters = new HashMap<String, String>();
		outputParameters.put("delimiter", Character.toString((char) 2));
		StatsContainer stats = new StatsContainer();

		DataSinkOp ds = new DataSinkOp(uri, outputType, "", outputParameters, stats);

		return ds.compute();
	}

	
	private static String inputStreamToString(InputStream inputStream) throws IOException {
		InputStream in = inputStream;
		InputStreamReader is = new InputStreamReader(in);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read = br.readLine();

		while (read != null) {
			sb.append(read);
			read = br.readLine();
		}

		return sb.toString();
	}
	
	
}
