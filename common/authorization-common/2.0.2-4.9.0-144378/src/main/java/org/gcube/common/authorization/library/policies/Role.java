package org.gcube.common.authorization.library.policies;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Role extends UserEntity {
	
	
	protected Role() {
		super();
	}

	protected Role(String identifier) {
		super(identifier);
	}
	
	protected Role(List<String> excludes) {
		super(excludes);
	}

	@Override
	public UserEntityType getType() {
		return UserEntityType.ROLE;
	}

	@Override
	public boolean isSubsetOf(UserEntity entity) {
		if (entity.getType()==UserEntityType.ROLE){
			if (this.getIdentifier()==null)
				return entity.getIdentifier()==null && entity.getExcludes().containsAll(this.getExcludes());
			else {
				if (entity.getIdentifier()!=null)
					return entity.getIdentifier().equals(this.getIdentifier());
				else 
					return !entity.getExcludes().contains(this.getIdentifier());
			}
			
		} else return entity.getIdentifier()==null;
	}

}
