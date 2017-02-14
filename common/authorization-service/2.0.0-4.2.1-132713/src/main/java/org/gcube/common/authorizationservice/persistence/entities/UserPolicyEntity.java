package org.gcube.common.authorizationservice.persistence.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import org.gcube.common.authorization.library.policies.Action;
import org.gcube.common.authorization.library.policies.Roles;
import org.gcube.common.authorization.library.policies.ServiceAccess;
import org.gcube.common.authorization.library.policies.UserEntity;
import org.gcube.common.authorization.library.policies.UserEntity.UserEntityType;
import org.gcube.common.authorization.library.policies.Users;

@Entity
@DiscriminatorValue(EntityConstants.USER_POLICY)
@NamedQuery(name="UserPolicy.get", query=EntityQueries.USER_POLICY_GET)
public class UserPolicyEntity extends PolicyEntity{

	@Transient
	private UserEntity userClient;
	
	@Column(nullable=true)
	@Enumerated(EnumType.ORDINAL)
	private UserEntityType type;
	
	@ElementCollection
	private List<String> excludes;
	
	@Column(nullable=true)
	private String identifier;
	
	protected UserPolicyEntity() {}

	public UserPolicyEntity(String context, ServiceAccess serviceAccess, UserEntity user, Action action) {
		super(context, serviceAccess, EntityConstants.USER_POLICY, action);
		this.userClient = user; 
		this.type = user.getType();
		if (user.getIdentifier()!=null){
			this.identifier = user.getIdentifier();
			this.excludeType= ExcludeType.NOTEXCLUDE;
		}
		else {
			this.excludes = user.getExcludes();
			this.excludeType= ExcludeType.EXCLUDE;
		}
	}

	public UserEntity getUser() {
		if (type==UserEntityType.USER){
			if (this.excludeType == ExcludeType.NOTEXCLUDE)
				return Users.one(identifier);
			else return Users.allExcept(excludes.toArray(new String[excludes.size()]));
		} else {
			if (this.excludeType == ExcludeType.NOTEXCLUDE)
				return Roles.one(identifier);
			else return Roles.allExcept(excludes.toArray(new String[excludes.size()]));
			
		}
	}

	@Override
	public boolean isRewritable() {
		return this.type==UserEntityType.ROLE;
	}

	@Override
	public String toString() {
		return "UserPolicyEntity [userClient=" + userClient + ", type=" + type
				+ ", excludes=" + excludes + ", identifier=" + identifier + "]";
	}
	
	
}
