package gr.uoa.di.madgik.searchlibrary.operatorlibrary.testing;

import gr.uoa.di.madgik.commons.server.ConnectionManagerConfig;
import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.gmerge.GradualMergeOp;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.partition.PartitionOp;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.stats.StatsContainer;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class TestPartition extends Thread {

	public static void main(String[] args) throws Exception {
		TCPConnectionManager.Init(new ConnectionManagerConfig("localhost", new ArrayList<PortRange>(), true));
		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
		for (int i = 0; i < 1000; i++) {
			test(args);
		}
	}
	public static void test(String[] args) throws Exception {
		try {
			URI uri;

			uri = TestDataSource.testJDBCSource();

			// uri = new MergeOp(new URI[]{TestDataSource.testFTPSource(),
			// TestDataSource.testFTPSource()}, new StatsContainer()).compute();

			HashMap<String, String> pars = new HashMap<String, String>();
			pars.put("partitionField", "0");
			uri = new PartitionOp(uri, pars, new StatsContainer()).compute();
			// ////
			
			uri = new GradualMergeOp(uri, new HashMap<String, String>(), new StatsContainer()).compute();
			//
			GRS2Printer.print(uri, 6);
			// Thread.sleep(2000);

			GRS2Printer.compute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
