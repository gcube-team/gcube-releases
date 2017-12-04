package org.gcube.accounting.aggregator.workspace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.gcube.accounting.aggregator.plugin.AccountingAggregatorPluginDeclaration;
import org.gcube.accounting.aggregator.utility.Utility;
import org.gcube.accounting.aggregator.workspace.HTTPCall.HTTPMETHOD;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alessandro Pieve (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class WorkSpaceManagement {

	public static Logger logger = LoggerFactory.getLogger(WorkSpaceManagement.class);

	private static final String ZIP_SUFFIX = ".zip";
	private static final String ZIP_FILE_DESCRIPTION = "Backup of original records deleted and aggregtaed records inserted.";
	private static final String ZIP_MIMETYPE = "application/zip, application/octet-stream";
	
	protected static final GCoreEndpoint gCoreEndpoint;
	protected static final Map<String, String> restEndpointMap;
	
	protected static final String CLASS_FORMAT = "$resource/Profile/ServiceClass/text() eq '%1s'";
	protected static final String NAME_FORMAT = "$resource/Profile/ServiceName/text() eq '%1s'";
	protected static final String STATUS_FORMAT = "$resource/Profile/DeploymentData/Status/text() eq 'ready'";
	
	protected static final String SERVICE_CLASS = "DataAccess";
	protected static final String SERVICE_NAME = "HomeLibraryWebapp";
	
	public static final String USER_AGENT = AccountingAggregatorPluginDeclaration.NAME;
	
	protected static SimpleQuery queryForHomeLibraryGCoreEndpoint(){
		return ICFactory.queryFor(GCoreEndpoint.class)
				.addCondition(String.format(CLASS_FORMAT, SERVICE_CLASS))
				.addCondition(String.format(NAME_FORMAT, SERVICE_NAME))
				.addCondition(String.format(STATUS_FORMAT))
				.setResult("$resource");
	}
	
	protected static GCoreEndpoint getHomeLibraryGCoreEndpoint(){
		SimpleQuery query = queryForHomeLibraryGCoreEndpoint();
		DiscoveryClient<GCoreEndpoint> client = ICFactory.clientFor(GCoreEndpoint.class);
		List<GCoreEndpoint> gCoreEndpoints = client.submit(query);
		return gCoreEndpoints.get(0);
	}
	
	static {
		gCoreEndpoint = getHomeLibraryGCoreEndpoint();
		Group<Endpoint> endpoints = gCoreEndpoint.profile().endpoints();
		restEndpointMap = new HashMap<>();
		for(Endpoint endpoint : endpoints){
			String endpointName = endpoint.name();
			String endpointURI = endpoint.uri().toString();
			if(endpointURI.contains("rest")){
				restEndpointMap.put(endpointName, endpointURI);
			}
		}
	}
	
	public static void addToZipFile(ZipOutputStream zos, File file) throws Exception {
		
		byte[] buffer = new byte[1024];
		
		FileInputStream in = new FileInputStream(file);
		
		ZipEntry ze = new ZipEntry(file.getName());
		zos.putNextEntry(ze);
		int len;
		while ((len = in.read(buffer)) > 0) {
			zos.write(buffer, 0, len);
		}
		zos.closeEntry();
		in.close();
	}
	
	private static String getZipFileName(String name) throws Exception {
		String zipFileName = String.format("%s%s", name, ZIP_SUFFIX);
		return zipFileName;
	}
	
	public static boolean zipAndBackupFiles(String targetFolder, String name, File... files) throws Exception {

		try {
			String zipFileName = getZipFileName(name);
			
			File zipFile = new File(files[0].getParentFile(), zipFileName);
			zipFile.delete();
			logger.trace("Going to save {} into workspace", zipFile.getAbsolutePath());
			
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
			
			for(File file : files){
				addToZipFile(zos, file);
			}
			
			zos.close();

			
			FileInputStream zipFileStream = new FileInputStream(zipFile);
			
			WorkSpaceManagement.uploadFile(zipFileStream, zipFileName, ZIP_FILE_DESCRIPTION, 
					ZIP_MIMETYPE, targetFolder);
			
			zipFile.delete();
			
			return true;
		} catch (Exception e) {
			logger.error("Error while trying to save a backup file containg aggregated records", e);
			throw e;
		}
	}

	public static String getHome() throws Exception {
		String username = Utility.getUsername();
		return String.format("/Home/%s/Workspace", username);
	}

	/**
	 * Create a Folder name folderName into workspace in the parent folder.
	 * Before creating it check if it already exists. 
	 * If it exist there is no needs to recreate it, so it just return it.  
	 * 
	 * @param parent
	 * @param folderName
	 * @param folderDescription
	 * @return
	 * @throws Exception
	 */
	public static String createFolder(String parentPath, String folderName, String folderDescription)
			throws Exception {
		try {
			HTTPCall httpCall = new HTTPCall(restEndpointMap.get("CreateFolder"), USER_AGENT);
			Map<String, String> parameters = new HashMap<>();
			parameters.put("name", folderName);
			parameters.put("description", folderDescription);
			parameters.put("parentPath", parentPath);
			httpCall.call("", HTTPMETHOD.POST, parameters, null, HTTPCall.CONTENT_TYPE_TEXT_PLAIN);
			return parentPath + "/" + folderName;
		} catch (Exception e) {
			logger.error("Error while creating folder ", e);
			throw e;
		}
	}

	/**
	 * Save a Item on workspace
	 * 
	 * @param user
	 *            of workspace
	 * @param inputStream
	 * @param name
	 * @param description
	 * @param folderId
	 * @throws Exception
	 */
	public static void uploadFile(InputStream inputStream, String name, String description, String mimeType,
			String parentPath) throws Exception {
		try {
			logger.trace("Going to upload file on WorkSpace name:{}, description:{}, mimetype:{}, parentPath:{}", name,
					description, mimeType, parentPath);
			HTTPCall httpCall = new HTTPCall(restEndpointMap.get("Upload"), USER_AGENT);
			Map<String, String> parameters = new HashMap<>();
			parameters.put("name", name);
			parameters.put("description", description);
			parameters.put("parentPath", parentPath);
			
			httpCall.call("", HTTPMETHOD.POST, inputStream, parameters, HTTPCall.CONTENT_TYPE_TEXT_PLAIN);
			
		} catch (Exception e) {
			logger.error("Error while uploading file on WorkSpace", e);
			throw e;
		}
	}
}
