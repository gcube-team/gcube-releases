package org.gcube.spatial.data.sdi.engine.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;
import javax.validation.metadata.ConstraintDescriptor;

import org.gcube.spatial.data.sdi.engine.RoleManager;
import org.gcube.spatial.data.sdi.model.credentials.AccessType;
import org.gcube.spatial.data.sdi.model.credentials.Credentials;
import org.gcube.spatial.data.sdi.model.service.GeoServiceDescriptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class RoleManagerImpl implements RoleManager {

	public RoleManagerImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Credentials getMostAccessible(List<Credentials> toFilter, boolean considerAdmin) {
		
		//need to check roles by contacting social
		AccessType maxLevel=getMaxLevel(considerAdmin);
		
		Credentials toReturn=null;
		for(Credentials cred: toFilter) {
			if(cred.getAccessType().compareTo(maxLevel)>=0) { // cred level 
				if(toReturn==null || cred.getAccessType().compareTo(toReturn.getAccessType())<0)
					toReturn = cred;
			}
		}
		return toReturn;
	}

	@Override
	public <T extends GeoServiceDescriptor> List<T> filterByRole(List<T> toFilter, boolean considerAdmin) {
		ArrayList<T> toReturn=new ArrayList<T>();
		AccessType maxLevel=getMaxLevel(considerAdmin);
		for(T descriptor:toFilter) {
			
		}
		return toReturn;
	}

	
	private AccessType getMaxLevel(boolean considerAdmin) {
		//TOD ask to social manager
		return considerAdmin?AccessType.ADMIN:AccessType.CONTEXT_MANAGER;
	}
	
}
