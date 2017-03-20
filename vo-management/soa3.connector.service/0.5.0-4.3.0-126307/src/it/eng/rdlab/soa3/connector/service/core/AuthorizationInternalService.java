package it.eng.rdlab.soa3.connector.service.core;



public interface AuthorizationInternalService 
{
	
	public boolean authorize (String ticket, String action, String resource);

}
