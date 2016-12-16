package org.gcube.common.authorizationservice.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Entity")
@XmlAccessorType(XmlAccessType.FIELD)
public class AllowedEntity {

	public enum EntityType {
		IP, ROLE, USER
	}

	@XmlAttribute
	private EntityType type;
	
	@XmlAttribute
	private String value;
	
	protected AllowedEntity(){}
	
	public AllowedEntity(EntityType type, String value) {
		super();
		this.type = type;
		this.value = value;
	}

	public EntityType getType() {
		return type;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		AllowedEntity other = (AllowedEntity) obj;
		if (type != other.type)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AllowedEntity [type=" + type + ", value=" + value + "]";
	}
	
}
