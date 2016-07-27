package org.gcube.dataanalysis.ewe.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class NetworkUtils {

	private static String EWE_DOWNLOAD_URL = "http://goo.gl/hBc8UN";	

	private String executionId;
	
	public NetworkUtils(String executionId) {
		this.executionId = executionId;
	}

	public URL getEweDownloadUrl() throws MalformedURLException {
		return new URL(EWE_DOWNLOAD_URL);
	}
	
	public void downloadAndUnzipEwe() throws IOException {
		FileSystemUtils fsu = new FileSystemUtils(this.executionId);

		File destination = new File(fsu.getBinariesLocation(), "EwECmd.zip");
		this.download(this.getEweDownloadUrl(), destination);

//		File destination = new File(fsu.getBinariesLocation(), "EwECmd.zip");
//		fsu.copyFile(new File("...."), destination);

		ZipUtils.unzipFile(destination, fsu.getBinariesLocation());
		
		
	}
	
	public void download(URL source, File destination) throws IOException {
		AnalysisLogger.getLogger().debug("Downloading '" + source.toString() + " to " + destination.getAbsolutePath()+"'");
		FileUtils.copyURLToFile(source, destination);
		AnalysisLogger.getLogger().debug("Downloaded.");
	}
	
}
