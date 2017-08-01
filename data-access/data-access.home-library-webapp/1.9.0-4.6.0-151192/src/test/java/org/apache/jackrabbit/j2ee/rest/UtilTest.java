package org.apache.jackrabbit.j2ee.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.Map;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;

import com.thoughtworks.xstream.XStream;

public class UtilTest {

	private static final String VRE_PATH = "/Workspace/MySpecialFolders/";
	private static final String HOME = "Home";
	private static final String SEPARATOR = "/";
	private static final Object MY_SPECIAL_FOLDER = "MySpecialFolders";
	
	private static final String HTTP_URL_REPOSITORY = "http://node11.d.d4science.research-infrastructures.eu:8080/home-library-webapp";
//	private static final String HTTP_URL_REPOSITORY = "http://workspace-repository.d4science.org:8080/home-library-webapp";
	private static final String SAVE_DIR = "/home/valentina/Downloads/subfolder";
	private static final int BUFFER_SIZE = 4096;

	public static String uploadFile(File file, String name, String description, String parentPath) throws Exception {

		byte[] image = Files.readAllBytes(file.toPath());

		XStream xstream = new XStream();
		String uri = HTTP_URL_REPOSITORY + "/rest/Upload?" + "name=" + name+ "&description=" + URLEncoder.encode(description, "UTF-8") + "&parentPath=" + URLEncoder.encode(parentPath, "UTF-8");

		URL url = new URL(uri);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type", "image/jpeg");
		connection.setRequestMethod("POST");
		TokenUtility.setHeader(connection);

		// Write file to response.
		OutputStream output = connection.getOutputStream();
		output.write(image);
		output.close();

		BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = r.readLine()) != null) {
			response.append(inputLine);
		}

		String xmlOut = response.toString();
