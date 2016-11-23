package org.gcube.common.authorization.library.policies;

import java.util.Arrays;

public class Roles {
	
	public static Role one(String identifier){
		return new Role(identifier);
	}
	
	public static Role allExcept(String ... identifiers){
		return new Role(Arrays.asList(identifiers));
	}
}
