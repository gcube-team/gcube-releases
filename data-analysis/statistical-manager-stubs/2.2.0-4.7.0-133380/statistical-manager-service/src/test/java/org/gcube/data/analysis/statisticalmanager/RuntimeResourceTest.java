package org.gcube.data.analysis.statisticalmanager;

import org.gcube.data.analysis.statisticalmanager.persistence.DatabaseType;
import org.gcube.data.analysis.statisticalmanager.persistence.RuntimeResourceManager;
import org.gcube.data.analysis.statisticalmanager.util.ScopeUtils;
import org.gcube.data.analysis.statisticalmanager.util.ScopeUtils.ScopeBean;
import org.junit.Test;

public class RuntimeResourceTest {

	
	/**
	 * @param args
	 * @throws Exception 
	 */
	@Test
	public void test() throws Exception {
		ScopeUtils.setAuthorizationSettings(new ScopeBean("/gcube/devsec",null));
		
//		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		System.out.println("Scope is "+ScopeUtils.getCurrentScope());
		for(DatabaseType type:DatabaseType.values()){
			try{				
				System.err.println("DATABASE TYPE "+type+" "+RuntimeResourceManager.getDatabaseProfile(type));
			}catch(Exception e){
				e.printStackTrace();
			}	
		}
		
		System.err.println("TS DataStore : "+RuntimeResourceManager.getServiceEndpointAsMap("TimeSeriesDataStore"));
		
	}

}
