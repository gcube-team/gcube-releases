package org.acme;

import static java.util.Arrays.*;
import static org.cotrix.domain.dsl.Roles.*;
import static org.cotrix.gcube.extension.PortalRole.*;

import java.util.Collection;
import java.util.Collections;

import org.cotrix.domain.user.Role;
import org.cotrix.gcube.extension.DefaultRoleMapper;
import org.cotrix.gcube.extension.RoleMapper;
import org.cotrix.test.ApplicationTest;
import org.junit.Assert;
import org.junit.Test;

public class MappingTest extends ApplicationTest {

	RoleMapper mapper = new DefaultRoleMapper();
	
	@Test
	public void mapEmpty() {
	
		Collection<Role> roles = mapper.map(Collections.<String>emptySet());
		
		Assert.assertTrue(roles.contains(USER));
			
	}
	
	@Test
	public void mapVOAdmin() {
	
		Collection<Role> roles = mapper.map(asList(VO_ADMIN.value));
		
		Assert.assertTrue(roles.contains(VO_ADMIN.internal));
			
	}
	
	@Test
	public void mapVREManager() {
	
		Collection<Role> roles = mapper.map(asList(VRE_MANAGER.value));
		
		Assert.assertTrue(roles.contains(VRE_MANAGER.internal));
			
	}
	
	

}
