package org.gcube.data.transfer.common.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;

import junit.framework.Assert;

import org.gcube.data.transfer.common.TransferUtil;
import org.junit.Test;

public class TransferUtilTest {
	
	//@Test
	public void testTransfer() throws Exception{
		
		TransferUtil util = new TransferUtil();
		
		util.setConnectiontimeout(1000000);
		util.setTransferTimeout(1000000000);
		
		File outFile = File.createTempFile("temp","data-transfer");
		
		URI file = new URI ("https://dl.dropboxusercontent.com/u/12809149/geoserver-GetCoverage.image.asc");
	
		util.performTransfer(file, outFile.toString());
		
		Assert.assertNotNull(outFile);

	}
	
	//@Test
	public void testInputStream() throws Exception{
		
		int timeout = 10000000;

		URI file = new URI ("https://dl.dropboxusercontent.com/u/12809149/geoserver-GetCoverage.image.asc");
		
		Assert.assertNotNull(TransferUtil.getInputStream(file,timeout));
		
	}
	
	@Test
	public void testInputStreamRedirect() throws Exception{
		
		int timeout = 10000000;

		URI file = new URI ("http://goo.gl/l4tEmd");
		
		Assert.assertNotNull(TransferUtil.getInputStream(file,timeout));
		
		BufferedReader input = new BufferedReader(new InputStreamReader(TransferUtil.getInputStream(file,120000)));
		
		System.out.println(input.ready());
		System.out.println(input.readLine());
	}
	
	//@Test
	public void testFollowRedirect() throws Exception{
		
		TransferUtil util = new TransferUtil();
		
		File file = File.createTempFile("temp","data-transfer");
		
		util.setConnectiontimeout(1000000);
		util.setTransferTimeout(1000000000);
		
		URI fileInput = new URI ("http://goo.gl/l4tEmd");
		util.performTransfer(fileInput, file.toString());
		
		Assert.assertNotNull(file);
		
	}
	
	
	
}
