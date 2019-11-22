package org.gcube.spatial.data.sdi.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.spatial.data.sdi.engine.RoleManager;
import org.gcube.spatial.data.sdi.engine.impl.RoleManagerImpl;
import org.gcube.spatial.data.sdi.model.credentials.AccessType;
import org.gcube.spatial.data.sdi.model.credentials.Credentials;

public class RoleManagerTests {


	public static void main(String[] args) {
		RoleManager manager=new RoleManagerImpl();
		
		List<Credentials> toFilter=new ArrayList<Credentials>();
		toFilter.add(new Credentials("admin","adminpwd",AccessType.ADMIN));
		toFilter.add(new Credentials("manager","managerpwd",AccessType.CONTEXT_MANAGER));
		toFilter.add(new Credentials("user","userpwd",AccessType.CONTEXT_USER));
		toFilter.add(new Credentials("ckan","ckanPWD",AccessType.CKAN));
		
		
//		System.out.println(manager.getMostAccessible(toFilter, false));
//		System.out.println(manager.getMostAccessible(toFilter, true));
//		
		System.out.println(manager.getMostAccessible(Arrays.asList(
				new Credentials("user","pwd",AccessType.CONTEXT_USER),
				new Credentials("ckan","ckanPWD",AccessType.CKAN)), true));
	}

}
