package org.apache.jackrabbit.j2ee.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.util.zip.UnzipUtil;

import com.thoughtworks.xstream.XStream;

public class UploadArchiveRest {

//		private static final String HTTP_URL_REPOSITORY = "http://ws-repo-test.d4science.org/home-library-webapp";
		private static final String HTTP_URL_REPOSITORY = "http://ws-repo-test.d4science.org/home-library-webapp";
//	private static final String HTTP_URL_REPOSITORY = "http://node11.d.d4science.research-infrastructures.eu:8080/home-library-webapp";

	public static void main(String[] args) throws Exception {
		SecurityTokenProvider.instance.set("8920abf2-54e5-4e35-82ae-abd31dca65c2-98187548");
		
		File file = new File("/home/valentina/Downloads/gianpaolo.coro.zip");
		byte[] archive = Files.readAllBytes(file.toPath());
		boolean overwrite = true;
		boolean hard = false;
		
		long startTime = System.currentTimeMillis();
	
		uploadArchive(archive, "Panoramiche-"+UUID.randomUUID().toString(), "/Home/valentina.marioli/Workspace/TestUpload", "UTF-8", overwrite, hard);
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

	public static void uploadArchive(byte[] in, String name, String parentPath, String charset, boolean overwrite, boolean hard) throws Exception {

//		XStream xstream = new XStream();

		String uri = HTTP_URL_REPOSITORY + "/rest/Unzip?" + "name=" + name + "&parentPath=" + URLEncoder.encode(parentPath, "UTF-8") + "&replace="+ overwrite+ "&hardReplace="+ hard;
		System.out.println(uri);
		
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
		output.write(in);
		output.close();

		BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = r.readLine()) != null) {
			response.append(inputLine);
		}

		String xmlOut = response.toString();
		System.out.println(xmlOut);
//		return (String) xstream.fromXML(xmlOut);
	}

}
