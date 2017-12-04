package org.gcube.accounting.insert.rstudio.plugin;

import java.util.HashMap;
import java.util.Map;

import org.gcube.accounting.insert.rstudio.plugin.AccountingInsertRstudioPlugin;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Tests {

	/**
	 * Logger
	 */	
	private static Logger logger = LoggerFactory.getLogger(Tests.class);

	@Before
	public void beforeTest(){

	}

	@Test
	public void testLaunch() throws Exception {

		//SecurityTokenProvider.instance.set("36501a0d-a205-4bf1-87ad-4c7185faa0d6-98187548");
		SecurityTokenProvider.instance.set("3acdde42-6883-4564-b3ba-69f6486f6fe0-98187548");
		
		//FOR DEBUG
		String scopeDebug="/gcube";
		ScopeProvider.instance.set(scopeDebug);
		// END FOR DEBUG

		Map<String, Object> inputs = new HashMap<String, Object>(); 	
		//type aggregation

		/*optional*/
		//inputs.put("urlService","http://socialnetworking-d-d4s.d4science.org/social-networking-library-ws/rest/");
		inputs.put("dataServiceClass","content-management");
		inputs.put("dataServiceName","storage-manager");
		inputs.put("dataServiceId","identifier");
		inputs.put("uri","Rstudio");
		inputs.put("dataType","STORAGE");
		inputs.put("unitVolume","Kilobyte");
		inputs.put("pathFile","/home/pieve/home_disk_space");



		AccountingInsertRstudioPlugin plugin = new AccountingInsertRstudioPlugin(null);
		plugin.launch(inputs);
		logger.debug("-------------- launch test finished");

	}

	@After
	public void after(){

	}
}
