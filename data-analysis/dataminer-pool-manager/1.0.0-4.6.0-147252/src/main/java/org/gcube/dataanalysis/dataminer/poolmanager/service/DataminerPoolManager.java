//package org.gcube.dataanalysis.dataminer.poolmanager.service;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.net.UnknownHostException;
//import java.util.UUID;
//
//import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.AnsibleBridge;
//import org.gcube.dataanalysis.dataminer.poolmanager.clients.ISClient;
//import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
//import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.AlgorithmSet;
//import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
//import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Host;
//import org.gcube.dataanalysis.dataminer.poolmanager.process.AlgorithmPackageParser;
//import org.gcube.dataanalysis.dataminer.poolmanager.rest.PoolManager;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class DataminerPoolManager implements PoolManager {
//
//	
//	private static final Logger LOGGER = LoggerFactory.getLogger(DataminerPoolManager.class);
//
//	
////  static Collection<Algorithm> algorithms;
////  
////  static Collection<AlgorithmSet> sets;
////
////  static {
////    algorithms = new Vector<>();
////  }
////
////  public DataminerPoolManager() {
////  }
////
////  /**
////   * Add a new algorithm to the set of known ones. No further action is expected
////   * on the pool.
////   */
////  public void publishAlgorithm(Algorithm algorithm) {
////    algorithms.add(algorithm);
////  }
////
////  /**
////   * Re-deploy the given algorithm wherever it's installed
////   * 
////   * @param algorithm
////   */
////  /*
////   * public void updateAlgorithm(Algorithm algorithm) { // TODO implement this }
////   */
////
////  /**
////   * Add the give algorithm to the given set
////   * 
////   * @param algorithmId
////   * @param setId
////   */
////  public void addAlgorithmToSet(String algorithmName, String setName) {
////    AlgorithmSet set = this.getAlgorithmSet(setName);
////    Algorithm algorithm = this.getAlgorithm(algorithmName);
////    if (set != null && algorithm != null) {
////      set.addAlgorithm(algorithm);
////      this.updateClusters();
////    }
////  }
////
////  /**
////   * Apply the given set of algorithms to the given cluster
////   * 
////   * @param setId
////   * @param clusterId
////   */
////  public void applyAlgorithmSetToCluster(String setName, String clusterName) {
////    AlgorithmSet set = this.getAlgorithmSet(setName);
////    Cluster cluster = new ISClient().getCluster(clusterName);
////    if (set != null && cluster != null) {
////      cluster.addAlgorithmSet(set);
////      this.updateClusters();
////    }
////  }
////
////  private AlgorithmSet getAlgorithmSet(String name) {
////    for (AlgorithmSet set : sets) {
////      if (name.equals(set.getName())) {
////        return set;
////      }
////    }
////    return null;
////  }
////
////  private Algorithm getAlgorithm(String name) {
////    for (Algorithm a : algorithms) {
////      if (name.equals(a.getName())) {
////        return a;
////      }
////    }
////    return null;
////  }
//
//	
//
//
////	     
////	public void getLogId(final Algorithm algorithm, final String vre) {
////	    new Thread() {
////	        public void run() {
////	            while (true) {
////	                try {
////	                	addAlgorithmToVRE(algorithm, vre);
////	                } catch (Exception e) {
////	                    //log here
////	                }
////	            }
////	        }
////	    }.start();
////	}	
////	
////	
//	
////	public String getLogId(){
////		PrintStream console = System.out;
////	    File path = new File(worker.getWorkdir() + File.separator + "logs");
////		path.mkdirs();
////		File n = new File(path + File.separator + worker.getWorkerId());
////		FileOutputStream fos = new FileOutputStream(n);
////		PrintStream ps = new PrintStream(fos);
////		System.setOut(ps);
////		worker.apply();
////		System.setOut(console);
////		worker.apply();
////		System.out.println("Log stored to to " + n.getAbsolutePath());
////	}
//	
//	
//	
//	
////   public String getLogById(String id) throws IOException {
////	   String strLine = null;
////	   try{
////		   FileInputStream fstream = new FileInputStream("/tmp/dataminer-pool-manager/work/"+id+"/logs/"+id);
////		   BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
////		   /* read log line by line */
////		   while ((strLine = br.readLine()) != null)   {
////		     /* parse strLine to obtain what you want */
////		     System.out.println (strLine);
////		   }
////		   br.close();
////		} catch (Exception e) {
////		     System.err.println("Error: " + e.getMessage());
////		}
////	return strLine; 
////	}
//	
//
//   public String getScriptFromURL(URL url) throws IOException {
//	    if (url == null) {
//	      return null;
//	    }
//	    URLConnection yc = url.openConnection();
//	    BufferedReader input = new BufferedReader(new InputStreamReader(
//	        yc.getInputStream()));
//	    String line;
//	    StringBuffer buffer = new StringBuffer();
//	    while ((line = input.readLine()) != null) {
//	      buffer.append(line + "\n");
//	    }
//	    String bufferScript = buffer.substring(0, buffer.length());
//	    input.close();
//	    return bufferScript;
//	  }
//	
//   
//   
//   
//	
//  /**
//   * Publish the given algorithm in the given VRE
//   * 
//   * @param algorithmName
//   * @param vre
//   * 
//   */
//  public String addAlgorithmToVRE(Algorithm algorithm, final String vre) throws IOException {
//    // create a fake algorithm set
//    final AlgorithmSet algoSet = new AlgorithmSet();
//    algoSet.setName("fake");
//    algoSet.addAlgorithm(algorithm);
//    final String uuid = UUID.randomUUID().toString();
//        
//    new Thread(new Runnable() {
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			try {
//				addAlgorithmsToVRE(algoSet, vre, uuid);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}).start();
//	//this line will execute immediately, not waiting for your task to complete
//	System.out.println(uuid);
//	return uuid;
//	  }
//
//  
//  
//  public URL getURLfromWorkerLog(String a) throws MalformedURLException, UnknownHostException{
//		
//		File path = new File(System.getProperty("user.home")+File.separator+"/gcube/dataminer-pool-manager/work/"+a+File.separator+"logs");
//		path.mkdirs();
//		File n = new File(path + File.separator +a);
//        //String addr = InetAddress.getLocalHost().getHostAddress();
//		
//		return new File(n.getPath()).toURI().toURL();
//	  }
//   
//    
//  public String addAlgorithmsToVRE(AlgorithmSet algorithms, String vre, String uuid) throws IOException {
//    
//    // create the cluster (dataminers in the vre)
//    Cluster cluster = new Cluster();
//    for(Host h:new ISClient().listDataminersInVRE()) {
//      cluster.addHost(h);
//    }
//
//    // apply the changes
//    AnsibleBridge a = new AnsibleBridge();
//    return a.applyAlgorithmSetToCluster(algorithms, cluster,uuid).getWorkerId();
//    
//  }
//
//  public Algorithm extractAlgorithm(String url) throws IOException {
//	    return new AlgorithmPackageParser().parsePackage(url);
//	  }
//
//
//@Override
//public void getLogId(Algorithm algo, String vre) {
//	// TODO Auto-generated method stub
//	
//}
//
//
//@Override
//public String getLogById(String logId) throws IOException {
//	// TODO Auto-generated method stub
//	return null;
//}
//
//
//
//
//  
//}
package org.gcube.dataanalysis.dataminer.poolmanager.service;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.resources.gcore.Software.Profile.Dependency;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.ansiblebridge.AnsibleBridge;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.HAProxy;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.ISClient;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.AlgorithmSet;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Host;
import org.gcube.dataanalysis.dataminer.poolmanager.process.AlgorithmPackageParser;
import org.gcube.dataanalysis.dataminer.poolmanager.rest.PoolManager;
import org.gcube.informationsystem.publisher.AdvancedScopedPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.wc.SVNFileUtil;
import org.tmatesoft.svn.core.internal.wc.admin.SVNChecksumInputStream;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class DataminerPoolManager implements PoolManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataminerPoolManager.class);

	// static Collection<Algorithm> algorithms;
	//
	// static Collection<AlgorithmSet> sets;
	//
	// static {
	// algorithms = new Vector<>();
	// }
	//
	// public DataminerPoolManager() {
	// }
	//
	// /**
	// * Add a new algorithm to the set of known ones. No further action is
	// expected
	// * on the pool.
	// */
	// public void publishAlgorithm(Algorithm algorithm) {
	// algorithms.add(algorithm);
	// }
	//
	// /**
	// * Re-deploy the given algorithm wherever it's installed
	// *
	// * @param algorithm
	// */
	// /*
	// * public void updateAlgorithm(Algorithm algorithm) { // TODO implement
	// this }
	// */
	//
	// /**
	// * Add the give algorithm to the given set
	// *
	// * @param algorithmId
	// * @param setId
	// */
	// public void addAlgorithmToSet(String algorithmName, String setName) {
	// AlgorithmSet set = this.getAlgorithmSet(setName);
	// Algorithm algorithm = this.getAlgorithm(algorithmName);
	// if (set != null && algorithm != null) {
	// set.addAlgorithm(algorithm);
	// this.updateClusters();
	// }
	// }
	//
	// /**
	// * Apply the given set of algorithms to the given cluster
	// *
	// * @param setId
	// * @param clusterId
	// */
	// public void applyAlgorithmSetToCluster(String setName, String
	// clusterName) {
	// AlgorithmSet set = this.getAlgorithmSet(setName);
	// Cluster cluster = new ISClient().getCluster(clusterName);
	// if (set != null && cluster != null) {
	// cluster.addAlgorithmSet(set);
	// this.updateClusters();
	// }
	// }
	//
	// private AlgorithmSet getAlgorithmSet(String name) {
	// for (AlgorithmSet set : sets) {
	// if (name.equals(set.getName())) {
	// return set;
	// }
	// }
	// return null;
	// }
	//
	// private Algorithm getAlgorithm(String name) {
	// for (Algorithm a : algorithms) {
	// if (name.equals(a.getName())) {
	// return a;
	// }
	// }
	// return null;
	// }

	//
	// public void getLogId(final Algorithm algorithm, final String vre) {
	// new Thread() {
	// public void run() {
	// while (true) {
	// try {
	// addAlgorithmToVRE(algorithm, vre);
	// } catch (Exception e) {
	// //log here
	// }
	// }
	// }
	// }.start();
	// }
	//
	//

	// public String getLogId(){
	// PrintStream console = System.out;
	// File path = new File(worker.getWorkdir() + File.separator + "logs");
	// path.mkdirs();
	// File n = new File(path + File.separator + worker.getWorkerId());
	// FileOutputStream fos = new FileOutputStream(n);
	// PrintStream ps = new PrintStream(fos);
	// System.setOut(ps);
	// worker.apply();
	// System.setOut(console);
	// worker.apply();
	// System.out.println("Log stored to to " + n.getAbsolutePath());
	// }

	// public String getLogById(String id) throws IOException {
	// String strLine = null;
	// try{
	// FileInputStream fstream = new
	// FileInputStream("/tmp/dataminer-pool-manager/work/"+id+"/logs/"+id);
	// BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	// /* read log line by line */
	// while ((strLine = br.readLine()) != null) {
	// /* parse strLine to obtain what you want */
	// System.out.println (strLine);
	// }
	// br.close();
	// } catch (Exception e) {
	// System.err.println("Error: " + e.getMessage());
	// }
	// return strLine;
	// }

	public String getScriptFromURL(URL url) throws IOException {
		if (url == null) {
			return null;
		}
		URLConnection yc = url.openConnection();
		BufferedReader input = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		String line;
		StringBuffer buffer = new StringBuffer();
		while ((line = input.readLine()) != null) {
			buffer.append(line + "\n");
		}
		String bufferScript = buffer.substring(0, buffer.length());
		input.close();
		return bufferScript;
	}

	/**
	 * Publish the given algorithm in the given VRE
	 * 
	 * @param algorithmName
	 * @param vre
	 * 
	 */
	public String addAlgorithmToVRE(Algorithm algorithm, final String vre, final boolean updateSVN) throws IOException {
		// create a fake algorithm set
		final AlgorithmSet algoSet = new AlgorithmSet();
		algoSet.setName("fake");
		algoSet.addAlgorithm(algorithm);
		final String uuid = UUID.randomUUID().toString();

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					try {
						addAlgorithmsToVRE(algoSet, vre, uuid, updateSVN);
					} catch (SVNException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		// this line will execute immediately, not waiting for your task to
		// complete
		System.out.println(uuid);
		return uuid;
	}

	public String addAlgorithmToHost(Algorithm algorithm, final String hostname, final boolean updateSVN) throws IOException {
		// create a fake algorithm set
		final AlgorithmSet algoSet = new AlgorithmSet();
		algoSet.setName("fake");
		algoSet.addAlgorithm(algorithm);
		final String uuid = UUID.randomUUID().toString();

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					addAlgorithmsToHost(algoSet, hostname, uuid, updateSVN);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SVNException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		// this line will execute immediately, not waiting for your task to
		// complete
		System.out.println(uuid);
		return uuid;
	}

	public URL getURLfromWorkerLog(String a) throws MalformedURLException, UnknownHostException {

		File path = new File(System.getProperty("user.home") + File.separator + "dataminer-pool-manager/work/"
				+ a + File.separator + "logs");
		path.mkdirs();
		File n = new File(path + File.separator + a);
		// String addr = InetAddress.getLocalHost().getHostAddress();

		return new File(n.getPath()).toURI().toURL();
	}

	public String addAlgorithmsToVRE(AlgorithmSet algorithms, String vre, String uuid, boolean updateSVN) throws IOException, InterruptedException, SVNException {

		// create the cluster (dataminers in the vre)
		Cluster cluster = new Cluster();
		 for (Host h : new HAProxy().listDataMinersByCluster()) {
		//for (Host h : new ISClient().listDataminersInVRE()) {
			cluster.addHost(h);
		}

		// apply the changes
		AnsibleBridge a = new AnsibleBridge();
		return a.applyAlgorithmSetToCluster(algorithms, cluster, uuid, updateSVN).getWorkerId();

	}

	public String addAlgorithmsToHost(AlgorithmSet algorithms, String hostname, String uuid, boolean updateSVN)
			throws IOException, InterruptedException, SVNException {

		// create the cluster (dataminers in the vre)
		Cluster cluster = new Cluster();
		for (Host h : new HAProxy().listDataMinersByCluster()) {
			if (h.getName().equals(hostname)) {
				cluster.addHost(h);
			}
		}
		// if(ISClient.getHProxy().equals(hostname)){
		// cluster.addHost(new ISClient().getDataminer(hostname));
		// }
		// apply the changes
		AnsibleBridge a = new AnsibleBridge();
		return a.applyAlgorithmSetToCluster(algorithms, cluster, uuid, updateSVN).getWorkerId();

	}
	


	public Algorithm extractAlgorithm(String url) throws IOException {
		return new AlgorithmPackageParser().parsePackage(url);
	}

	@Override
	public void getLogId(Algorithm algo, String vre) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLogById(String logId) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	// 2017 March 29

	public void unPublishScopedResource(GenericResource resource) throws RegistryNotFoundException, Exception {
		ScopedPublisher scopedPublisher = RegistryPublisherFactory.scopedPublisher();
		AdvancedScopedPublisher advancedScopedPublisher = new AdvancedScopedPublisher(scopedPublisher);
		String id = resource.id();
		LOGGER.debug("Trying to remove {} with ID {} from {}", resource.getClass().getSimpleName(), id,
				ScopeProvider.instance.get());
		// scopedPublisher.remove(resource, scopes);
		advancedScopedPublisher.forceRemove(resource);
		LOGGER.debug("{} with ID {} removed successfully", resource.getClass().getSimpleName(), id);
	}

	public void publishScopedResource(GenericResource a, List<String> scopes)
			throws RegistryNotFoundException, Exception {
		StringWriter stringWriter = new StringWriter();
		Resources.marshal(a, stringWriter);

		ScopedPublisher scopedPublisher = RegistryPublisherFactory.scopedPublisher();
		try {
			System.out.println(scopes);
			System.out.println(stringWriter);
			scopedPublisher.create(a, scopes);
		} catch (RegistryNotFoundException e) {
			System.out.println(e);
			throw e;
		}
	}

	@Override
	public void addAlgToIs(Algorithm algo) {
		GenericResource a = new GenericResource();
		a.newProfile().name(algo.getName()).type("StatisticalManagerAlgorithm").description(algo.getDescription());
		a.profile().newBody(this.getAlgoBody(algo));
		try {
			publishScopedResource(a, Arrays.asList(new String[] { ScopeProvider.instance.get() }));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getAlgoBody(Algorithm algo) {
		return "<category>" + algo.getCategory() + "</category>" + "\n" + "<clazz>" + algo.getClazz() + "</clazz>"
				+ "\n" + "<algorithmType>" + algo.getAlgorithmType() + "</algorithmType>" + "\n" + "<skipJava>"
				+ algo.getSkipJava() + "</skipJava>" + "\n" + "<packageURL>" + algo.getPackageURL() + "</packageURL>"
				+ "\n" + "<dependencies>" + algo.getDependencies() + "</dependencies>";
	}

	public void updateAlg(Algorithm algo) {

		ScopedPublisher scopedPublisher = RegistryPublisherFactory.scopedPublisher();

		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/Name/text() eq '" + algo.getName() + "'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		if (ds.isEmpty()) {
			return;
		}
		GenericResource a = ds.get(0);
		a.profile().newBody(this.getAlgoBody(algo));
		try {
			scopedPublisher.update(a);
		} catch (RegistryNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Algorithm convertAlgo(GenericResource a) {
		Algorithm out = new Algorithm();

		// out.setId(a.profile().body().getElementsByTagName("id").item(0).getTextContent());
		out.setAlgorithmType(a.profile().body().getElementsByTagName("algorithmType").item(0).getTextContent());
		out.setCategory(a.profile().body().getElementsByTagName("category").item(0).getTextContent());
		out.setClazz(a.profile().body().getElementsByTagName("clazz").item(0).getTextContent());
		out.setName(a.profile().name());
		out.setPackageURL(a.profile().body().getElementsByTagName("packageURL").item(0).getTextContent());
		out.setSkipJava(a.profile().body().getElementsByTagName("skipJava").item(0).getTextContent());
		out.setDescription(a.profile().description());

		Set<org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency> deps = new HashSet<org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency>();
		for (int i = 0; i < a.profile().body().getElementsByTagName("dependencies").getLength(); i++) {
			org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency d1 = new org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency();
			d1.setName(a.profile().body().getElementsByTagName("dependencies").item(i).getTextContent());
			deps.add(d1);
		}
		out.setDependencies(deps);
		return out;
	}

	// public Algorithm getAlgoById(String id) {
	// for (Algorithm aa : this.getAlgoFromIs()) {
	// if (aa.getId().equals(id)) {
	// return aa;
	// }
	// }
	// return null;
	// }

	@Override
	public Set<Algorithm> getAlgoFromIs() {
		// TODO Auto-generated method stub

		Set<Algorithm> out = new HashSet<Algorithm>();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq 'StatisticalManagerAlgorithm'")
				.setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		for (GenericResource a : ds) {
			out.add(this.convertAlgo(a));
		}
		return out;
	}



	@Override
	public List<String> updateSVN(String file, List<String> ldep) throws SVNException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
