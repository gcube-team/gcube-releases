package org.gcube.datatransfer.agent.test;

import java.io.IOException;

import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gcube.datatransfer.agent.impl.utils.TransferUtils;
import org.junit.Test;

public class MymeTypeTest {
	
	GCUBEClientLog logger = new GCUBEClientLog(this.getClass());
	
	@Test
	public void testMymeType() throws IOException{
		
		System.out.println(TransferUtils.getMimeType("http://biogeo.ucdavis.edu/data/climate/worldclim/1_4/grid/cur/tmin_2-5m_bil.zip"));
		
	}

	
	@Test
	public void testunzip() throws Exception{
		
		TransferUtils.unzipArchive("/tmp","/tmp/tmin_2-5m_bil.zip");
		
	}
}
