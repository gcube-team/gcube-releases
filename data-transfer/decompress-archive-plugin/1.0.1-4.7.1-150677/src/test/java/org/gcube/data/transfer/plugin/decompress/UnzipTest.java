package org.gcube.data.transfer.plugin.decompress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.plugins.decompress.DecompressPluginFactory;

public class UnzipTest {

	public static void main(String[] args) throws IOException {
//		DecompressPlugin.unzip(new File("/home/fabio/workspaces/home-library-jcr-PRE-PROD.jar"), "/home/fabio/workspaces/unzips/hl", true);
		
		
		
		
		Map<String,String> params=new HashMap<>();
		params.put(DecompressPluginFactory.DESTINATION_PARAMETER, "here");
		params.put(DecompressPluginFactory.SOURCE_PARAMETER, urlToFile("https://goo.gl/r5jFZ9"));
		PluginInvocation invocation=new PluginInvocation("", params);
		
		
		new DecompressPluginFactory().createWorker(invocation).execute();
		
		
		
		
	}

	
	public static String urlToFile(String urlString) throws IOException{
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();
		InputStream in = connection.getInputStream();
		File outputFile=File.createTempFile("archive_", ".zip");
		FileOutputStream fos = new FileOutputStream(outputFile);
		byte[] buf = new byte[512];
		while (true) {
		    int len = in.read(buf);
		    if (len == -1) {
		        break;
		    }
		    fos.write(buf, 0, len);
		}
		in.close();
		fos.flush();
		fos.close();
		return outputFile.getAbsolutePath();
	}
	
}
