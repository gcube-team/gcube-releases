package org.gcube.informationsystem.exporter;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.informationsystem.exporter.mapper.GenericResourceExporterTest;
import org.gcube.informationsystem.exporter.mapper.ServiceEndpointExporterTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ISExporterPluginTest {
	
	private static Logger logger = LoggerFactory.getLogger(ISExporterPluginTest.class);
	
	@Test
	public void testLaunch() throws ObjectNotFound, Exception{
		String[] tokens = {
				ScopedTest.GCUBE, 
				ScopedTest.GCUBE_DEVSEC,
				ScopedTest.GCUBE_DEVSEC_DEVVRE,
				ScopedTest.GCUBE_DEVNEXT,
				ScopedTest.GCUBE_DEVNEXT_NEXTNEXT
		};
		
		for(String token : tokens){
			logger.info("\n\n\n-------------------------------------------------------------------------");
			ScopedTest.setContext(token);
			ISExporterPlugin isExporterPlugin = new ISExporterPlugin(new ISExporterPluginDeclaration());
			Map<String, Object> inputs = new HashMap<String, Object>();
			inputs.put(ISExporterPlugin.FILTERED_REPORT, true);
			isExporterPlugin.launch(inputs);
			logger.info("\n\n\n");
		}
	}
	
	//@Test
	public void delAllExported() throws ObjectNotFound, Exception{
		String[] tokens = {
				ScopedTest.GCUBE, 
				ScopedTest.GCUBE_DEVSEC,
				ScopedTest.GCUBE_DEVSEC_DEVVRE,
				ScopedTest.GCUBE_DEVNEXT,
				ScopedTest.GCUBE_DEVNEXT_NEXTNEXT
		};
		
		for(String token : tokens){
			ScopedTest.setContext(token);
			
			GenericResourceExporterTest genericResourceExporterTest = new GenericResourceExporterTest();
			genericResourceExporterTest.del();
			
			ServiceEndpointExporterTest serviceEndpointExporterTest = new ServiceEndpointExporterTest();
			serviceEndpointExporterTest.del();
		}
	}
}
