package org.apache.jackrabbit.j2ee.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;

import com.thoughtworks.xstream.XStream;

public class UploadTest {

	private static final String HTTP_URL_REPOSITORY = "http://node11.d.d4science.research-infrastructures.eu:8080/home-library-webapp";
	private static final String DEFAULT_IMAGE = "pluto.jpg";
	private static final String ROOT_PATH = "/Home/valentina.marioli/Workspace/MyFolder";

	public static void main(String[] args) throws Exception {

		SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");
		URL imageURL = UploadTest.class.getClassLoader().getResource(DEFAULT_IMAGE);
		File file = new File(imageURL.getFile());

		byte[] image = Files.readAllBytes(file.toPath());
		System.out.println(uploadFile(image, DEFAULT_IMAGE, "my description", ROOT_PATH));

	}


	public static String uploadFile(byte[] in, String name, String description, String parentPath) throws Exception {
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
		output.write(in);
		output.close();

		BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = r.readLine()) != null) {
			response.append(inputLine);
		}

		String xmlOut = response.toString();
		return (String) xstream.fromXML(xmlOut);
	}

}
