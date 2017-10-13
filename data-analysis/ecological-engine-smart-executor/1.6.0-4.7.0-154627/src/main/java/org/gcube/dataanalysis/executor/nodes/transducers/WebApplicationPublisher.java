package org.gcube.dataanalysis.executor.nodes.transducers;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalInfraAlgorithm;
import org.gcube.dataanalysis.executor.util.DataTransferer;
import org.gcube.dataanalysis.executor.util.InfraRetrieval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebApplicationPublisher extends StandardLocalInfraAlgorithm{
	//private static String MainPageParam = "MainPage";
	private static final String DATA_TRANSFER_PERSISTENCE_ID = "data-transfer-service";
	private static String FileParam = "ZipFile";
	private String transferServiceAddress = "";
	private int transferServicePort = 0;

	private Logger LOGGER = LoggerFactory.getLogger(WebApplicationPublisher.class);

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

			LOGGER.debug("scope: {}",scope);
			LOGGER.debug("username: {}",username);
			LOGGER.debug("fileAbsolutePath: {}",fileAbsolutePath);
			//		LOGGER.debug("layerTitle: "+mainPage);

			if (scope==null || username==null)
				throw new Exception ("Service parameters are not set - please contact the Administrators");
			if (fileAbsolutePath==null || fileAbsolutePath.trim().length()==0)
				throw new Exception ("No file has been provided to the process");

			getTransferInfo(config.getGcubeScope());

			DataTransferer.transferFileToService(scope, username, transferServiceAddress, transferServicePort, fileAbsolutePath, "", DATA_TRANSFER_PERSISTENCE_ID , true,uuid);
			String producedPage = "http://"+transferServiceAddress+"/"+uuid+"/";

			LOGGER.debug("Entry point of the page "+producedPage);

			//get URL
			addOutputString("Generated Website - Main URL", producedPage);
		}catch(Exception e){
			LOGGER.error("an error occurred!",e);
			throw e;
		}finally{	
			//clean everything
			if (folder.exists()){
				LOGGER.debug("Cleaning folder "+folder);
				FileUtils.cleanDirectory(folder);
				FileUtils.deleteDirectory(folder);
			}
		}
		LOGGER.debug("Process finished");
		status = 100;
	}

/*	private Map<String,String> getFilesPaths(File[] toexplore, String prefix){
		Map<String,String> toreturn = new HashMap<String,String> ();
		for (File toex:toexplore){
			if (toex.isDirectory()){
				toreturn.putAll(getFilesPaths(toex.listFiles(),prefix+toex.getName()+"/"));
			}
			else
				toreturn.put(toex.getAbsolutePath(),prefix+toex.getName());
		}

		return toreturn;
	}*/
	@Override
	protected void setInputParameters() {

		inputs.add(new PrimitiveType(File.class.getName(), null, PrimitiveTypes.FILE, FileParam, "Zip file containing the Web site"));
		inputs.add(new ServiceType(ServiceParameters.USERNAME,"ServiceUserName","The final user Name"));
		//inputs.add(MainPageParam, "Main page of the website", "index.html");
	}

	@Override
	public void shutdown() {
		LOGGER.debug("WebApplicationPublisher - shutdown");
	}	


	public void getTransferInfo(String scope) throws Exception{

		List<String> apacheAddress = InfraRetrieval.retrieveServiceAddress("Application", "Apache Server", scope, "Transect");

		if (apacheAddress.size()==0)
			throw new Exception("Apache Server resource is not available in scope "+scope);

		String apacheServiceAddress = apacheAddress.get(0);
		apacheServiceAddress = apacheServiceAddress.substring(apacheServiceAddress.indexOf("http://")+7);

		LOGGER.debug("Found {} services",apacheAddress.size());
		LOGGER.debug("Apache address: {}",apacheServiceAddress);

		List<String> dataTransferAddress = InfraRetrieval.retrieveService("data-transfer-service", scope);

		if (dataTransferAddress.size()==0)
			throw new Exception("Data Transfer services are not available in scope "+scope);

		LOGGER.debug("Found {} transfer services",dataTransferAddress.size());

		int apacheDTPort = 9090; 
		boolean found = false;
		for (String datatransferservice:dataTransferAddress){
			LOGGER.debug("Transfer service found");
			datatransferservice = datatransferservice.substring(datatransferservice.indexOf("http://")+7);
			String servicehost = datatransferservice.substring(0,datatransferservice.indexOf(":"));
			String serviceport = datatransferservice.substring(datatransferservice.indexOf(":")+1,datatransferservice.indexOf("/"));
			LOGGER.debug("Transfer service: {} , {}",servicehost,serviceport);
			if (apacheServiceAddress.equals(servicehost)){
				apacheDTPort = Integer.parseInt(serviceport);
				found = true;
				break;
			}
		}

		if (!found)
			throw new Exception("Apache data transfer has not been found in the same scope of the catalog: "+scope);
		else
			LOGGER.debug("Transfer service found at address {} : {}",apacheServiceAddress,apacheDTPort);

		transferServiceAddress = apacheServiceAddress;
		transferServicePort = apacheDTPort;
	}

}

