//package org.gcube.dataanalysis.dataminer.poolmanager.rest;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.ProxySelector;
//import java.net.URL;
//import java.net.UnknownHostException;
//
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//
//import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
//import org.gcube.dataanalysis.dataminer.poolmanager.service.DataminerPoolManager;
//import org.gcube.dataanalysis.dataminer.poolmanager.util.PropertiesBasedProxySelector;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
//@Path("/")
//public class RestPoolManager implements PoolManager {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(RestPoolManager.class);
//
//	private PoolManager service = new DataminerPoolManager();
//
//	
//	@GET
//	@Path("/add")
//	@Produces("text/plain")
//	public String addAlgorithmToVRE(
//			@QueryParam("algorithm") String algorithm, 
//			@QueryParam("vre") String vre,
//			@QueryParam("name") String name,
//			@QueryParam("description") String description,
//			@QueryParam("category") String category,
//			@QueryParam("algorithmType") String algorithmType,
//			@QueryParam("skipJava") String skipJava) throws IOException, InterruptedException {
//		// TODO Auto-generated method stub
//		LOGGER.debug("Adding algorithm =" + algorithm + " to VRE =" + vre);
//		Algorithm algo = service.extractAlgorithm(algorithm);
//		
//		if (algo.getCategory() == null){
//			algo.setCategory(category);
//		} else category = algo.getCategory();
//		
//		if (algo.getAlgorithmType() == null){
//			algo.setAlgorithmType(algorithmType);
//		} else algorithmType = algo.getCategory();		
//		
//		if (algo.getSkipJava() == null){
//			algo.setSkipJava(skipJava);
//		} else skipJava = algo.getSkipJava();		
//		
//		if (algo.getName() == null){
//			algo.setCategory(name);
//		} else name = algo.getName();
//				
//		if (algo.getDescription() == null){
//			algo.setDescription(description);;
//		} else description = algo.getDescription();
//
//		return service.addAlgorithmToVRE(algo, vre);
//	}
//
//	
//	@GET
//	@Path("/log")
//	@Produces("text/plain")
//	public String getLogById(@QueryParam("logUrl") String logUrl) throws IOException {
//		// TODO Auto-generated method stub
//		LOGGER.debug("Returning Log =" + logUrl);
//		return service.getScriptFromURL(service.getURLfromWorkerLog(logUrl));
//	}
//	
//	
//
//
//
//	@Override
//	public Algorithm extractAlgorithm(String url) throws IOException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	public static void main(String[] args) throws IOException, InterruptedException {
//		RestPoolManager a = new RestPoolManager();
//		//System.out.println(System.getProperty("user.home")+File.separator+"/gcube/dataminer-pool-manager");
//		
//		
//	    //ProxySelector.setDefault(new PropertiesBasedProxySelector("/home/ngalante/.proxy-settings"));
//		
//		ScopeProvider.instance.set("/gcube/devNext/NextNext");
//		a.addAlgorithmToVRE(
//		"http://data.d4science.org/S2h1RHZGd0JpWnBjZk9qTytQTndqcDRLVHNrQUt6QjhHbWJQNStIS0N6Yz0",
//		"/gcube/devNext/NextNext",
//		null, null, "test", "transducerers", "N");
//		
//	//System.out.println(a.getLogById("34ac474d-b9df-4929-87e1-2a0ae26cf898"));
//	}
//
//
//	@Override
//	public void getLogId(Algorithm algo, String vre) {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//	@Override
//	public String getScriptFromURL(URL logId) throws IOException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public String addAlgorithmToVRE(Algorithm algo, String vre) throws IOException, InterruptedException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//	@Override
//	public URL getURLfromWorkerLog(String logUrl) throws MalformedURLException, UnknownHostException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//
//
//	
//
//
//
//
//}
package org.gcube.dataanalysis.dataminer.poolmanager.rest;

import java.awt.color.ICC_ColorSpace;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProxySelector;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.math.RandomUtils;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.clients.ISClient;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency;
import org.gcube.dataanalysis.dataminer.poolmanager.service.DataminerPoolManager;
import org.gcube.dataanalysis.dataminer.poolmanager.util.PropertiesBasedProxySelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

