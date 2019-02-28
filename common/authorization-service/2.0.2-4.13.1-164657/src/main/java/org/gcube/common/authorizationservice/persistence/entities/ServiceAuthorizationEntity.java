package org.gcube.common.authorizationservice.persistence.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.gcube.common.authorization.library.provider.ServiceInfo;

@Entity
@DiscriminatorValue(EntityConstants.SERVICE_AUTHORIZATION)
@NamedQueries({
	@NamedQuery(name="Service.get", query="SELECT info FROM ServiceAuthorizationEntity info WHERE  "
		+ " info.token=:token AND info.id.context=:context AND info.id.clientId=:clientId")
})
public class ServiceAuthorizationEntity extends AuthorizationEntity{
		
	@SuppressWarnings("unused")
	private ServiceAuthorizationEntity(){}
	
	public ServiceAuthorizationEntity(String token, String context , String qualifier ,
			 ServiceInfo info, String generatedBy) {
		super(token, context, info, qualifier, EntityConstants.SERVICE_AUTHORIZATION, generatedBy);

	}

		
}
