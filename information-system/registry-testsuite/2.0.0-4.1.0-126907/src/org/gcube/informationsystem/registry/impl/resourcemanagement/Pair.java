package org.gcube.informationsystem.registry.impl.resourcemanagement;

import org.gcube.informationsystem.registry.impl.state.ProfileResource;

/**
 * 
 * Maintain a {@link ProfileResource} and related lifetime association
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class Pair {
	
	public long lifetime;
	public ProfileResource  resource; 
	
	public Pair(long lifetime, ProfileResource resource){
		this.lifetime = lifetime;
		this.resource = resource;
	}
	
	public boolean equals(Object o){
		Pair objectCouple = (Pair) o;
		return objectCouple.resource.equals(this.resource);
		
	}
	
}