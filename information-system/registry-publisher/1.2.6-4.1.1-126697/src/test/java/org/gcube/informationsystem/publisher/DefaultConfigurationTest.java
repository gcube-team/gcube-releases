package org.gcube.informationsystem.publisher;

import java.util.List;
import static org.junit.Assert.*;
import org.gcube.informationsystem.publisher.scope.IValidatorContext;
import org.gcube.informationsystem.publisher.scope.ScopeValidatorScanner;
import org.gcube.informationsystem.publisher.scope.Validator;
import org.junit.Test;

public class DefaultConfigurationTest {
	
	@Test
	public void testDefaultValidator(){
		IValidatorContext context=ScopeValidatorScanner.provider();
		List<Validator> list= context.getValidators();
		assertNotNull(list);
		Validator validator =list.get(0);
		System.out.println("found validator: "+validator.type());
		assertEquals(validator.type().toString().trim(),"class org.gcube.common.resources.gcore.Resource");
	}

}
