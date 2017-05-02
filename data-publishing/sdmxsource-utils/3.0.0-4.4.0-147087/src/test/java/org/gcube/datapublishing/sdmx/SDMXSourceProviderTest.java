package org.gcube.datapublishing.sdmx;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sdmxsource.sdmx.api.manager.output.StructureWriterManager;
import org.sdmxsource.sdmx.api.manager.parse.StructureParsingManager;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class SDMXSourceProviderTest {

	@Inject
	StructureParsingManager parsingMgr;
	
	@Inject
	StructureWriterManager writerMgr;
	
	@Test
	public void testAutoInjection() {
		Assert.assertNotNull(parsingMgr);
		Assert.assertNotNull(writerMgr);
	}
	
	public void testManualInjection(){
		SDMXSourceProvider provider = new SDMXSourceProvider();
		Assert.assertNotNull(provider.getStructureParsingManager());
		Assert.assertNotNull(provider.getStructureWriterManager());
	}

}
