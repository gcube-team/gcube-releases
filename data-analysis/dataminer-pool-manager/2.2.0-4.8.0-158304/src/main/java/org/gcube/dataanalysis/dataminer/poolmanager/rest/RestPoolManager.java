package org.gcube.dataanalysis.dataminer.poolmanager.rest;


import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.service.DataminerPoolManager;
import org.gcube.dataanalysis.dataminer.poolmanager.util.AlgorithmBuilder;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

@Path("/")
public class RestPoolManager implements PoolManager {
	
	

    //@Context ServletContext context;

	private final Logger logger;

	private DataminerPoolManager service;
	
	//@Context
	//private ApplicationContext context = ContextProvider.get();
	
	public RestPoolManager() {
		this.logger = LoggerFactory.getLogger(RestPoolManager.class);
		this.service = new DataminerPoolManager();
	
	}
	

	@GET
	@Path("/algorithm/stage")
	@Produces("text/plain")
	public String stageAlgorithm(
			@QueryParam("algorithmPackageURL") String algorithmPackageURL,
			@QueryParam("targetVRE") String targetVRE,
			@QueryParam("category") String category,
			@QueryParam("algorithm_type") String algorithm_type) throws IOException, InterruptedException {
		this.logger.debug("Stage algorithm method called");
		Algorithm algo = AlgorithmBuilder.create(algorithmPackageURL);	
		//String env = context.application().getInitParameter("Environment");
		return this.service.stageAlgorithm(algo,targetVRE,category,algorithm_type/*,env*/);
	}


	@GET
	@Path("/algorithm/add")
	@Produces("text/plain")
	public String publishAlgorithm(
			@QueryParam("algorithmPackageURL") String algorithmPackageURL,
			//@QueryParam("targetVREToken") String targetVREToken,
			@QueryParam("targetVRE") String targetVRE,
			@QueryParam("category") String category,
			@QueryParam("algorithm_type") String algorithm_type) throws IOException, InterruptedException {
		this.logger.debug("Publish algorithm method called");
		Algorithm algo = AlgorithmBuilder.create(algorithmPackageURL);
		//String env = context.application().getInitParameter("Environment");
		return this.service.publishAlgorithm(algo, /*targetVREToken,*/ targetVRE,category,algorithm_type/*,env*/);
	}

	/*
	 * /scopes/<scope> POST // add an algorithm to all dataminers in the scope
	 * /hosts/<hostname> POST // add an algorithm to the given host
	 */

