package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.dataspace;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.util.WorkspaceUtil;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.AbstractEcologicalEngineMapper;

public class DataspaceManager implements Runnable {

	public static String dataminerFolder = "DataMiner";
	public static String importedDataFolder = "Input Data Sets";
	public static String computedDataFolder = "Output Data Sets";
	public static String computationsFolder = "Computations";
	AlgorithmConfiguration config;
	ComputationData computation;
	List<StoredData> inputData;
	List<StoredData> outputData;
	List<File> generatedFiles;

	public static String computation_id = "computation_id";
	public static String data_id = "data_id";
	public static String data_type = "data_type";
	public static String operator_name = "operator_name";
	public static String operator_id = "operator_id";
	public static String vre = "VRE";

	public static String operator_description = "operator_description";
	public static String data_description = "data_description";
	public static String creation_date = "creation_date";
	public static String start_date = "start_date";
	public static String end_date = "end_date";
	public static String status = "status";
	public static String execution_platform = "execution_platform";
	public static String error = "error";
	public static String IO = "IO";
	public static String operator = "operator_name";
	public static String payload = "payload";

	public DataspaceManager(AlgorithmConfiguration config, ComputationData computation, List<StoredData> inputData, List<StoredData> outputData, List<File> generatedFiles) {
		this.config = config;
		this.computation = computation;
		this.inputData = inputData;
		this.outputData = outputData;
		this.generatedFiles = generatedFiles;
	}

	public void run() {
		try {
			AnalysisLogger.getLogger().debug("Dataspace->Deleting running computation");
			try {
				deleteRunningComputationData();
			} catch (Exception e) {
				e.printStackTrace();
				AnalysisLogger.getLogger().debug("Dataspace->No running computation available");
			}
			AnalysisLogger.getLogger().debug("Dataspace->Writing provenance information");
			writeProvenance(computation, inputData, outputData);
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("Dataspace-> error writing provenance information " + e.getLocalizedMessage());
			AnalysisLogger.getLogger().debug(e);
		}

	}

