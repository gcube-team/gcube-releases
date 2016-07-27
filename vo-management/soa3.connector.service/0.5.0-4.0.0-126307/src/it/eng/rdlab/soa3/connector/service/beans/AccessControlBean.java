package it.eng.rdlab.soa3.connector.service.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Utility bean carrying the access control information
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class AccessControlBean 
{
	private String 	username,
					ticket;
	
	private long 	sessionStart,
					sessionEnd;
	private Map<String, List<String>> permittedOperations;
	private Map<String, List<String>> deniedOperations;
	private List<String> roles;
	private boolean rolesLoaded;
	
	public AccessControlBean ()
	{
		this.permittedOperations = new HashMap<String, List<String>> ();
		this.deniedOperations = new HashMap<String, List<String>>();
		this.roles = new ArrayList<String>();
		this.rolesLoaded = false;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public long getSessionStart() {
		return sessionStart;
	}
	public void setSessionStart(long sessionStart) {
		this.sessionStart = sessionStart;
	}
	public long getSessionEnd() {
		return sessionEnd;
	}
	public void setSessionEnd(long sessionEnd) {
		this.sessionEnd = sessionEnd;
	}
	
	public void addPermittedOperation (String service, String operation)
	{
		List<String> operationList = this.permittedOperations.get(service);
		
		if (operationList == null)
		{
			operationList = new ArrayList<String>();
			this.permittedOperations.put(service, operationList);
		}
		
		operationList.add(operation);
	}
	
	public void addDeniedOperation (String service, String operation)
	{
		List<String> operationList = this.deniedOperations.get(service);
		
		if (operationList == null)
		{
			operationList = new ArrayList<String>();
			this.deniedOperations.put(service, operationList);
		}
		
		operationList.add(operation);
	}
	
	
	public boolean isPermitted (String service, String operation)
	{
		List<String> operations = this.permittedOperations.get(service);
		
		if (operations == null) return false;
		else return operations.contains(operation);
		
	}
	
	public boolean isDenied (String service, String operation)
	{
		List<String> operations = this.deniedOperations.get(service);
		
		if (operations == null) return false;
		else return operations.contains(operation);
		
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	
	public List<String> getRoles ()
	{
		return this.roles;
	}

	public boolean isRolesLoaded() {
		return rolesLoaded;
	}

	public void setRolesLoaded(boolean rolesLoaded) {
		this.rolesLoaded = rolesLoaded;
	}

	
	
}
