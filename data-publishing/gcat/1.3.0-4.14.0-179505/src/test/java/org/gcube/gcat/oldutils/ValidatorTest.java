package org.gcube.gcat.oldutils;

import org.gcube.gcat.ContextTest;
import org.junit.Test;

public class ValidatorTest extends ContextTest {
	
	@Test
	public void createGroupAsSysAdmin() throws Exception {
		String groupName = "Italian";
		Validator validator = new Validator();
		validator.createGroupAsSysAdmin(groupName);
	}
}
