package org.gcube.data.analysis.tabulardata.statistical;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.apache.commons.io.IOUtils;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.contentmanager.storageclient.model.protocol.smp.SMPURLConnection;
import org.junit.Test;

public class StreamTest {

	static{
		Handler.activateProtocol();
	}
	
	@Test
	public void check() throws IOException, Exception{
		String url="smp://Home/fabio.sinibaldi/Workspace/.applications/StatisticalManager/ResultsMap05 03 2015 16_12_47?5ezvFfBOLqb3YESyI/kesN4T+ZD0mtmc/4sZ0vGMrl0lgx7k85j8o2Q1vF0ezJi/TEYl7d+F4sKR7EwqeONAlQygGb2MgXevPHITz2QgmAJ45OTH73abscF3bRtSi0SU8bcFUbQ4Mwd/XO2bqsokgB5v1H/QUQgN";
		System.out.println("CONTENT TYPE : "+getContentType(url));
		IOUtils.copy(getStorageClientInputStream(url), System.out);
		
	}
	
	
	private static InputStream getStorageClientInputStream(String url) throws Exception{
		URL u = new URL(url);
		return u.openConnection().getInputStream();
	}
	
	private static String getContentType(String url)throws Exception {
		URL u = new URL(url);
		return u.openConnection().getContentType();
	}
}
