package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.dataspace;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.ItemContainer;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.items.GCubeItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.AbstractEcologicalEngineMapper;
import org.n52.wps.commons.WPSConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataspaceManager implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataspaceManager.class);

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
	public static String hostname = "hostname";
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

	public DataspaceManager(AlgorithmConfiguration config, ComputationData computation, List<StoredData> inputData,
			List<StoredData> outputData, List<File> generatedFiles) {
		this.config = config;
		this.computation = computation;
		this.inputData = inputData;
		this.outputData = outputData;
		this.generatedFiles = generatedFiles;
		LOGGER.debug("DataspaceManager [config=" + config + ", computation=" + computation + ", inputData=" + inputData
				+ ", outputData=" + outputData + ", generatedFiles=" + generatedFiles + "]");
	}

	public void run() {
		try {
			LOGGER.debug("Dataspace->Deleting running computation");
			try {
				deleteRunningComputationData();
			} catch (Exception e) {
				LOGGER.debug("Dataspace->No running computation available");
			}
			LOGGER.debug("Dataspace->Writing provenance information");
			writeProvenance(computation, inputData, outputData);
		} catch (Exception e) {
			LOGGER.error("Dataspace-> error writing provenance information ", e);
		}

	}

	public FolderContainer createFoldersNetwork() throws Exception {

		LOGGER.debug("Dataspace->Creating folders for DataMiner");

		StorageHubClient shc = new StorageHubClient();

		FolderContainer root = shc.getWSRoot();

		List<ItemContainer<? extends Item>> dataminerItems = root.findByName(dataminerFolder).getContainers();

		FolderContainer dataminerFolderContainer;

		// manage folders: create the folders network
		if (dataminerItems.isEmpty()) {
			LOGGER.debug("Dataspace->Creating DataMiner main folder");
			dataminerFolderContainer = root.newFolder(dataminerFolder,
					"A folder collecting DataMiner experiments data and computation information");
			// ((WorkspaceFolder)
			// root.find(dataminerFolder)).setSystemFolder(true);
		} else if (dataminerItems.size() > 1)
			throw new Exception("found more than one dataminer folder (impossible!!!)");
		else
			dataminerFolderContainer = (FolderContainer) dataminerItems.get(0);

		if (dataminerFolderContainer.findByName(importedDataFolder).getContainers().isEmpty()) {
			LOGGER.debug("Dataspace->Creating DataMiner imported data folder");
			dataminerFolderContainer.newFolder(importedDataFolder, "A folder collecting DataMiner imported data");
		}

		if (dataminerFolderContainer.findByName(computedDataFolder).getContainers().isEmpty()) {
			LOGGER.debug("Dataspace->Creating DataMiner computed data folder");
			dataminerFolderContainer.newFolder(computedDataFolder, "A folder collecting DataMiner computed data");
		}
		if (dataminerFolderContainer.findByName(computationsFolder).getContainers().isEmpty()) {
			LOGGER.debug("Dataspace->Creating DataMiner computations folder");
			dataminerFolderContainer.newFolder(computationsFolder,
					"A folder collecting DataMiner computations information");
		}

		return dataminerFolderContainer;
	}

	public String uploadData(StoredData data, FolderContainer destinationFolder) throws Exception {
		return uploadData(data, destinationFolder, true);
	}

	public String uploadData(StoredData data, FolderContainer destinationFolder, boolean changename) throws Exception {
		LOGGER.debug("Dataspace->Analysing " + data);
		// String filenameonwsString = WorkspaceUtil.getUniqueName(data.name,
		// wsFolder);
		String filenameonwsString = data.name;
		if (changename)
			filenameonwsString = String.format("%s_(%s)%s", data.name, data.computationId,
					getExtension(data.payload));

		InputStream in = null;
		String url = "";
		try {
			//long size = 0;
			if (data.type.equals("text/csv") || data.type.equals("application/d4science")
					|| data.type.equals("image/png")) {

				if (new File(data.payload).exists() || !data.payload.startsWith("http")) {
					LOGGER.debug("Dataspace->Uploading file {}", data.payload);
					in = new FileInputStream(new File(data.payload));
					//size = new File(data.payload).length();
				} else {
					LOGGER.debug("Dataspace->Uploading via URL {}", data.payload);
					int tries = 10;
					for (int i = 0; i < tries; i++) {
						try {
							URL urlc = new URL(data.payload);
							url = urlc.toString();
							HttpURLConnection urlConnection = (HttpURLConnection) urlc.openConnection();
							urlConnection.setConnectTimeout(10000);
							urlConnection.setReadTimeout(10000);
							in = new BufferedInputStream(urlConnection.getInputStream());
						} catch (Exception ee) {
							LOGGER.warn("Dataspace->Retrying connection to {} number {} ", data.payload, (i + 1), ee);
							in = null;
						}
						if (in != null)
							break;
						else
							Thread.sleep(10000);
					}

				}
				if (in == null)
					throw new Exception("Impossible to open stream from " + data.payload);

				// LOGGER.debug("Dataspace->final file name on ws " +
				// data.name+" description "+data.description);
				LOGGER.debug("Dataspace->WS OP saving the following file on the WS " + filenameonwsString);
				Map<String, Object> properties = new LinkedHashMap<String, Object>();

				properties.put(computation_id, data.computationId);
				properties.put(hostname, WPSConfig.getInstance().getWPSConfig().getServer().getHostname());
				properties.put(vre, data.vre);
				properties.put(creation_date, data.creationDate);
				properties.put(operator, data.operator);
				properties.put(data_id, data.id);
				properties.put(data_description, data.description);
				properties.put(IO, data.provenance.name());
				properties.put(data_type, data.type);
				properties.put(payload, url);

				FileContainer fileContainer = destinationFolder.uploadFile(in, filenameonwsString, data.description);
				fileContainer.setMetadata(new Metadata(properties));

				LOGGER.debug("Dataspace->WS OP file saved on the WS " + filenameonwsString);

				url = fileContainer.getPublicLink().toString();
				LOGGER.debug("Dataspace->WS OP url produced for the file " + url);

				data.payload = url;
				try {
					in.close();
				} catch (Exception e) {
					LOGGER.debug("Dataspace->Error creating file {}", e.getMessage());
					// LOGGER.debug(e);
				}
				LOGGER.debug("Dataspace->File created {}", filenameonwsString);
			} else {
				LOGGER.debug("Dataspace->String parameter {}", data.payload);
				url = data.payload;
			}
		} catch (Throwable e) {
			LOGGER.error("Dataspace->Could not retrieve input payload {} ", data.payload, e);
			// LOGGER.debug(e);
			url = "payload was not made available for this dataset";
			data.payload = url;
		}
		return url;
	}

	public List<String> uploadInputData(List<StoredData> inputData, FolderContainer dataminerFolder) throws Exception {
		LOGGER.debug("Dataspace->uploading input data; Number of data: {}", inputData.size());
		FolderContainer destinationFolder = (FolderContainer) dataminerFolder.findByName(importedDataFolder)
				.getContainers().get(0);
		List<String> urls = new ArrayList<String>();
		for (StoredData input : inputData) {
			List<ItemContainer<? extends Item>> items = null;

			if (input.type.equals("text/csv") || input.type.equals("application/d4science")
					|| input.type.equals("image/png"))
				items = destinationFolder.findByName(input.name).getContainers();

			if (items == null || items.isEmpty()) {
				String url = uploadData(input, destinationFolder, false);
				LOGGER.debug("Dataspace->returning property {}", url);
				urls.add(url);
			} else {
				FileContainer item = (FileContainer) items.get(0);
				LOGGER.debug("Dataspace->Input item {} is already available in the input folder", input.name);
				String url = item.getPublicLink().toString();
				LOGGER.debug("Dataspace->returning WS url {}", url);
				urls.add(url);
			}
		}

		LOGGER.debug("Dataspace->finished uploading input data");
		return urls;
	}

	public List<String> uploadOutputData(List<StoredData> outputData, FolderContainer dataminerFolder)
			throws Exception {
		LOGGER.debug("Dataspace->uploading output data; Number of data: " + outputData.size());
		FolderContainer destinationFolder = (FolderContainer) dataminerFolder.findByName(computedDataFolder)
				.getContainers().get(0);
		List<String> urls = new ArrayList<String>();
		for (StoredData output : outputData) {
			String url = uploadData(output, destinationFolder);
			urls.add(url);
		}
		LOGGER.debug("Dataspace->finished uploading output data");
		return urls;
	}

	public void uploadComputationData(ComputationData computation, List<StoredData> inputData,
			List<StoredData> outputData, FolderContainer dataminerFolder) throws Exception {
		LOGGER.debug("Dataspace->uploading computation data");
		FolderContainer computationContainer = (FolderContainer) dataminerFolder.findByName(computationsFolder)
				.getContainers().get(0);
		// create a folder in here
		LOGGER.debug("Dataspace->Creating computation folder " + computation.id);
		String cfoldername = computation.id;
		FolderContainer newcomputationFolder = null;
		try {
			newcomputationFolder = computationContainer.newFolder(cfoldername, computation.operatorDescription);
		} catch (java.lang.ClassCastException e) {
			LOGGER.debug("Dataspace->concurrency exception - deleting remaining item");
			deleteRunningComputationData();
			newcomputationFolder = computationContainer.newFolder(cfoldername, computation.operatorDescription);
		}
		// String itemType = "COMPUTATION";

		// create IO folders
		LOGGER.debug("Dataspace->creating IO folders under " + cfoldername);
		newcomputationFolder.newFolder(importedDataFolder, importedDataFolder);
		newcomputationFolder.newFolder(computedDataFolder, computedDataFolder);

		// copy IO in those folders
		LOGGER.debug("Dataspace->*****uploading inputs in IO folder*****");
		List<String> inputurls = uploadInputData(inputData, newcomputationFolder);
		LOGGER.debug("Dataspace->*****uploading outputs in IO folder*****");
		List<String> outputurls = uploadOutputData(outputData, newcomputationFolder);

		LOGGER.debug("Dataspace->*****adding properties to the folder*****");

		LOGGER.debug("Dataspace->creating Folder Properties");

		// write a computation item for the computation
		Map<String, Object> properties = new LinkedHashMap<String, Object>();
		properties.put(computation_id, computation.id);

		properties.put(hostname, WPSConfig.getInstance().getWPSConfig().getServer().getHostname());

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

		LOGGER.debug("Dataspace->Adding input properties for " + ninput + " inputs");
		for (int i = 1; i <= ninput; i++) {
			StoredData input = inputData.get(i - 1);
			if (input.payload.contains("|")) {
				String payload = input.payload;
				LOGGER.debug("Dataspace->Managing complex input {} : {}", input.name, payload);
				// delete the names that are not useful

				for (StoredData subinput : inputData) {
					if (input.description.equals(subinput.description)) {
						payload = payload.replace(subinput.name, subinput.payload);
						subinput.name = null;
					}
				}

				input.name = null;

				// delete last pipe character
				if (payload.endsWith("|"))
					payload = payload.substring(0, payload.length() - 1);
				LOGGER.debug("Dataspace->Complex input after processing " + payload);
				properties.put("input" + i + "_" + input.description, payload);
				input.payload = payload;

			}
		}

		for (int i = 1; i <= ninput; i++) {
			StoredData input = inputData.get(i - 1);
			if (input.name != null) {
				properties.put(String.format("input%d_%s", i, input.name), inputurls.get(i - 1));

			}
		}

		LOGGER.debug("Dataspace->Adding output properties for " + noutput + " outputs");
		for (int i = 1; i <= noutput; i++) {
			properties.put(String.format("output%d_%s", i, outputData.get(i - 1).name), outputurls.get(i - 1));

		}

		LOGGER.debug("Dataspace->Properties of the folder: {} ", properties);

		LOGGER.debug("Dataspace->Saving properties to ProvO XML file {} outputs", noutput);

		/*
		 * XStream xstream = new XStream(); String xmlproperties =
		 * xstream.toXML(properties);
		 */
		try {
			String xmlproperties = ProvOGenerator.toProvO(computation, inputData, outputData);

			File xmltosave = new File(config.getPersistencePath(), "prov_o_" + UUID.randomUUID());
			FileTools.saveString(xmltosave.getAbsolutePath(), xmlproperties, true, "UTF-8");
			try (InputStream sis = new FileInputStream(xmltosave)) {
				newcomputationFolder.uploadFile(sis, computation.id + ".xml", computation.operatorDescription);
			}
			xmltosave.delete();
		} catch (Exception e) {
			LOGGER.error("Dataspace->Failed creating ProvO XML file ", e);
		}
		/*
		 * List<String> scopes = new ArrayList<String>();
		 * scopes.add(config.getGcubeScope());
		 * ws.createGcubeItem(computation.id, computation.operatorDescription,
		 * scopes, computation.user, itemType, properties,
		 * newcomputationFolder.getId());
		 */
		newcomputationFolder.setMetadata(new Metadata(properties));

		LOGGER.debug("Dataspace->finished uploading computation data");
	}

	public String buildCompositePayload(List<StoredData> inputData, String payload, String inputName) {

		for (StoredData input : inputData) {
			if (inputName.equals(input.description)) {
				payload = payload.replace(input.name, input.payload);
			}
		}
		return payload;
	}

	public void writeProvenance(ComputationData computation, List<StoredData> inputData, List<StoredData> outputData)
			throws Exception {
		LOGGER.debug("Dataspace->connecting to Workspace");
		LOGGER.debug("Dataspace->create folders network");
		FolderContainer dataminerFolder = createFoldersNetwork();
		LOGGER.debug("Dataspace->****uploading input files****");
		uploadInputData(inputData, dataminerFolder);
		LOGGER.debug("Dataspace->****uploading output files****");
		uploadOutputData(outputData, dataminerFolder);
		LOGGER.debug("Dataspace->****uploading computation files****");
		uploadComputationData(computation, inputData, outputData, dataminerFolder);
		LOGGER.debug("Dataspace->provenance management finished");
		LOGGER.debug("Dataspace->deleting generated files");
		AbstractEcologicalEngineMapper.deleteGeneratedFiles(generatedFiles);
		LOGGER.debug("Dataspace->generated files deleted");
	}

	public void writeRunningComputationData() throws Exception {
		try {
			deleteRunningComputationData();
		} catch (Exception e) {
			LOGGER.debug("Dataspace->impossible to delete running computation : {} ", e.getMessage());
		}
		
		// LOGGER.debug("Dataspace->create folders network");
		FolderContainer folderContainer = createFoldersNetwork();

		FolderContainer computationsContainer = (FolderContainer) folderContainer.findByName(computationsFolder)
				.getContainers().get(0);
		// LOGGER.debug("Dataspace->Creating computation item " +
		// computation.id+" with status"+computation.status);
		String itemType = "COMPUTATION";

		// write a computation item for the computation
		Map<String, Object> properties = new LinkedHashMap<String, Object>();
		properties.put(computation_id, computation.id);
		properties.put(hostname, WPSConfig.getInstance().getWPSConfig().getServer().getHostname());
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
		GCubeItem gcubeItem = new GCubeItem();
		gcubeItem.setName(computation.id);
		gcubeItem.setDescription(computation.operatorDescription);
		gcubeItem.setScopes(scopes.toArray(new String[scopes.size()]));
		gcubeItem.setItemType(itemType);
		gcubeItem.setMetadata(new Metadata(properties));
		gcubeItem.setCreator(AuthorizationProvider.instance.get().getClient().getId());

		computationsContainer.newGcubeItem(gcubeItem);

		LOGGER.debug("Dataspace->finished uploading computation data");
	}

	public String getStatus(String status) {
		double statusD = 0;
		try {
			statusD = Double.parseDouble(status);
		} catch (Exception e) {
			return status;
		}

		if (statusD == 100)
			return "completed";
		else if (statusD == -2)
			return "error";
		else if (statusD == -1)
			return "cancelled";
		else
			return status;
	}

	public void deleteRunningComputationData() throws Exception {

		LOGGER.debug("Dataspace->deleting computation item");
		LOGGER.debug("Dataspace->connecting to Workspace");
		StorageHubClient shc = new StorageHubClient();
		FolderContainer dataminerContainer = (FolderContainer) shc.getWSRoot().findByName(dataminerFolder)
				.getContainers().get(0);
		FolderContainer computationContainer = (FolderContainer) dataminerContainer.findByName(computationsFolder)
				.getContainers().get(0);
		LOGGER.debug("Dataspace->removing computation data");

		List<ItemContainer<? extends Item>> wi = computationContainer.findByName(computation.id).getContainers();
		if (wi.isEmpty()) {
			for (ItemContainer<? extends Item> container : wi)
				container.delete();
		} else
			LOGGER.debug("Dataspace->Warning Could not find {} under {}", computation.id,
					computationContainer.get().getName());

		List<ItemContainer<? extends Item>> fileComputations = computationContainer.findByName(computation.id)
				.getContainers();

		if (fileComputations.size() > 0)
			fileComputations.get(0).delete();

		/*
		 * TODO: ASK GIANPAOLO int maxtries = 3; int i =1; while
		 * (ws.exists(computation.id,computationsFolderWs.getId()) &&
		 * i<maxtries){
		 * LOGGER.debug("Dataspace->computation data still exist... retrying "+i
		 * ); Thread.sleep(1000);
		 * computationsFolderWs.find(computation.id).remove(); i++; }
		 */

		LOGGER.debug("Dataspace->finished removing computation data ");
	}

	// TODO
	public static String getExtension(String payload) {
		LOGGER.debug("DataSpace->Get Extension from: " + payload);
		String extension="";
		if (payload.toLowerCase().startsWith("http")) {
			try {
				URL obj = new URL(payload);
				URLConnection conn = obj.openConnection();
				// get all headers
				Map<String, List<String>> map = conn.getHeaderFields();
				for (Map.Entry<String, List<String>> entry : map.entrySet()) {
					String value = entry.getValue().toString();
					LOGGER.debug("Header value: " + value);
					if (value.toLowerCase().contains("filename")) {
						LOGGER.debug("DataSpace->Searching in http header: found " + value);
						extension = value.substring(value.lastIndexOf("."), value.lastIndexOf("\""));
						break;
					}
				}
				conn.getInputStream().close();
			} catch (Exception e) {
				LOGGER.warn("DataSpace->Error in the payload http link ", e);
			}
		} else {
			File paylFile = new File(payload);
			if (paylFile.exists()) {
				String paylname = paylFile.getName();
				extension = paylname.substring(paylname.lastIndexOf("."));
			}
		}
		LOGGER.debug("DataSpace->Extension retrieved: " + extension);
		return extension;
	}

}
