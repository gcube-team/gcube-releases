package org.gcube.dataanalysis.dataminer.poolmanager.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Host;
import org.gcube.dataanalysis.dataminer.poolmanager.util.CheckPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

public class HAProxy {

	private Logger logger;
	
	private CSVReader reader;

	public HAProxy() {
		this.logger = LoggerFactory.getLogger(HAProxy.class);
	}
	
//	public Cluster getClusterByHProxy() throws IOException {
//		Cluster cl = new Cluster();
//		String HProxy = ISClient.getHProxy();
//		URL stockURL = new URL("http://data.d4science.org/Yk4zSFF6V3JOSytNd3JkRDlnRFpDUUR5TnRJZEw2QjRHbWJQNStIS0N6Yz0");
//		BufferedReader in = new BufferedReader(new InputStreamReader(stockURL.openStream()));
//		reader = new CSVReader(in);
//		String[] nextLine;
//		while ((nextLine = reader.readNext()) != null) {
//			// rules to add
//			if (HProxy.contains(nextLine[0])) {
//				cl.setName(nextLine[0]);
//			}
//		}
//		return cl;
//
//	}

	public Cluster MapCluster() throws IOException {
		Cluster cl = new Cluster();
		String HProxy = ISClient.getHProxy();
		//Prod HAproxy
		if (HProxy.equals("dataminer-cluster1.d4science.org")) {
			cl.setName("dataminer_cluster1");
		}
		if (HProxy.equals("dataminer-bigdata.d4science.org")) {
			cl.setName("bigdata");
		}
		if (HProxy.equals("dataminer-cluster1.d4science.org")) {
			cl.setName("dataminer_cluster1");
		}
		if (HProxy.equals("dataminer-cloud1.d4science.org")) {
			cl.setName("dataminer_cloud1");
		}
		if (HProxy.equals("dataminer-prototypes.d4science.org")) {
			cl.setName("prototypes");
		}
		if (HProxy.equals("dataminer.d4science.org")) {
			cl.setName("gcubeapps");
		}
		if (HProxy.equals("dataminer-genericworkers.d4science.org")) {
			cl.setName("genericworkers");
		}
		if (HProxy.equals("dataminer-genericworkers-proto.d4science.org")) {
			cl.setName("genericworkers_proto");
		}
		//dev HAProxy
		if (HProxy.equals("dataminer-d-workers.d4science.org")||(HProxy.equals("dataminer-d-d4s.d4science.org"))) {
			cl.setName("devnext_backend");
		}	
		//preProd HAProxy
//		if (HProxy.equals("dataminer1-pre.d4science.org")) {
//			cl.setName("dataminer1-pre.d4science.org");
//		}
		return cl;

	}
	
	
	public List<Host> listDataMinersByCluster(String targetVREToken,String targetVRE) throws IOException {
		
		SecurityTokenProvider.instance.set(targetVREToken);
		ScopeProvider.instance.set(targetVRE);

		// next op to use when Cluster info available in the IS
		// Cluster cluster = this.getClusterByHProxy();
		Cluster cluster = this.MapCluster();
		List<Host> out = new LinkedList<Host>();
		Host a = new Host();

		//no proxy dataminer (preprod)
		if (cluster.getName() == null){
			a.setName(ISClient.getHProxy());
			out.add(a);
		}
		
		// if preprod, just one dm available
//		if (cluster.getName().equals("dataminer1-pre.d4science.org")) {
//			a.setName("dataminer1-pre.d4science.org");
//			out.add(a);
		//} 
		else {
			// prod
			//URL stockURL = new
			//URL("http://data.d4science.org/Yk4zSFF6V3JOSytNd3JkRDlnRFpDUUR5TnRJZEw2QjRHbWJQNStIS0N6Yz0");
			URL stockURL = new URL("http://"+ ISClient.getHProxy() +":8880/;csv");
			//URL stockURL = new URL("http://data.d4science.org/c29KTUluTkZnRlB0WXE5NVNaZnRoR0dtYThUSmNTVlhHbWJQNStIS0N6Yz0");
			//System.out.println(stockURL);
			// dev
			//URL stockURL = new
			//URL("http://data.d4science.org/c29KTUluTkZnRlB0WXE5NVNaZnRoR0dtYThUSmNTVlhHbWJQNStIS0N6Yz0");
			BufferedReader in = new BufferedReader(new InputStreamReader(stockURL.openStream()));
			reader = new CSVReader(in, ',');
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {
				if (nextLine[1].equals("BACKEND") || (nextLine[1].equals("FRONTEND"))) {
					continue;
				}
				if (nextLine[0].equals(cluster.getName())) {
					Host b = new Host();
					b.setName(nextLine[1]);
					out.add(b);
					this.logger.info(b.getFullyQualifiedName());
				}
			}
		}
		this.logger.info(out.toString());
		return out;

	}

	public static void main(String[] args) throws ObjectNotFound, Exception {
		HAProxy a = new HAProxy();
		//ScopeProvider.instance.set("/gcube/devNext/NextNext");
		//ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps/RPrototypingLab");
		SecurityTokenProvider.instance.set("3a23bfa4-4dfe-44fc-988f-194b91071dd2-843339462");
		
		CheckPermission test = new CheckPermission();
		CheckPermission.apply("708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548", "/gcube/devNext/NextNext");
		
		
		//ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps/RPrototypingLab");
		// System.out.println(a.getHProxy());
		// System.out.println(a.MapCluster());
		//System.out.println(a.listDataMinersByCluster("708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548","/gcube/devNext/NextNext"));
		// System.out.println(a.listDataMinersByCluster());

		// List<Dependency> list = new LinkedList<Dependency>();
		// Dependency aa = new Dependency();
		// aa.setName("testnunzio");
		// aa.setType("cran:");
		// list.add(aa);

		// a.checkSVNdep();
		// System.out.println(a.getDataminer("dataminer1-devnext.d4science.org").getDomain());
		// System.out.println(a.listDataminersInVRE());
	}
}