//		System.out.println("File " +  name + " upload in folder " + parentPath );
		return (String) xstream.fromXML(xmlOut);
	}


	public static void download(String path) throws IOException {


		String fileURL = HTTP_URL_REPOSITORY + "/rest/Download?" + "absPath=" + path;

		System.out.println(fileURL);
		URL url = new URL(fileURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		//		httpConn.setRequestProperty(GCUBE_TOKEN, SecurityTokenProvider.instance.get());
		TokenUtility.setHeader(httpConn);
		int responseCode = httpConn.getResponseCode();

		// always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {
			String fileName = "";
			String disposition = httpConn.getHeaderField("Content-Disposition");
			String contentType = httpConn.getContentType();
			int contentLength = httpConn.getContentLength();

			if (disposition != null) {
				// extracts file name from header field
				int index = disposition.indexOf("filename=");
				if (index > 0) {
					fileName = disposition.substring(index + 10,
							disposition.length() - 1);
				}
			} else {
				// extracts file name from URL
				fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
						fileURL.length());
			}

			System.out.println("Content-Type = " + contentType);
			System.out.println("Content-Disposition = " + disposition);
			System.out.println("Content-Length = " + contentLength);
			System.out.println("fileName = " + fileName);

			// opens input stream from the HTTP connection
			InputStream inputStream = httpConn.getInputStream();
			String saveFilePath = SAVE_DIR + File.separator + fileName;

			// opens an output stream to save into file
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();

			System.out.println("File downloaded: " + saveFilePath);
		} else {
			System.out.println("No file to download. Server replied HTTP code: " + responseCode);
		}
		httpConn.disconnect();

	}


	public static String createFolder(String name, String description,
			String parentPath) throws Exception {

		System.out.println("Create Folder " + name);
		String uri = HTTP_URL_REPOSITORY + "/rest/CreateFolder?" +"name=" + name+ "&description=" + URLEncoder.encode(description, "UTF-8") + "&parentPath=" + URLEncoder.encode(parentPath, "UTF-8");
		System.out.println(uri);
		URL url = new URL(uri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Accept", "application/xml");
		TokenUtility.setHeader(connection);

		int responseCode = connection.getResponseCode();
		//		System.out.println("\nSending 'POST' request to URL : " + url);
		//		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		XStream xstream = new XStream();

		BufferedReader in = new BufferedReader(
				new InputStreamReader(connection.getInputStream()));

		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		String xmlOut = response.toString();

		connection.disconnect();
		return (String) xstream.fromXML(xmlOut);

	}

	
	public static String getPublicLink(String path, String isShortUrl) throws Exception {

		System.out.println("Get Public Link for file " + path);
		String uri = HTTP_URL_REPOSITORY + "/rest/GetPublicLink?" +"absPath=" + URLEncoder.encode(path, "UTF-8")+ "&shortUrl="+isShortUrl;
		System.out.println(uri);
		URL url = new URL(uri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Accept", "application/xml");
		TokenUtility.setHeader(connection);

		int responseCode = connection.getResponseCode();
		//		System.out.println("\nSending 'POST' request to URL : " + url);
		//		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		XStream xstream = new XStream();

		BufferedReader in = new BufferedReader(
				new InputStreamReader(connection.getInputStream()));

		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		String xmlOut = response.toString();

		connection.disconnect();
		return (String) xstream.fromXML(xmlOut);

	}


	@SuppressWarnings("unchecked")
	public static Map<String, Boolean> listFolder(String absPath) throws Exception {

		System.out.println("Calling servlet List folder " + absPath);
		XStream xstream = new XStream();
		String xmlOut = null;
		Map<String, Boolean> list = null;
		try{
			String uri = HTTP_URL_REPOSITORY + "/rest/List?"+ "absPath=" + URLEncoder.encode(absPath, "UTF-8");
			System.out.println(uri);
			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/xml");
			TokenUtility.setHeader(connection);

			int responseCode = connection.getResponseCode();
			//		System.out.println("\nSending 'GET' request to URL : " + uri);
			System.out.println("Response Code : " + responseCode);



			BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));

			StringBuffer response = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			xmlOut = response.toString();
			//			System.out.println(xmlOut);

			connection.disconnect();

			list = (Map<String, Boolean>) xstream.fromXML(xmlOut);

		} catch (ClassCastException e) {
			throw new Exception(e.getMessage());
		} 

		return list;

	}
	
	public static Boolean delete(String absPath) throws Exception{
		XStream xstream = null;
		String xmlOut = null;
		Boolean flag = false;
		try{
			String uri = HTTP_URL_REPOSITORY + "/rest/Delete?"+ "absPath=" +  URLEncoder.encode(absPath, "UTF-8") ;
			System.out.println(uri);
			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/xml");
			TokenUtility.setHeader(connection);


			int responseCode = connection.getResponseCode();
			//			System.out.println("\nSending 'POST' request to URL : " + url);
			//			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);

			xstream = new XStream();

			BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));

			StringBuffer response = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			xmlOut = response.toString();
			System.out.println(xmlOut);

			connection.disconnect();


			flag = (Boolean) xstream.fromXML(xmlOut);


		} catch (ClassCastException e) {

			throw new Exception(e.getMessage());
		} 

		return flag;

	}
	
	
	public static String cleanPath(Workspace workspace, String absPath) throws ItemNotFoundException, InternalErrorException {
		String myVRE = null;
		String longVRE = null;
		
		String [] splitPath = absPath.split(SEPARATOR);
		if(absPath.contains(VRE_PATH) && (!splitPath[splitPath.length-1].equals(MY_SPECIAL_FOLDER))){
					
			if (splitPath[1].equals(HOME))
				myVRE = splitPath[5];
			else
				myVRE = splitPath[3];

			java.util.List<WorkspaceItem> vres = workspace.getMySpecialFolders().getChildren();
			for (WorkspaceItem vre: vres){
				if (vre.getName().endsWith(myVRE)){
					longVRE = vre.getName();
					break;
				} 
			}

			if (longVRE!=null)
				absPath = absPath.replace(myVRE, longVRE);
		}
		return absPath;

	}
}