	@GET
	@Path("/log")
	@Produces("text/plain")
	public String getLogById(@QueryParam("logUrl") String logUrl) throws IOException {
		// TODO Auto-generated method stub
		this.logger.debug("Get log by id method called");
		this.logger.debug("Returning Log =" + logUrl);
		return service.getLogById(logUrl);
	}

	
	@GET
	@Path("/monitor")
	@Produces("text/plain")
	public String getMonitorById(@QueryParam("logUrl") String logUrl) throws IOException {
		// TODO Auto-generated method stub
		this.logger.debug("Get monitor by id method called");
		this.logger.debug("Returning Log =" + logUrl);
		return service.getMonitorById(logUrl);
	}
	
	
	
	
	
	
		@Override
	public Algorithm extractAlgorithm(String url) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) throws ObjectNotFound, Exception {
		
		
		// System.out.println(System.getProperty("user.home")+File.separator+"/gcube/dataminer-pool-manager");
//		// ProxySelector.setDefault(new
//		// PropertiesBasedProxySelector("/home/ngalante/.proxy-settings"));
//
//		ScopeProvider.instance.set("/d4science.research-infrastructures.eu/gCubeApps/RPrototypingLab");
//		SecurityTokenProvider.instance.set("3a23bfa4-4dfe-44fc-988f-194b91071dd2-843339462");
	
		ScopeProvider.instance.set("/gcube/devNext");
		SecurityTokenProvider.instance.set("708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548");
		
		
		
//		AuthorizationEntry entry = authorizationService().get("708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548");
//		System.out.println(entry.getContext());
		

		RestPoolManager a = new RestPoolManager();
		
		a.stageAlgorithm("http://data-d.d4science.org/TSt3cUpDTG1teUJMemxpcXplVXYzV1lBelVHTTdsYjlHbWJQNStIS0N6Yz0");
//		//a.publishAlgorithm("http://data.d4science.org/MnovRjZIdGV5WlB0WXE5NVNaZnRoRVg0SU8xZWpWQlFHbWJQNStIS0N6Yz0", "708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548","/gcube/devNext/NextNext");
//		// PoolManager aa = new DataminerPoolManager();
//

	}
	
	
	
	//Production Testing
	/*
	stageAlgorithm(Rproto caller token,pacchetto, category)
	http://node2-d-d4s.d4science.org:8080/dataminer-pool-manager-1.0.0-SNAPSHOT/rest/algorithm/stage?gcube-token=3a23bfa4-4dfe-44fc-988f-194b91071dd2-843339462&algorithmPackageURL=http://data.d4science.org/dENQTTMxdjNZcGRpK0NHd2pvU0owMFFzN0VWemw3Zy9HbWJQNStIS0N6Yz0&category=ICHTHYOP_MODEL
	
	publishAlgorithm(Rproto caller token, pacchetto, category, target token, target prod vre)
    node2-d-d4s.d4science.org:8080/dataminer-pool-manager-1.0.0-SNAPSHOT/rest/algorithm/add?gcube-token=708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548&algorithmPackageURL=http://data.d4science.org/dENQTTMxdjNZcGRpK0NHd2pvU0owMFFzN0VWemw3Zy9HbWJQNStIS0N6Yz0&category=ICHTHYOP_MODEL&targetVREToken=3a23bfa4-4dfe-44fc-988f-194b91071dd2-843339462&targetVRE=/d4science.research-infrastructures.eu/gCubeApps/RPrototypingLab	
	
	getLogById(Rproto caller token, logid)
	http://node2-d-d4s.d4science.org:8080/dataminer-pool-manager-1.0.0-SNAPSHOT/rest/log?gcube-token=3a23bfa4-4dfe-44fc-988f-194b91071dd2-843339462&logUrl=
	*/
	
	
	//dev Testing
	/*
	stageAlgorithm(dev_caller_vre_token,pacchetto, category)
	http://node2-d-d4s.d4science.org:8080/dataminer-pool-manager-1.0.0-SNAPSHOT/rest/algorithm/stage?gcube-token=708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548&algorithmPackageURL=http://data.d4science.org/dENQTTMxdjNZcGRpK0NHd2pvU0owMFFzN0VWemw3Zy9HbWJQNStIS0N6Yz0&category=ICHTHYOP_MODEL		
		
	publishAlgorithm(dev_caller_vre_token, pacchetto, category, target token, target prod vre)
	http://node2-d-d4s.d4science.org:8080/dataminer-pool-manager-1.0.0-SNAPSHOT/rest/log?gcube-token=708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548&logUrl=450bb7f9-9e38-4bde-8f4d-f3296f95deba		
	
	getLogById(dev_caller_vre_token, logid)
	http://node2-d-d4s.d4science.org:8080/dataminer-pool-manager-1.0.0-SNAPSHOT/rest/log?gcube-token=708e7eb8-11a7-4e9a-816b-c9ed7e7e99fe-98187548&logUrl=426c8e35-a624-4710-b612-c90929c32c27	*/
	

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
	public String addAlgorithmToHost(Algorithm algo, String host,  boolean test)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String addAlgorithmToVRE(Algorithm algo, String vre, boolean test)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String stageAlgorithm(String algorithmPackageURL) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String publishAlgorithm(String algorithmPackageURL, String targetVREToken, String targetVRE)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}





	


	
}
