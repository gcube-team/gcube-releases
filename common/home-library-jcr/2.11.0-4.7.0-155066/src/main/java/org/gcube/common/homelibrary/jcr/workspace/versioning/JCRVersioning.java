package org.gcube.common.homelibrary.jcr.workspace.versioning;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.gcube.common.homelibary.model.servlet.ServletParameter;
import org.gcube.common.homelibary.model.versioning.WorkspaceVersion;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.repository.ServletName;
import org.gcube.common.homelibrary.jcr.workspace.usermanager.JCRUserManager;
import org.gcube.common.homelibrary.jcr.workspace.util.TokenUtility;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class JCRVersioning {
	private Logger logger = LoggerFactory.getLogger(JCRUserManager.class);

	Map<String, Endpoint> servlets;

	private String portalLogin;

	public JCRVersioning(String portalLogin){
		super();
		this.servlets = JCRRepository.servlets;
		this.portalLogin = portalLogin;
	}


	@SuppressWarnings("unchecked")
	public List<WorkspaceVersion> getVersionHistory(String itemId) throws InternalErrorException {

		List<WorkspaceVersion> list = null;
		GetMethod getMethod = null;
		try {
			HttpClient httpClient = new HttpClient();            

			//			logger.info(url);

			getMethod =  new GetMethod(servlets.get(ServletName.VERSION_HISTORY).uri().toString() + "?" + ServletParameter.ID + "=" + itemId + "&" + ServletParameter.PORTAL_LOGIN + "=" + portalLogin);
			//			System.out.println(servlets.get(ServletName.VERSION_HISTORY).uri().toString() + "?" + ServletParameter.ID + "=" + id + "&" + ServletParameter.PORTAL_LOGIN + "=" + portalLogin);

			TokenUtility.setHeader(getMethod);
			httpClient.executeMethod(getMethod);

			logger.trace("Calling List Version History Servlet");
			XStream xstream = new XStream();

			//			System.out.println(getMethod.getResponseBodyAsString());
			list = (List<WorkspaceVersion>) xstream.fromXML(getMethod.getResponseBodyAsString());

		} catch (Exception e) {
			logger.error("Error retrieving version history for node " + itemId, e);
			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}

		return list;
	}



	public void saveCurrentVersion(String id, String remotePath) throws InternalErrorException {
		logger.trace("Calling Save Current Version Servlet");

		GetMethod getMethod = null;
		try {
			HttpClient httpClient = new HttpClient();            

			//System.out.println(servlets.get(ServletName.SAVE_CURRENT_VERSION).uri().toString() + "?" + ServletParameter.ID + "=" + id + "&" + ServletParameter.REMOTE_PATH + "=" + URLEncoder.encode(remotePath, "UTF-8") + "&" + ServletParameter.PORTAL_LOGIN + "=" + portalLogin);
			getMethod =  new GetMethod(servlets.get(ServletName.SAVE_CURRENT_VERSION).uri().toString() + "?" + ServletParameter.ID + "=" + id + "&" + ServletParameter.REMOTE_PATH + "=" + URLEncoder.encode(remotePath, "UTF-8") + "&" + ServletParameter.PORTAL_LOGIN + "=" + portalLogin);

			TokenUtility.setHeader(getMethod);
			int response =  httpClient.executeMethod(getMethod);

			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);


		} catch (Exception e) {
			logger.error("Error saving current version for node " + id, e);
			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}

	}



	public void restoreVersion(String id, String remotePath, String version) throws InternalErrorException {
		logger.trace("Calling Restore Version History Servlet");

		GetMethod getMethod = null;
		try {
			HttpClient httpClient = new HttpClient();            

			getMethod =  new GetMethod(servlets.get(ServletName.RESTORE_VERSION).uri().toString() + "?" + ServletParameter.ID + "=" + id + "&" +  ServletParameter.REMOTE_PATH + "=" + URLEncoder.encode(remotePath, "UTF-8") + "&" + ServletParameter.VERSION + "=" + version + "&" + ServletParameter.PORTAL_LOGIN + "=" + portalLogin);

			TokenUtility.setHeader(getMethod);
			int response =  httpClient.executeMethod(getMethod);

			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);


		} catch (Exception e) {
			logger.error("Error restoring version for node " + id, e);
			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}

	}

	public void removeVersion(String id, String remotePath, String versionID) throws InternalErrorException {
		logger.trace("Calling Remove Version History Servlet");

		GetMethod getMethod = null;
		try {
			HttpClient httpClient = new HttpClient();            

			getMethod =  new GetMethod(servlets.get(ServletName.REMOVE_VERSION).uri().toString() + "?" + ServletParameter.ID + "=" + id + "&" + ServletParameter.VERSION + "=" + versionID + "&" + ServletParameter.PORTAL_LOGIN + "=" + portalLogin);

			TokenUtility.setHeader(getMethod);
			int response =  httpClient.executeMethod(getMethod);

			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);


		} catch (Exception e) {
			logger.error("Error removing versioning for node " + id, e);
			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}

	}


	public WorkspaceVersion getVersion(String itemID, String versionID) throws InternalErrorException {
		GetMethod getMethod = null;
		WorkspaceVersion version;
		try {
			HttpClient httpClient = new HttpClient();            

			getMethod =  new GetMethod(servlets.get(ServletName.GET_VERSION).uri().toString() + "?" + ServletParameter.ID + "=" + itemID + "&" + ServletParameter.VERSION + "=" + versionID +  "&" + ServletParameter.PORTAL_LOGIN + "=" + portalLogin);

			TokenUtility.setHeader(getMethod);
			int response =  httpClient.executeMethod(getMethod);

			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			XStream xstream = new XStream();
			version = (WorkspaceVersion) xstream.fromXML(getMethod.getResponseBodyAsString());

		} catch (Exception e) {
			logger.error("Error getting versioning for node " + itemID, e);
			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return version;
	}


	public WorkspaceVersion getCurrentVersion(String id) throws InternalErrorException {
		GetMethod getMethod = null;
		WorkspaceVersion version;
		try {
			HttpClient httpClient = new HttpClient();            

			getMethod =  new GetMethod(servlets.get(ServletName.GET_CURRENT_VERSION).uri().toString() + "?" + ServletParameter.ID + "=" + id + "&" + ServletParameter.PORTAL_LOGIN + "=" + portalLogin);
			//			System.out.println(servlets.get(ServletName.GET_CURRENT_VERSION).uri().toString() + "?" + ServletParameter.ID + "=" + id + "&" + ServletParameter.PORTAL_LOGIN + "=" + portalLogin);

			TokenUtility.setHeader(getMethod);
			int response =  httpClient.executeMethod(getMethod);

			// Check response code
			if (response != HttpStatus.SC_OK)
				throw new HttpException("Received error status " + response);

			XStream xstream = new XStream();
			version = (WorkspaceVersion) xstream.fromXML(getMethod.getResponseBodyAsString());

		} catch (Exception e) {
			logger.error("Error getting versioning for node " + id, e);
			throw new InternalErrorException(e);
		} finally {
			if(getMethod != null)
				getMethod.releaseConnection();
		}
		return version;
	}


	public InputStream downloadVersion(String id, String version) throws InternalErrorException, IOException {
		InputStream inputStream;
		String fileURL = servlets.get(ServletName.DOWNLOAD_VERSION).uri().toString() + "?" + ServletParameter.ID + "=" + id + "&" + ServletParameter.VERSION + "=" + version + "&" + ServletParameter.PORTAL_LOGIN + "=" + portalLogin;
		//		System.out.println(fileURL);
		URL url = new URL(fileURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		TokenUtility.setHeader(httpConn);
		int responseCode = httpConn.getResponseCode();

		// check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {

			// opens input stream from the HTTP connection
			inputStream = httpConn.getInputStream();

			return inputStream;

		} else {
			//			System.out.println("No file to download. Server replied HTTP code: " + responseCode);
			throw new InternalErrorException("No file to download. Server replied HTTP code: " + responseCode);
		}
		//		httpConn.disconnect();
	}




}
