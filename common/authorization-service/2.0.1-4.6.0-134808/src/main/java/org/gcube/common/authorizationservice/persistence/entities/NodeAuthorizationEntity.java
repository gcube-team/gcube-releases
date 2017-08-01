package org.gcube.common.authorizationservice.persistence.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.gcube.common.authorization.library.provider.ContainerInfo;

@Entity
@DiscriminatorValue(EntityConstants.CONTAINER_AUTHORIZATION)
public class NodeAuthorizationEntity extends AuthorizationEntity{
	
	@SuppressWarnings("unused")
	private NodeAuthorizationEntity(){}
	
	public NodeAuthorizationEntity(String token, String context, String qualifier ,
			 ContainerInfo containerInfo, String generatedBy) {
		super(token, context, containerInfo, qualifier, EntityConstants.CONTAINER_AUTHORIZATION, generatedBy==null?"UNKNOWN":generatedBy);
	}


}
