package org.gcube.contentmanager.storageserver.accounting;

import static org.junit.Assert.*;

import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Before;
import org.junit.Test;

public class ProviderUriTest {

	ReportAccountingImpl report;
	private static final String scope="/d4science.research-infrastructures.eu";
	
	@Before
	public void init(){
		
			report = new ReportAccountingImpl();
			ScopeProvider.instance.set(scope);
	}
	
	@Test
	public void test(){
			String providerUri= report.buildProviderURI(scope);
			if(scope.contains("d4science.research-infrastructures.eu"))
				assertEquals(providerUri, "data.d4science.org");
			else if(scope.contains("gcube"))
				assertEquals(providerUri, "data.gcube.org");
			else
				System.out.println("provider uri for scope: "+scope+" is "+providerUri);
	}

}
