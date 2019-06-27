package org.acme;
import org.gcube.common.uri.ap.ScopedPropertyAP;
import org.junit.Test;

public class ScopedPropertyDPTest {

	
	@Test
	public void resolverAuthority() {
		
		new ScopedPropertyAP().authorityIn("d4science.research-infrastructures.eu");
		
		
	}
}
