package org.gcube.common.authorization.library.policies;

import java.util.Arrays;


public class Users {

	public static User one(String identifier){
		return new User(identifier);
	}
	
	public static User all(){
		return new User();
	}
	
	public static User allExcept(String ... identifiers){
		return new User(Arrays.asList(identifiers));
	}
		
}
