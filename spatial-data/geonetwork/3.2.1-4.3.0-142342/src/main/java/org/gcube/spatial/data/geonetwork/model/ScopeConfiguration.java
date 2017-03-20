package org.gcube.spatial.data.geonetwork.model;

import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class ScopeConfiguration {

	
	public static String NOT_ASSIGNED="N/A";
	
	private String assignedScope; 
	private Integer publicGroup;
	private Integer privateGroup;
	private Map<Account.Type,Account> accounts;
	private Integer defaultGroup;
		
	
	public void addAccount(Account toAdd){
		accounts.put(toAdd.getType(),toAdd);
	}
}
