package org.gcube.datatransfer.uriresolver.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.junit.Test;

public class TestURI {
	
	@Test
	public void testGetInputStreamFromSMP() throws IOException{
		Handler.activateProtocol();
		URL url = new URL("smp://Wikipedia_logo_silver.png?5ezvFfBOLqaqBlwCEtAvz4ch5BUu1ag3yftpCvV+gayz9bAtSsnO1/sX6pemTKbDe0qbchLexXeWgGcJlskYE8td9QSDXSZj5VSl9kdN9SN0/LRYaWUZuP4Q1J7lEiwkU4GKPsiD6PDRVcT4QAqTEy5hSIbr6o4Y");
		Assert.assertNotNull(url.openConnection().getInputStream());
		
	}
	
	@Test
	public void testGetInputStreamFromHTTP() throws IOException{
		
		URL url = new URL("http://localhost:8080/uri-resolver/smp?smp-uri=smp://Wikipedia_logo_silver.png?5ezvFfBOLqaqBlwCEtAvz4ch5BUu1ag3yftpCvV+gayz9bAtSsnO1/sX6pemTKbDe0qbchLexXeWgGcJlskYE8td9QSDXSZj5VSl9kdN9SN0/LRYaWUZuP4Q1J7lEiwkU4GKPsiD6PDRVcT4QAqTEy5hSIbr6o4Y");
		FileOutputStream out = new FileOutputStream(new File("/Users/andrea/test1"));
		InputStream in = url.openConnection().getInputStream();
		IOUtils.copy(in, out);
		out.close();
		in.close();
	}
	

}
