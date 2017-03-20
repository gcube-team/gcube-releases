package org.gcube.common.authorization.library.policies;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class User extends UserEntity {
	
	protected User() {
		super();
	}

	protected User(String identifier) {
		super(identifier);
	}
		
	protected User(List<String> excludes) {
		super(excludes);
	}
	
	@Override
	public UserEntityType getType() {
		return UserEntityType.USER;
	}

	@Override
	public boolean isSubsetOf(UserEntity entity) {
		if (entity.getType()== UserEntityType.USER)
			return entity.getIdentifier() ==null || this.getIdentifier().equals(entity.getIdentifier()); 
		else return false;
	}

	
}