	public void createFoldersNetwork(Workspace ws, WorkspaceFolder root) throws Exception {

		AnalysisLogger.getLogger().debug("Dataspace->Creating folders for DataMiner");

		// manage folders: create the folders network
		if (!ws.exists(dataminerFolder, root.getId())) {
			AnalysisLogger.getLogger().debug("Dataspace->Creating DataMiner main folder");
			root.createFolder(dataminerFolder, "A folder collecting DataMiner experiments data and computation information");
			 ((WorkspaceFolder) root.find(dataminerFolder)).setSystemFolder(true);
		}
		WorkspaceFolder dataminerFolderWS = (WorkspaceFolder) root.find(dataminerFolder);
		
		if (!ws.exists(importedDataFolder, dataminerFolderWS.getId())) {
			AnalysisLogger.getLogger().debug("Dataspace->Creating DataMiner imported data folder");
			dataminerFolderWS.createFolder(importedDataFolder, "A folder collecting DataMiner imported data");
		}
		if (!ws.exists(computedDataFolder, dataminerFolderWS.getId())) {
			AnalysisLogger.getLogger().debug("Dataspace->Creating DataMiner computed data folder");
			dataminerFolderWS.createFolder(computedDataFolder, "A folder collecting DataMiner computed data");
		}
		if (!ws.exists(computationsFolder, dataminerFolderWS.getId())) {
			AnalysisLogger.getLogger().debug("Dataspace->Creating DataMiner computations folder");
			dataminerFolderWS.createFolder(computationsFolder, "A folder collecting DataMiner computations information");
		}
	}
	public String uploadData(StoredData data, WorkspaceFolder wsFolder) throws Exception {
		return uploadData(data, wsFolder, true);
	}
	public String uploadData(StoredData data, WorkspaceFolder wsFolder, boolean changename) throws Exception {
		AnalysisLogger.getLogger().debug("Dataspace->Analysing " + data.name);
		// String filenameonwsString = WorkspaceUtil.getUniqueName(data.name, wsFolder);
		String filenameonwsString = data.name ;
		if (changename){
			filenameonwsString = data.name + "_[" + data.computationId + "]";// ("_"+UUID.randomUUID()).replace("-", "");
			if (data.type.equals("text/csv"))
				filenameonwsString+=".csv";
			else if (data.type.equals("image/png"))
				filenameonwsString+=".png";
		}
		InputStream in = null;
		String url = "";
		try {
			long size = 0;
			if (data.type.equals("text/csv")||data.type.equals("application/d4science")||data.type.equals("image/png")) {
				
				if (new File(data.payload).exists() || !data.payload.startsWith("http")) {
					AnalysisLogger.getLogger().debug("Dataspace->Uploading file " + data.payload);
					in = new FileInputStream(new File(data.payload));
					size = new File(data.payload).length();
				} else {
					AnalysisLogger.getLogger().debug("Dataspace->Uploading via URL " + data.payload);
					int tries = 10;
					for (int i=0;i<tries;i++){
						try {
							URL urlc = new URL(data.payload);
							HttpURLConnection urlConnection = (HttpURLConnection) urlc.openConnection();
							urlConnection.setConnectTimeout(10000);
							urlConnection.setReadTimeout(10000);
							in = new BufferedInputStream(urlConnection.getInputStream());
						}catch(Exception ee){
							AnalysisLogger.getLogger().debug(ee);
							AnalysisLogger.getLogger().debug("Dataspace->Retrying connection to "+data.payload+" number "+(i+1));
							in =null;
						}
						if (in!=null)
							break;
						else
							Thread.sleep(10000);
					}

				}
				if (in==null)
					throw new Exception("Impossible to open stream from "+data.payload);
					
				// AnalysisLogger.getLogger().debug("Dataspace->final file name on ws " + data.name+" description "+data.description);
				AnalysisLogger.getLogger().debug("Dataspace->WS OP saving the following file on the WS " + filenameonwsString);
				LinkedHashMap<String, String> properties = new LinkedHashMap<String, String>();
				
				properties.put(computation_id, data.computationId);
				properties.put(vre, data.vre);
				properties.put(creation_date, data.creationDate);
				properties.put(operator, data.operator);
				properties.put(data_id, data.id);
				properties.put(data_description, data.description);
				properties.put(IO, data.provenance.name());
				properties.put(data_type, data.type);
				properties.put(payload, url);
				
				FolderItem fileItem = WorkspaceUtil.createExternalFile(wsFolder, filenameonwsString, data.description, in,properties,data.type,size);
				//fileItem.getProperties().addProperties(properties);
				AnalysisLogger.getLogger().debug("Dataspace->WS OP file saved on the WS " + filenameonwsString);
				
				url = fileItem.getPublicLink(false);
				AnalysisLogger.getLogger().debug("Dataspace->WS OP url produced for the file " + url);
				
				data.payload = url;
				try {
					in.close();
				} catch (Exception e) {
					AnalysisLogger.getLogger().debug("Dataspace->Error creating file " + e.getMessage());
					//AnalysisLogger.getLogger().debug(e);
				}
				AnalysisLogger.getLogger().debug("Dataspace->File created " + filenameonwsString);
			} else {
				AnalysisLogger.getLogger().debug("Dataspace->String parameter " + data.payload);
				url = data.payload;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("Dataspace->Could not retrieve input payload " + data.payload+" - "+e.getLocalizedMessage());
			//AnalysisLogger.getLogger().debug(e);
			url = "payload was not made available for this dataset";
			data.payload = url;
		}
		return url;
	}

	public List<String> uploadInputData(List<StoredData> inputData, WorkspaceFolder dataminerFolder) throws Exception {
		AnalysisLogger.getLogger().debug("Dataspace->uploading input data; Number of data: " + inputData.size());
		WorkspaceItem folderItem = dataminerFolder.find(importedDataFolder);
		List<String> urls = new ArrayList<String>();
		if (folderItem != null && folderItem.isFolder()) {
			WorkspaceFolder destinationFolder = (WorkspaceFolder) folderItem;
			for (StoredData input : inputData) {
				WorkspaceItem item = null;
			
				if (input.type.equals("text/csv")||input.type.equals("application/d4science")||input.type.equals("image/png")) 
					item = destinationFolder.find(input.name);
				
				if (item==null){
					String url = uploadData(input, destinationFolder,false);
					AnalysisLogger.getLogger().debug("Dataspace->returning property "+url);
					urls.add(url);
				}
				else{
					AnalysisLogger.getLogger().debug("Dataspace->Input item "+input.name+" is already available in the input folder");
					String url = item.getPublicLink(false);
					AnalysisLogger.getLogger().debug("Dataspace->returning WS url "+url);
					urls.add(url);
				}
			}
		} else
			AnalysisLogger.getLogger().debug("Dataspace->folder is not valid");

		AnalysisLogger.getLogger().debug("Dataspace->finished uploading input data");
		return urls;
	}

	public List<String> uploadOutputData(List<StoredData> outputData, WorkspaceFolder dataminerFolder) throws Exception {
		AnalysisLogger.getLogger().debug("Dataspace->uploading output data; Number of data: " + outputData.size());
		WorkspaceItem folderItem = dataminerFolder.find(computedDataFolder);
		List<String> urls = new ArrayList<String>();
		if (folderItem != null && folderItem.isFolder()) {
			WorkspaceFolder destinationFolder = (WorkspaceFolder) folderItem;
			for (StoredData output : outputData) {
				String url = uploadData(output, destinationFolder);
				urls.add(url);
			}
		} else
			AnalysisLogger.getLogger().debug("Dataspace->folder is not valid");
		AnalysisLogger.getLogger().debug("Dataspace->finished uploading output data");
		return urls;
	}

	public void uploadComputationData(ComputationData computation, List<StoredData> inputData, List<StoredData> outputData, WorkspaceFolder dataminerFolder, Workspace ws) throws Exception {
		AnalysisLogger.getLogger().debug("Dataspace->uploading computation data");
		WorkspaceItem folderItem = dataminerFolder.find(computationsFolder);
		if (folderItem != null && folderItem.isFolder()) {
			// create a folder in here
			AnalysisLogger.getLogger().debug("Dataspace->Creating computation folder " + computation.id);
			WorkspaceFolder cfolder = ((WorkspaceFolder) folderItem);
			String cfoldername = computation.id;
			WorkspaceFolder newcomputationFolder = null;
			try{
				newcomputationFolder = cfolder.createFolder(cfoldername, computation.operatorDescription);
			}catch(java.lang.ClassCastException e){
				AnalysisLogger.getLogger().debug("Dataspace->concurrency exception - deleting remaining item");
				deleteRunningComputationData();
				newcomputationFolder = cfolder.createFolder(cfoldername, computation.operatorDescription);
			}
			String itemType = "COMPUTATION";

			// create IO folders
			AnalysisLogger.getLogger().debug("Dataspace->creating IO folders under " + cfoldername);
			newcomputationFolder.createFolder(importedDataFolder, importedDataFolder);
			newcomputationFolder.createFolder(computedDataFolder, computedDataFolder);

			// copy IO in those folders
			AnalysisLogger.getLogger().debug("Dataspace->*****uploading inputs in IO folder*****");
			List<String> inputurls = uploadInputData(inputData, newcomputationFolder);
			AnalysisLogger.getLogger().debug("Dataspace->*****uploading outputs in IO folder*****");
			List<String> outputurls = uploadOutputData(outputData, newcomputationFolder);
			
			AnalysisLogger.getLogger().debug("Dataspace->*****adding properties to the folder*****");
			
			AnalysisLogger.getLogger().debug("Dataspace->creating Folder Properties");

			// write a computation item for the computation
			LinkedHashMap<String, String> properties = new LinkedHashMap<String, String>();
			properties.put(computation_id, computation.id);
			
			properties.put(vre, computation.vre);
			
			properties.put(operator_name, config.getAgent());
			
			properties.put(operator_id, computation.operatorId);
			
			properties.put(operator_description, computation.operatorDescription);
			
			properties.put(start_date, computation.startDate);
			
			properties.put(end_date, computation.endDate);
			
			properties.put(status, getStatus(computation.status));
			
			properties.put(execution_platform, computation.infrastructure);
			
			int ninput = inputurls.size();
			int noutput = outputurls.size();

			AnalysisLogger.getLogger().debug("Dataspace->Adding input properties for " + ninput + " inputs");
			for (int i = 1; i <= ninput; i++) {
				StoredData input = inputData.get(i - 1);
				if (input.payload.contains("|")){
					String payload = input .payload;
					AnalysisLogger.getLogger().debug("Dataspace->Managing complex input "+input.name+" : "+payload);
					//delete the names that are not useful
					
					for (StoredData subinput:inputData){
						if (input.description.equals(subinput.description)){
							payload = payload.replace(subinput.name,subinput.payload);
							subinput.name=null;
						}
					}
					
					input.name = null;
					
					//delete last pipe character
					if (payload.endsWith("|"))
						payload = payload.substring(0,payload.length()-1);
					AnalysisLogger.getLogger().debug("Dataspace->Complex input after processing "+payload);
					properties.put("input" + i + "_" + input.description, payload);
					input.payload=payload;
					
				}
			}
			
			for (int i = 1; i <= ninput; i++) {
				StoredData input = inputData.get(i - 1);
				if (input.name!=null){
					properties.put("input" + i + "_" + input.name, inputurls.get(i - 1));
					
				}
			}
			
			AnalysisLogger.getLogger().debug("Dataspace->Adding output properties for " + noutput + " outputs");
			for (int i = 1; i <= noutput; i++) {
				properties.put("output" + i + "_" + outputData.get(i - 1).name, outputurls.get(i - 1));
				
			}

			AnalysisLogger.getLogger().debug("Dataspace->Properties of the folder: " + properties);
			
			AnalysisLogger.getLogger().debug("Dataspace->Saving properties to ProvO XML file " + noutput + " outputs");

			/*
			 * XStream xstream = new XStream(); String xmlproperties = xstream.toXML(properties);
			 */
			try {
				String xmlproperties = ProvOGenerator.toProvO(computation, inputData, outputData);

				File xmltosave = new File(config.getPersistencePath(), "prov_o_" + UUID.randomUUID());
				FileTools.saveString(xmltosave.getAbsolutePath(), xmlproperties, true, "UTF-8");
				InputStream sis = new FileInputStream(xmltosave);
				WorkspaceUtil.createExternalFile(newcomputationFolder, computation.id + ".xml", computation.operatorDescription, sis,null,"text/xml",xmltosave.length());
				sis.close();
				xmltosave.delete();
			} catch (Exception e) {
				AnalysisLogger.getLogger().debug("Dataspace->Failed creating ProvO XML file " + e.getLocalizedMessage());
				AnalysisLogger.getLogger().debug(e);
				e.printStackTrace();
			}
			//List<String> scopes = new ArrayList<String>();
			//scopes.add(config.getGcubeScope());
			//ws.createGcubeItem(computation.id, computation.operatorDescription, scopes, computation.user, itemType, properties, newcomputationFolder.getId());
			newcomputationFolder.getProperties().addProperties(properties);
		}

		AnalysisLogger.getLogger().debug("Dataspace->finished uploading computation data");
	}

	public String buildCompositePayload(List<StoredData> inputData,String payload, String inputName){
		
		for (StoredData input:inputData){
			if (inputName.equals(input.description)){
				payload = payload.replace(input.name,input.payload);
			}
		}
		return payload;
	}
	
	public void writeProvenance(ComputationData computation, List<StoredData> inputData, List<StoredData> outputData) throws Exception {
		AnalysisLogger.getLogger().debug("Dataspace->connecting to Workspace");
		HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
		HomeManager manager = factory.getHomeManager();
		AnalysisLogger.getLogger().debug("Dataspace->getting user");
		User user = manager.createUser(computation.user);
		Home home = manager.getHome(user);
		AnalysisLogger.getLogger().debug("Dataspace->getting root folder");
		Workspace ws = home.getWorkspace();
		WorkspaceFolder root = ws.getRoot();
		AnalysisLogger.getLogger().debug("Dataspace->create folders network");
		createFoldersNetwork(ws, root);
		WorkspaceFolder dataminerItem = (WorkspaceFolder) root.find(dataminerFolder);
		AnalysisLogger.getLogger().debug("Dataspace->****uploading input files****");
		uploadInputData(inputData, dataminerItem);
		AnalysisLogger.getLogger().debug("Dataspace->****uploading output files****");
		uploadOutputData(outputData, dataminerItem);
		AnalysisLogger.getLogger().debug("Dataspace->****uploading computation files****");
		uploadComputationData(computation, inputData, outputData, dataminerItem, ws);
		AnalysisLogger.getLogger().debug("Dataspace->provenance management finished");
		AnalysisLogger.getLogger().debug("Dataspace->deleting generated files");
		AbstractEcologicalEngineMapper.deleteGeneratedFiles(generatedFiles);
		AnalysisLogger.getLogger().debug("Dataspace->generated files deleted");
	}

	public void writeRunningComputationData() throws Exception {
		try {
			deleteRunningComputationData();
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("Dataspace->impossible to delete running computation");
		}
		// AnalysisLogger.getLogger().debug("Dataspace->updating computation status");
		// AnalysisLogger.getLogger().debug("Dataspace->connecting to Workspace");
		HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
		HomeManager manager = factory.getHomeManager();
		// AnalysisLogger.getLogger().debug("Dataspace->getting user");
		User user = manager.createUser(computation.user);
		Home home = manager.getHome(user);
		// AnalysisLogger.getLogger().debug("Dataspace->getting root folder");
		Workspace ws = home.getWorkspace();
		WorkspaceFolder root = ws.getRoot();
		// AnalysisLogger.getLogger().debug("Dataspace->create folders network");
		createFoldersNetwork(ws, root);
		WorkspaceFolder dataminerFolderWS = (WorkspaceFolder) root.find(dataminerFolder);
		WorkspaceItem computationsFolderItem = dataminerFolderWS.find(computationsFolder);
		// AnalysisLogger.getLogger().debug("Dataspace->Creating computation item " + computation.id+" with status"+computation.status);
		String itemType = "COMPUTATION";

		// write a computation item for the computation
		LinkedHashMap<String, String> properties = new LinkedHashMap<String, String>();
		properties.put(computation_id, computation.id);
		properties.put(vre, computation.vre);
		properties.put(operator_name, config.getAgent());
		properties.put(operator_description, computation.operatorDescription);
		properties.put(operator_id, computation.operatorId);
		properties.put(start_date, computation.startDate);
		properties.put(end_date, computation.endDate);
		properties.put(status, getStatus(computation.status));
		properties.put(execution_platform, computation.infrastructure);
		if (computation.exception != null && computation.exception.length() > 0)
			properties.put(error, computation.exception);

		List<String> scopes = new ArrayList<String>();
		scopes.add(config.getGcubeScope());
		ws.createGcubeItem(computation.id, computation.operatorDescription, scopes, computation.user, itemType, properties, computationsFolderItem.getId());

		AnalysisLogger.getLogger().debug("Dataspace->finished uploading computation data");
	}

	public String getStatus(String status){
		double statusD = 0;
		try{
			statusD = Double.parseDouble(status);
		}catch(Exception e){
			return status;
		}
		
		if (statusD==100)
			return "completed";
		else if (statusD==-2)
			return "error";
		else if (statusD==-1)
			return "cancelled";
		else
			return status;
	}
	
	public void deleteRunningComputationData() throws Exception {
		AnalysisLogger.getLogger().debug("Dataspace->deleting computation item");
		AnalysisLogger.getLogger().debug("Dataspace->connecting to Workspace");
		HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
		HomeManager manager = factory.getHomeManager();
		AnalysisLogger.getLogger().debug("Dataspace->getting user");
		User user = manager.createUser(computation.user);
		Home home = manager.getHome(user);
		AnalysisLogger.getLogger().debug("Dataspace->getting root folder");
		Workspace ws = home.getWorkspace();
		WorkspaceFolder root = ws.getRoot();
		WorkspaceFolder dataminerFolderWS = (WorkspaceFolder) root.find(dataminerFolder);
		WorkspaceItem computationsFolderItem = dataminerFolderWS.find(computationsFolder);
		AnalysisLogger.getLogger().debug("Dataspace->removing computation data");
		WorkspaceFolder computationsFolderWs = ((WorkspaceFolder) computationsFolderItem);
		WorkspaceItem wi = computationsFolderWs.find(computation.id);
		if (wi!=null){
			AnalysisLogger.getLogger().debug("Dataspace->Found "+computation.id+" under "+computationsFolderWs.getName()+" - removing");
			wi.remove();
		}
		else
			AnalysisLogger.getLogger().debug("Dataspace->Warning Could not find "+computation.id+" under "+computationsFolderWs.getName());
		
		int maxtries = 3;
		int i =1;
		while (ws.exists(computation.id,computationsFolderWs.getId()) && i<maxtries){
			AnalysisLogger.getLogger().debug("Dataspace->computation data still exist... retrying "+i);
			Thread.sleep(1000);
			computationsFolderWs.find(computation.id).remove();
			i++;
		}
		
		AnalysisLogger.getLogger().debug("Dataspace->finished removing computation data - success "+!ws.exists(computation.id,computationsFolderWs.getId()));
	}

}
