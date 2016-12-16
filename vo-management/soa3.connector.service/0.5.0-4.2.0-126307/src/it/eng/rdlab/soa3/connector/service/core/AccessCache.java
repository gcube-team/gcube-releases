package it.eng.rdlab.soa3.connector.service.core;

import it.eng.rdlab.soa3.connector.service.beans.AccessControlBean;

/**
 * 
 * Manages cached access entry objects
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public interface AccessCache 
{
	/**
	 * 
	 * @param id
	 * @return
	 */
	public AccessControlBean getAccessGrantEntry (String id);
	
	/**
	 * 
	 * @param id
	 */
	public void removeAccessGrantEntry (String id);
	
	/**
	 * 
	 * @param id
	 * @param bean
	 */
	public void setAccessGrantEntry (String id, AccessControlBean bean);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public long getAccessDeniedEntry (String id);
	
	/**
	 * 
	 * @param id
	 */
	public void removeAccessDeniedEntry (String id);
	
	/**
	 * 
	 * @param id
	 * @param expirationTime
	 */
	public void setAccessDeniedEntry (String id, long expirationTime);
}
