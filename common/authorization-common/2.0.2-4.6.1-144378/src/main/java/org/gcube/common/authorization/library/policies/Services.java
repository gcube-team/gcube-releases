package org.gcube.common.authorization.library.policies;

import java.util.Arrays;

public class Services {

	public static ServiceEntity all(){
		return new ServiceEntity(new ServiceAccess());
	}
	
	public static ServiceEntity specialized(ServiceAccess serviceAccess){
		return new ServiceEntity(serviceAccess);
	}
	
	public static ServiceEntity allExcept(ServiceAccess ... serviceAccesses){
		return new ServiceEntity(Arrays.asList(serviceAccesses));
	}
}
