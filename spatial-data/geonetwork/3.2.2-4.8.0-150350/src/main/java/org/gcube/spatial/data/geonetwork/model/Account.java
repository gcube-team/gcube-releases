package org.gcube.spatial.data.geonetwork.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class Account {

	public static enum Type{
		CKAN,SCOPE
	}
	
	private String user;
	private String password;
	private Type type;
	@Override
	public String toString() {
		return "Account [user=" + user + ", password=" + "***" + ", type="
				+ type + "]";
	}
	
	
}
