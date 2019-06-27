/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.server.csv;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class CSVTargetRegistry {
	
	protected static CSVTargetRegistry instance;
	
	public static final CSVTargetRegistry getInstance()
	{
		if (instance == null) instance = new CSVTargetRegistry();
		return instance;
	}
	
	protected Logger logger = LoggerFactory.getLogger(CSVTargetRegistry.class);
	
	protected Map<String, CSVTarget> registry = new HashMap<String, CSVTarget>();
	
	/**
	 * Adds a new {@link CSVTarget} to the registry.
	 * @param target the {@link CSVTarget} to add.
	 */
	public void add(CSVTarget target)
	{
		logger.trace("add target: "+target.getId());
		CSVTarget old = registry.put(target.getId(), target);
		if (old!=null) logger.warn("A CSVTarget instance with id "+old.getId()+" and class "+old.getClass().getCanonicalName()+" was already registered. The old one has been replaced by the new one with class "+target.getClass().getCanonicalName()+".");
	}
	
	/**
	 * Retrieves the specified {@link CSVTarget}.
	 * @param targetId the {@link CSVTarget} id.
	 * @return the {@link CSVTarget} if found, <code>null</code> otherwise.
	 */
	public CSVTarget get(String targetId)
	{
		return registry.get(targetId);
	}
	
	public boolean exists(String targetId)
	{
		return registry.containsKey(targetId);
	}
	
	/**
	 * Removes the specified {@link CSVTarget}.
	 * @param targetId the {@link CSVTarget} id.
	 */
	public void remove(String targetId)
	{
		logger.trace("remove targetId: "+targetId);
		registry.remove(targetId);
	}

}
