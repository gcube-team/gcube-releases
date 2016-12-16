package it.eng.rdlab.soa3.connector.service.core;

import it.eng.rdlab.soa3.connector.service.beans.AccessControlBean;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 
 * Access control manager abstract class
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public abstract class AccessControlManager implements AccessCache
{
	private Map<String, AccessControlBean> accessControlMap;
	private Map<String, Long> accessDeniedMap; 
	
	protected AccessControlManager ()
	{
		this.accessControlMap = Collections.synchronizedMap(new WeakHashMap<String, AccessControlBean>()); 
		this.accessDeniedMap = Collections.synchronizedMap(new WeakHashMap<String, Long>()); 
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized AccessControlBean getAccessGrantEntry (String id)
	{
		return this.accessControlMap.get(id);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void removeAccessGrantEntry (String id)
	{
		this.accessControlMap.remove(id);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void setAccessGrantEntry (String id, AccessControlBean bean)
	{
		this.accessControlMap.put(id, bean);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized long getAccessDeniedEntry (String id)
	{
		Long accessDeniedExpiration =  this.accessDeniedMap.get(id);
		
		if (accessDeniedExpiration == null ) accessDeniedExpiration = -1l;
		
		return accessDeniedExpiration;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void removeAccessDeniedEntry (String id)
	{
		this.accessDeniedMap.remove(id);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void setAccessDeniedEntry (String id, long accessDeniedExpiration)
	{
		this.accessDeniedMap.put(id, accessDeniedExpiration);
	}
	
}
