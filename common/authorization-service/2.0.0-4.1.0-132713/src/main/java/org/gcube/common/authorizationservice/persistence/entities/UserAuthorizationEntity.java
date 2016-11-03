package org.gcube.common.authorizationservice.persistence.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.gcube.common.authorization.library.provider.UserInfo;

@Entity
@DiscriminatorValue(EntityConstants.USER_AUTHORIZATION)
@NamedQueries({
	@NamedQuery(name="User.get", query="SELECT info FROM UserAuthorizationEntity info WHERE  "
		+ " info.token=:token AND info.id.context=:context AND info.id.clientId = :clientId")
})
public class UserAuthorizationEntity extends AuthorizationEntity {
	
	
	@SuppressWarnings("unused")
	private UserAuthorizationEntity(){}
	
	public UserAuthorizationEntity(String token, String context , String qualifier ,
			UserInfo info) {
		super(token, context, info, qualifier, EntityConstants.USER_AUTHORIZATION );
	}

	
}
