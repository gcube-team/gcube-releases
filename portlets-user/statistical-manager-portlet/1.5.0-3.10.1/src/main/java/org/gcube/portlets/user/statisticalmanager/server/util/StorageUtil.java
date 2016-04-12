package org.gcube.portlets.user.statisticalmanager.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.contentmanager.storageclient.model.protocol.smp.SMPURLConnection;

public class StorageUtil {

	private static Logger logger = Logger.getLogger(StorageUtil.class);

	public static InputStream getStorageClientInputStream(String url)
			throws Exception {
		/*
		 * try { logger.debug("Get ImputStream from: "+url); SMPUrl smsHome =
		 * new SMPUrl(url); logger.debug("smsHome: [host:" + smsHome.getHost() +
		 * " path:" + smsHome.getPath() + " ref:" + smsHome.getRef() +
		 * " userinfo:" + smsHome.getUserInfo() + " ]"); URLConnection uc =
		 * (URLConnection) smsHome.openConnection(); InputStream is =
		 * uc.getInputStream(); return is;
		 * 
		 * } catch (Exception e) {
		 * logger.error("Error retrieving imput stream from storage: ", e);
		 * e.printStackTrace(); throw new Exception(
		 * "Error retrieving imput stream from storage: " +
		 * e.getLocalizedMessage(), e); }
		 */

		try {

			URL u = new URL(null, url, new URLStreamHandler() {

				@Override
				protected URLConnection openConnection(URL u)
						throws IOException {

					return new SMPURLConnection(u);
				}
			});
			return u.openConnection().getInputStream();

		} catch (Throwable e) {
			logger.error("Error in StorageUtil: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
			
		}

		// String [] urlParam=url.split("\\?");
		// // String param=urlParam[1];
		// logger.info("String encrypted "+urlParam[1]);
		// String param=new StringEncrypter("DES").decrypt(urlParam[1]);
		// logger.info("String decrypted: "+param);
		// String [] getParam=param.split("\\&");
		// String serviceClass=null;
		// String serviceName=null;
		// String owner=null;
		// String accessType=null;
		// String scopeType=null;
		// AccessType type = null;
		// String server= null;
		// String [] par1;
		// for(String par : getParam){
		// if(par.contains("ServiceClass")){
		// par1=par.split("=");
		// serviceClass=par1[1];
		// }else if(par.contains("ServiceName")){
		// par1=par.split("=");
		// serviceName=par1[1];
		// }else if(par.contains("owner")){
		// par1=par.split("=");
		// owner=par1[1];
		// }else if(par.contains("scope")){
		// par1=par.split("=");
		// scopeType=par1[1];
		// }else if(par.contains("server")){
		// par1=par.split("=");
		// server=par1[1];
		// }else if(par.contains("AccessType")){
		// par1=par.split("=");
		// accessType=par1[1];
		// if(accessType.equalsIgnoreCase("public")){
		// type=type.PUBLIC;
		// }else if(accessType.equalsIgnoreCase("shared")){
		// type=type.SHARED;
		// }
		// }else{
		// ;
		// }
		// }
		// if((serviceName==null) || (serviceClass==null) || (owner == null) ||
		// (scopeType==null) || (type == null))
		// throw new MalformedURLException();
		// String location=extractLocation(urlParam[0]);
		//
		// logger.trace("IStanzio Storage con parametri "+serviceClass+" "+serviceName+" "+owner+" "+type+" "+scopeType+
		// " location: "+urlParam[0]);
		// IClient client=new StorageClient(serviceClass, serviceName, owner,
		// type, scopeType).getClient();
		// InputStream is=null;
		// is=client.get().RFileAStream(location);
		// // createFileTest(is);
		// return is;
	}

	public static String extractLocation(String url) {
		String[] loc = url.split("//");
		// logger.trace("url extracted: " + loc[1]);
		return loc[1];
	}

	public static String getFileName(String url) {
		String[] urlParam = url.split("\\?");
		String location = extractLocation(urlParam[0]);

		try {
			return location.split("/")[1];
		} catch (Exception e) {
			// e.printStackTrace();
			return location;
		}
	}

	/**
	 * @return
	 */
	public static Map<String, String> getFilesUrlFromFolderUrl(
			String serviceClass, String serviceName, String url,
			String username, String scope) throws Exception {

		Home home = HomeLibrary.getHomeManagerFactory().getHomeManager()
				.getHome(username);
		Map<String, String> map = new LinkedHashMap<String, String>();
		Workspace ws = home.getWorkspace();
		WorkspaceItem folderItem = ws.getItemByPath(url);
		// logger.trace("Type of workspace item is : "
		// + folderItem.getType().toString());
		WorkspaceFolder folder = (WorkspaceFolder) folderItem;
		List<WorkspaceItem> childrenList = folder.getChildren();
		for (WorkspaceItem item : childrenList) {
			ExternalImage file = (ExternalImage) item;
			String name = item.getName();
			String absoluteUrlFile = file.getPublicLink();

			map.put(name, absoluteUrlFile);
		}

		return map;
	}
	// public static Map<String, String> getFilesUrlFromFolderUrl(String
	// serviceClass, String serviceName, String url, String username, String
	// scope) throws Exception {
	// IClient client = new StorageClient(
	// serviceClass,
	// serviceName,
	// username,
	// AccessType.SHARED,
	// scope).getClient();
	//
	// Map<String, String> map = new LinkedHashMap<String, String>();
	// List<StorageObject> dir = client.showDir().RDir(url);
	// for (StorageObject storageObj: dir) {
	// String name = storageObj.getName();
	// String relativeUrlFile = url+"/"+name;
	// String absoluteUrlFile = client.getUrl().RFile(relativeUrlFile);
	// map.put(name, absoluteUrlFile);
	// }
	// return map;
	// }

}