@Path("/")
public class RestPoolManager implements PoolManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestPoolManager.class);

	private PoolManager service = new DataminerPoolManager();

	/*
	 * /scopes/<scope> POST // add an algorithm to all dataminers in the scope
	 * /hosts/<hostname> POST // add an algorithm to the given host
	 */

	@GET
	@Path("/scopes/add")
	@Produces("text/plain")
	public String addAlgorithmToVRE(
			@QueryParam("algorithm") String algorithm, 
			@QueryParam("name") String name,
			@QueryParam("description") String description,
			@QueryParam("category") String category,
			@DefaultValue("transducerers") @QueryParam("algorithmType") String algorithmType,
			@DefaultValue("N") @QueryParam("skipJava") String skipJava,
			@DefaultValue("false") @QueryParam("publish") boolean publish,
			@DefaultValue("false") @QueryParam("updateSVN") boolean updateSVN)
			throws IOException, InterruptedException, SVNException {
		Algorithm algo = this.getAlgorithm(algorithm, /*vre*/null, null, name, description, category, algorithmType, skipJava);
		// publish algo
		if (publish) {
			service.addAlgToIs(algo);
		}
		return service.addAlgorithmToVRE(algo, ScopeProvider.instance.get(),updateSVN);
	}

	
	
	@GET
	@Path("/hosts/add")
	@Produces("text/plain")
	public String addAlgorithmToHost(
			@QueryParam("algorithm") String algorithm, 
			@QueryParam("hostname") String hostname,
			@QueryParam("name") String name,
			@QueryParam("description") String description,
			@QueryParam("category") String category,
			@DefaultValue("transducerers") @QueryParam("algorithmType") String algorithmType,
			@DefaultValue("N") @QueryParam("skipJava") String skipJava,
			@DefaultValue("false") @QueryParam("publish") boolean publish,
			@DefaultValue("false") @QueryParam("updateSVN") boolean updateSVN)
			throws IOException, InterruptedException, SVNException {
		Algorithm algo = this.getAlgorithm(algorithm, null, hostname, name, description, category, algorithmType,
				skipJava);
		// publish algo
		if (publish) {
			service.addAlgToIs(algo);
		}
		return service.addAlgorithmToHost(algo, hostname,updateSVN);
	}

	private Algorithm getAlgorithm(String algorithm, String vre, String hostname, String name, String description,
			String category, String algorithmType, String skipJava) throws IOException, InterruptedException {
		LOGGER.debug("Adding algorithm =" + algorithm + " to VRE =" + ScopeProvider.instance.get());
		Algorithm algo = service.extractAlgorithm(algorithm);

		if (algo.getCategory() == null) {
			algo.setCategory(category);
		} else
			algo.setCategory(algo.getCategory());

		if (algo.getAlgorithmType() == null) {
			algo.setAlgorithmType(algorithmType);
		} else
			algo.setAlgorithmType(algo.getCategory());

		if (algo.getSkipJava() == null) {
			algo.setSkipJava(skipJava);
		} else
			algo.setSkipJava(algo.getSkipJava());

		if (algo.getName() == null) {
			algo.setName(name);
		} else
			algo.setName(algo.getName());

		if (algo.getDescription() == null) {
			algo.setDescription(description);
			;
		} else
			algo.setDescription(algo.getDescription());

		return algo;
	}

	@GET
	@Path("/log")
	@Produces("text/plain")
	public String getLogById(@QueryParam("logUrl") String logUrl) throws IOException {
		// TODO Auto-generated method stub
		LOGGER.debug("Returning Log =" + logUrl);
		return service.getScriptFromURL(service.getURLfromWorkerLog(logUrl));
	}

	@Override
	public Algorithm extractAlgorithm(String url) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) throws IOException, InterruptedException, SVNException {
		// System.out.println(System.getProperty("user.home")+File.separator+"/gcube/dataminer-pool-manager");
		// ProxySelector.setDefault(new
		// PropertiesBasedProxySelector("/home/ngalante/.proxy-settings"));

		ScopeProvider.instance.set("/gcube/devNext/NextNext");

		// PoolManager aa = new DataminerPoolManager();
		// System.out.println(aa.getAlgoById("ICHTHYOP_MODEL_ONE_BY_ONE@3141d3aa-5f93-409f-b6f8-9fae0a6c0ee3"));
		// System.out.println(aa.getAlgoFromIs());

	
		 RestPoolManager a = new RestPoolManager();
		 a.addAlgorithmToVRE(
		 "http://data.d4science.org/TVc0TW9Ud1FjYlppK0NHd2pvU0owNmRFWHE4OW4xSGNHbWJQNStIS0N6Yz0",
		 "test",
		 null,
		 null,
		 "N",
		 "transducerers",
		 false,
		 false);

//		 a.addAlgorithmToHost(
//		 "http://data.d4science.org/MnovRjZIdGV5WlB0WXE5NVNaZnRoRVg0SU8xZWpWQlFHbWJQNStIS0N6Yz0",
//		 "dataminer1-pre.d4science.org",
//		 "ICHTHYOP_MODEL_ONE_BY_ONE", null, "ICHTHYOP_MODEL", "transducerers",
//		 "N",false, false);
		
		

//		PoolManager aa = new DataminerPoolManager();
//		List<String> ls = new LinkedList<String>();
//		String afa = "test";
//		ls.add(afa);
//
//		System.out.println(aa.updateSVN("r_deb_pkgs.txt", ls));
	}

	@Override
	public void getLogId(Algorithm algo, String vre) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getScriptFromURL(URL logId) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public URL getURLfromWorkerLog(String logUrl) throws MalformedURLException, UnknownHostException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAlgToIs(Algorithm algo) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<Algorithm> getAlgoFromIs() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> updateSVN(String file, List<String> ldep) throws SVNException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String addAlgorithmToVRE(Algorithm algo, String vre, boolean svn) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String addAlgorithmToHost(Algorithm algo, String host, boolean svn)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
