package org.gcube.common.authorization.library.binder;


import org.gcube.common.authorization.library.policies.Role;
import org.gcube.common.authorization.library.policies.Roles;
import org.gcube.common.authorization.library.policies.ServiceAccess;
import org.gcube.common.authorization.library.policies.User;
import org.gcube.common.authorization.library.policies.Users;
import org.junit.Assert;
import org.junit.Test;

public class SubsetTest {

	@Test
	public void serviceAccess(){
		ServiceAccess sa1 = new ServiceAccess();
		ServiceAccess sa2 = new ServiceAccess("C1");
		ServiceAccess sa3 = new ServiceAccess("S1", "C1");
		ServiceAccess sa4 = new ServiceAccess("S1", "C1", "I1");
		ServiceAccess sa5 = new ServiceAccess("S1", "C1", "I1");
		ServiceAccess sa6 = new ServiceAccess("S1", "C1", "I2");
		ServiceAccess sa7 = new ServiceAccess("S2", "C1", "I1");
		ServiceAccess sa8 = new ServiceAccess("S2", "C2", "I1");
		
		Assert.assertTrue(sa2.isSubsetOf(sa1));
		Assert.assertFalse(sa1.isSubsetOf(sa2));
		Assert.assertTrue(sa3.isSubsetOf(sa1));
		Assert.assertTrue(sa3.isSubsetOf(sa2));
		
		Assert.assertTrue(sa4.isSubsetOf(sa1));
		Assert.assertTrue(sa4.isSubsetOf(sa2));
		Assert.assertTrue(sa4.isSubsetOf(sa3));
		Assert.assertTrue(sa4.isSubsetOf(sa4));
		Assert.assertTrue(sa4.isSubsetOf(sa5));
		
		Assert.assertTrue(sa5.isSubsetOf(sa4));
		Assert.assertFalse(sa5.isSubsetOf(sa6));
		Assert.assertFalse(sa7.isSubsetOf(sa3));
		Assert.assertFalse(sa8.isSubsetOf(sa2));
		Assert.assertFalse(sa8.isSubsetOf(sa3));
		Assert.assertTrue(sa8.isSubsetOf(sa1));
		
	}
	
	@Test
	public void users(){
		User u1 = Users.one("u1");
		User u2 = Users.one("u2");
		User u3 = Users.all();
		
		Assert.assertTrue(u1.isSubsetOf(u3));
		Assert.assertTrue(u2.isSubsetOf(u3));
		Assert.assertFalse(u1.isSubsetOf(u2));
		Assert.assertFalse(u2.isSubsetOf(u1));
		
		Role r1 = Roles.one("VREManager");
		Role r2 = Roles.allExcept("VREManager");
		Role r3 = Roles.allExcept("VOManager");
		Role r4 = Roles.allExcept("VOManager", "VREManager");
		
		Assert.assertTrue(r1.isSubsetOf(r3));
		Assert.assertFalse(r2.isSubsetOf(r3));
		Assert.assertTrue(r2.isSubsetOf(r4));
		Assert.assertTrue(r3.isSubsetOf(r4));
		Assert.assertFalse(r4.isSubsetOf(r3));
		
	}
}
