package org.gcube.dataanalysis.executor.nodes.transducers;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.dataanalysis.ecoengine.utils.ZipTools;
import org.gcube.dataanalysis.executor.util.DataTransferer;
import org.gcube.dataanalysis.executor.util.InfraRetrieval;


public class WebApplicationPublisher extends StandardLocalExternalAlgorithm{
//	private static String MainPageParam = "MainPage";
	private static String FileParam = "ZipFile";
	private String transferServiceAddress = "";
	private int transferServicePort = 0;
	
	@Override
	public String getDescription() {
		return "This algorithm publishes a zip file containing a Web site, based on html and javascript in the e-Infrastructure. It generates a public URL to the application that can be shared.";
	}

	@Override
	public void init() throws Exception {
	}

	@Override
	protected void process() throws Exception {
		String uuid = "webpub_" + UUID.randomUUID();
		File folder = new File(config.getConfigPath(), uuid);
		try
		{
			status = 10;
		String scope = config.getGcubeScope();
		String username = config.getParam("ServiceUserName");
		String fileAbsolutePath = config.getParam(FileParam);
//		String mainPage = config.getParam(MainPageParam);
		
		AnalysisLogger.getLogger().debug("scope: "+scope);
		AnalysisLogger.getLogger().debug("username: "+username);
		AnalysisLogger.getLogger().debug("fileAbsolutePath: "+fileAbsolutePath);
//		AnalysisLogger.getLogger().debug("layerTitle: "+mainPage);
		
		if (scope==null || username==null)
			throw new Exception ("Service parameters are not set - please contact the Administrators");
		if (fileAbsolutePath==null || fileAbsolutePath.trim().length()==0)
			throw new Exception ("No file has been provided to the process");
//		if (mainPage==null || mainPage.trim().length()==0)
//			throw new Exception ("Please provide a valid main page");
	 
		File f = new File(fileAbsolutePath); 
		String fileName = f.getName();
       		
		//unzip the file
		AnalysisLogger.getLogger().debug("Package is in file "+fileName);
		
		boolean mkdir = folder.mkdir();
		AnalysisLogger.getLogger().debug("Sandbox " + folder.getAbsolutePath() + " generated: " + mkdir);
		AnalysisLogger.getLogger().debug("Unzipping package into " + folder.getAbsolutePath());
		ZipTools.unZip(f.getAbsolutePath(), folder.getAbsolutePath());
//		f.delete();
		AnalysisLogger.getLogger().debug("Package unzipped and original file deleted");
		
		File[] webappfiles = folder.listFiles();
		//get all files for the upload
		String prefix = "/"+folder.getName()+"/";
		
		Map<String,String> allfiles = getFilesPaths(webappfiles,prefix);
		
		//discover the dataTransfer service with Apache
		getTransferInfo(config.getGcubeScope());
		String remoteFolder = "/var/www/html";
	    //upload every file
		int nfiles = allfiles.size();
		float step = 80f/(float)nfiles;
		float initialStatus=status;
		int i=0;
		for (String key:allfiles.keySet()){
			status=(float)MathFunctions.roundDecimal(initialStatus+(float)i*step,2);
			String subpath = allfiles.get(key);
			subpath = subpath.substring(0,subpath.lastIndexOf("/"));
			String remotePath = remoteFolder+subpath+"/";
			AnalysisLogger.getLogger().debug("Uploading "+key+" -> "+remotePath);
			
			boolean transferout = DataTransferer.transferFileToService(scope, username, transferServiceAddress, transferServicePort, key, remotePath);
			if (!transferout){
				throw new Exception("Error transferring files to the infrastructure ");
			}
			i++;
		}
		String producedPage = "http://"+transferServiceAddress+"/"+uuid+"/";
		if (webappfiles.length==1 && webappfiles[0].isDirectory()){
			producedPage = producedPage + webappfiles[0].getName()+"/";
		}
		AnalysisLogger.getLogger().debug("Entry point of the page "+producedPage);
      
      //get URL
		addOutputString("Generated Website - Main URL", producedPage);
	}catch(Exception e){
			e.printStackTrace();
			AnalysisLogger.getLogger().debug(e);
			AnalysisLogger.getLogger().debug("An error occurred!");
			throw e;
		}finally{	
		       //clean everything
			if (folder.exists()){
				AnalysisLogger.getLogger().debug("Cleaning folder "+folder);
				FileUtils.cleanDirectory(folder);
				FileUtils.deleteDirectory(folder);
			}
	}
		AnalysisLogger.getLogger().debug("Process finished");
		status = 100;
	}

	private Map<String,String> getFilesPaths(File[] toexplore, String prefix){
		Map<String,String> toreturn = new HashMap<String,String> ();
		for (File toex:toexplore){
			if (toex.isDirectory()){
				toreturn.putAll(getFilesPaths(toex.listFiles(),prefix+toex.getName()+"/"));
			}
			else
				toreturn.put(toex.getAbsolutePath(),prefix+toex.getName());
		}
		
		return toreturn;
	}
	@Override
	protected void setInputParameters() {
		
			inputs.add(new PrimitiveType(File.class.getName(), null, PrimitiveTypes.FILE, FileParam, "Zip file containing the Web site"));
			inputs.add(new ServiceType(ServiceParameters.USERNAME,"ServiceUserName","The final user Name"));
//			addStringInput(MainPageParam, "Main page of the website", "index.html");
		}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("WebApplicationPublisher - shutdown");
	}	

	
	public void getTransferInfo(String scope) throws Exception{
		
		List<String> apacheAddress = InfraRetrieval.retrieveServiceAddress("Application", "Apache Server", scope, "Transect");
		
		if (apacheAddress.size()==0)
			throw new Exception("Apache Server resource is not available in scope "+scope);
		
		String apacheServiceAddress = apacheAddress.get(0);
		apacheServiceAddress = apacheServiceAddress.substring(apacheServiceAddress.indexOf("http://")+7);
		
		AnalysisLogger.getLogger().debug("Found "+apacheAddress.size()+" services");
		AnalysisLogger.getLogger().debug("Apache address: "+apacheServiceAddress);
		List<String> dataTransferAddress = InfraRetrieval.retrieveService("agent-service", scope);
		
		if (dataTransferAddress.size()==0)
			throw new Exception("Data Transfer services are not available in scope "+scope);
		
		AnalysisLogger.getLogger().debug("Found "+dataTransferAddress.size()+" transfer services");
		
		int apacheDTPort = 9090; 
		boolean found = false;
		for (String datatransferservice:dataTransferAddress){
			AnalysisLogger.getLogger().debug("Transfer service found");
			datatransferservice = datatransferservice.substring(datatransferservice.indexOf("http://")+7);
			String servicehost = datatransferservice.substring(0,datatransferservice.indexOf(":"));
			String serviceport = datatransferservice.substring(datatransferservice.indexOf(":")+1,datatransferservice.indexOf("/"));
			AnalysisLogger.getLogger().debug("Transfer service: "+servicehost+":"+serviceport);
			if (apacheServiceAddress.equals(servicehost)){
				apacheDTPort = Integer.parseInt(serviceport);
				found = true;
				break;
			}
		}
		
		if (!found)
			throw new Exception("Apache data transfer has not been found in the same scope of the catalog: "+scope);
		else
			AnalysisLogger.getLogger().debug("Transfer service found at address "+apacheServiceAddress+":"+apacheDTPort);
		
		transferServiceAddress = apacheServiceAddress;
		transferServicePort = apacheDTPort;
	}
	
}

