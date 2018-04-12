package org.gcube.common.authorizationservice.persistence.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.gcube.common.authorization.library.provider.ExternalServiceInfo;

@Entity
@DiscriminatorValue(EntityConstants.EXTERNAL_SERVICE_AUTHORIZATION)
@NamedQueries({
	@NamedQuery(name="ExternalService.get", query="SELECT info FROM ExternalServiceAuthorizationEntity info WHERE  "
		+ " info.token=:token AND info.id.context=:context AND info.id.clientId=:clientId"),
	@NamedQuery(name="ExternalService.getByGenerator", query="SELECT info FROM ExternalServiceAuthorizationEntity info WHERE  "
			+ " info.id.context=:context AND info.internalInfo.generatedBy=:generatorId")
})
public class ExternalServiceAuthorizationEntity extends AuthorizationEntity{

	
	
	@SuppressWarnings("unused")
	private ExternalServiceAuthorizationEntity(){}
	
	public ExternalServiceAuthorizationEntity(String token, String context, String qualifier,
			 ExternalServiceInfo externalServiceInfo, String generatedBy) {
		super(token, context, externalServiceInfo, qualifier, EntityConstants.EXTERNAL_SERVICE_AUTHORIZATION, generatedBy);
	}

	
	
}