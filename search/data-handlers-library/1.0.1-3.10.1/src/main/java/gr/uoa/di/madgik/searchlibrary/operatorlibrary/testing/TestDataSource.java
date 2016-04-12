package gr.uoa.di.madgik.searchlibrary.operatorlibrary.testing;

import gr.uoa.di.madgik.commons.server.ConnectionManagerConfig;
import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.UnaryOp;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.datasource.DataSourceOp;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.extjdbc.QueryParser;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.grs2.GRS2Aggregator;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.grs2.GRS2Splitter;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.select.SelectOp;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.transform.ScriptOp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;

public class TestDataSource {

	public static void main(String[] args) throws Exception {
		TCPConnectionManager.Init(new ConnectionManagerConfig("localhost", new ArrayList<PortRange>(), true));
		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
		ScopeProvider.instance.set("/gcube/devNext");
		
		URI uri = testJDBCSource();
		
//		Map<String, String> inputParameters = new HashMap<String, String>();
//		inputParameters.put("delimiter", Character.toString((char) 2));
//		inputParameters.put("filterMask", "[0,1]");
//		uri = new GRS2Splitter(uri, null, new StatsContainer()).compute();
//		uri1 = new SelectOp(uri1, "[mimeType] <= [id]", new StatsContainer()).compute();
//		uri1 = new SortOp(uri1, new StatsContainer()).compute("id", CompareTokens.DESCENDING_ORDER);
		
//		URI uri2 = testJDBCSource();
//		uri2 = new SelectOp(uri2, "[mimeType] == 'image/jpeg'", new StatsContainer()).compute();
//		uri2 = new SortOp(uri2, new StatsContainer()).compute("id", CompareTokens.DESCENDING_ORDER);
		
		
//		uri = new MergeOp(new URI[]{uri2, uri1}, new StatsContainer()).compute();

//		uri = new SelectOp(uri, "[0] > 'ITIS:710176'", "[0, 1]", new StatsContainer()).compute();

		
//		Map<String, String> operatorParameters = new HashMap<String, String>();
//		operatorParameters.put("filterMask", "[3, 2]");
//		operatorParameters.put("logicalExpressions", "[0] > 'ITIS:710220'");
//		uri = new UnaryOp(SelectOp.class.getName(), uri, operatorParameters , new StatsContainer()).compute();
		
		Map<String, String> operatorParameters = new HashMap<String, String>();
		operatorParameters.put("script", "python " + "/home/jgerbe/Desktop/script.py");
		operatorParameters.put("schema", "[a, b , c]");
		uri = new UnaryOp(ScriptOp.class.getName(), uri, operatorParameters , new StatsContainer()).compute();
		
		Thread.sleep(2000);
		GRS2Printer.print(uri, 6);
		
//		JoinOp join = ;
//		join.producerDefinitionMap;
//		URI uri = join.compute("id", "id");
		
//		
//		BooleanOperator bool = new BooleanOperator();
//		bool.compareMe(new URI[]{uri}, "<Eq><Token>12</Token><Token>12</Token></Eq>", new StatsContainer());
		
		GRS2Printer.compute();
	}

	public static URI testPathSource() throws Exception {
		String inputType = "PATH";
		String inputValue = "/home/jgerbe/Desktop/input.dat.10";
		Map<String, String> inputParameters;
		inputParameters = new HashMap<String, String>();
		inputParameters.put("filterMask", "[1, 0, 2]");
		inputParameters.put("delimiter", "\t");
		
		DataSourceOp ds = new DataSourceOp(inputType, inputValue, inputParameters);
		URI uri = ds.compute();

		return uri;
	}

	public static URI testFTPSource() throws Exception {
		String inputType = "FTP";
		String inputValue = "ftp://giannis:aplagiaftp@meteora.di.uoa.gr/testArea/src";

		Map<String, String> inputParameters = new HashMap<String, String>();
//		inputParameters.put("filterMask", "[2,0, 1]");

		DataSourceOp ds = new DataSourceOp(inputType, inputValue, inputParameters);
		URI uri = ds.compute();

		return uri;
	}

	public static URI testTMSource() throws Exception {
		String inputType = "TM";
		String inputValue = "d686c177-8d4c-4947-b85a-f6e8ef65620e";
		Map<String, String> inputParameters;
		inputParameters = new HashMap<String, String>();
		inputParameters.put("GCubeActionScope", "/gcube/devNext");
//		inputParameters.put("filterMask", "[0,1,2,3]");

		DataSourceOp ds = new DataSourceOp(inputType, inputValue, inputParameters);
		URI uri = ds.compute();

		return uri;
	}
	
	public static URI testJDBCSource() throws Exception {
		String queryString = new String("jdbc:postgresql://meteora.di.uoa.gr:5432/dellstore2?" +
				 "user=postgres&password=aplagiadb" +
				 "/SELECT+*+FROM+spdtrees"
		);

		String inputType = "JDBC";
		String inputValue = queryString;
		Map<String, String> inputParameters;
		inputParameters = new HashMap<String, String>();
//		inputParameters.put("filterMask", "[0,1]");

		DataSourceOp ds = new DataSourceOp(inputType, inputValue, inputParameters);
		URI uri = ds.compute();

		return uri;

	}

	public static String inputStreamToString(InputStream inputStream) throws IOException {
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
