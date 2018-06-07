package org.gcube.common.authorization.library.policies;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExternalService extends UserEntity {

	@Override
	public UserEntityType getType() {
		return UserEntityType.EXTERNALSERVICE;
	}

	@Override
	public boolean isSubsetOf(UserEntity entity) {
		if (entity.getType()== UserEntityType.EXTERNALSERVICE)
			return entity.getIdentifier()==null || this.getIdentifier().equals(entity.getIdentifier()); 
		else return false;
	}

}
