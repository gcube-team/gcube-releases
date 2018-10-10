package org.gcube.common.authorization.library.policies;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({User.class,Role.class})
public abstract class UserEntity {

	private String identifier = null;

	private List<String> excludes = new ArrayList<String>();

	public enum UserEntityType {
		ROLE , USER, EXTERNALSERVICE
	}

	protected UserEntity() {
		super();
	}

	protected UserEntity(String identifier) {
		this.identifier = identifier;
	}

	protected UserEntity(List<String> excludes) {
		if (excludes==null || excludes.isEmpty()) throw new IllegalArgumentException("list of excludes cannot be empty");
		this.excludes = excludes;
	}


	public String getIdentifier(){
		return identifier;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	public abstract UserEntityType getType();

	public final String getAsString() {
		if (identifier!=null )
			return this.getType()+":"+this.identifier;
		else {
			if (excludes !=null && !excludes.isEmpty())
				return this.getType()+":allExcept"+this.excludes;
			else return this.getType()+":*";
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((excludes == null) ? 0 : excludes.hashCode());
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserEntity other = (UserEntity) obj;
		if (excludes == null) {
			if (other.excludes != null)
				return false;
		} else if (!excludes.equals(other.excludes))
			return false;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserEntity [ "+getAsString()+" ]";
	}

	public abstract boolean isSubsetOf(UserEntity entity);


}
