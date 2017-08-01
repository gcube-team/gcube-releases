//package org.gcube.dataanalysis.dataminer.poolmanager.rest;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.UnknownHostException;
//
//import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
//
//public interface PoolManager {
//
//	String addAlgorithmToVRE(Algorithm algo, String vre) throws IOException, InterruptedException;
//
//	Algorithm extractAlgorithm(String url) throws IOException;
//
//	String getLogById(String logId) throws IOException;
//
//	void getLogId(Algorithm algo, String vre);
//
//	String getScriptFromURL(URL logId) throws IOException;
//
//	URL getURLfromWorkerLog(String logUrl) throws MalformedURLException, UnknownHostException;
//
//}



package org.gcube.dataanalysis.dataminer.poolmanager.rest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.tmatesoft.svn.core.SVNException;

public interface PoolManager {

  String addAlgorithmToVRE(Algorithm algo, String vre, boolean svn ) throws IOException, InterruptedException;
  String addAlgorithmToHost(Algorithm algo, String host, boolean svn) throws IOException, InterruptedException;
	
	Algorithm extractAlgorithm(String url) throws IOException;

	String getLogById(String logId) throws IOException;

	void getLogId(Algorithm algo, String vre);

	String getScriptFromURL(URL logId) throws IOException;

	URL getURLfromWorkerLog(String logUrl) throws MalformedURLException, UnknownHostException;
	
	void addAlgToIs(Algorithm algo);
	
	Set<Algorithm> getAlgoFromIs();
	List<String> updateSVN(String file, List<String> ldep) throws SVNException, IOException;
	
	
}
