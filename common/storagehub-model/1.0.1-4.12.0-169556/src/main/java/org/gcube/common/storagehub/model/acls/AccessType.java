package org.gcube.common.storagehub.model.acls;

public enum AccessType {

	WRITE_ALL("hl:writeAll"),
	ADMINISTRATOR("jcr:all"),
	READ_ONLY("jcr:read"),
	WRITE_OWNER("jcr:write");
	
	private String value;
	
	private AccessType(String value){
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static AccessType fromValue(String value) {
		for (AccessType accessType : AccessType.values())
			if (accessType.value.equals(value))
				return accessType;
		throw new IllegalArgumentException(value+" cannot be mapped to AccessType");
	}
	
}
