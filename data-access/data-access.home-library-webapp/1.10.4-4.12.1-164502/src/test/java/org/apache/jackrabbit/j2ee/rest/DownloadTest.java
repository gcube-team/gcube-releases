package org.apache.jackrabbit.j2ee.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.scope.api.ScopeProvider;

public class DownloadTest {

	//	private static final String HTTP_URL_REPOSITORY = "http://ws-repo-test.d4science.org/home-library-webapp";
	private static final int BUFFER_SIZE = 4096;
	private static final String HTTP_URL_REPOSITORY = "http://node11.d.d4science.research-infrastructures.eu:8080/home-library-webapp";
//	public static final String GCUBE_TOKEN 				= "gcube-token";
	
	
	public static void main(String[] args) throws Exception {
		SecurityTokenProvider.instance.set("8920abf2-54e5-4e35-82ae-abd31dca65c2-98187548");
//		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		Workspace workspace = HomeLibrary.getHomeManagerFactory().getHomeManager().getHome().getWorkspace();
		WorkspaceItem item = workspace.getItemByPath("/Home/valentina.marioli/Workspace/MyFolder/");
		System.out.println(item.getPath());
//		System.out.println(workspace.getRoot().getPath());
		
//		String absPath = "/Home/valentina.marioli/Workspace/MyFolder/";
//		String saveDir = "/home/valentina/Downloads/subfolder";
//
//		String fileURL = HTTP_URL_REPOSITORY + "/rest/Download?" + "absPath=" + absPath;
//		testDownload(fileURL, saveDir);

	}

	private static void testDownload(String fileURL, String saveDir) throws IOException {

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
			String saveFilePath = saveDir + File.separator + fileName;

			// opens an output stream to save into file
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();

			System.out.println("File downloaded");
		} else {
			System.out.println("No file to download. Server replied HTTP code: " + responseCode);
		}
		httpConn.disconnect();

}


}
