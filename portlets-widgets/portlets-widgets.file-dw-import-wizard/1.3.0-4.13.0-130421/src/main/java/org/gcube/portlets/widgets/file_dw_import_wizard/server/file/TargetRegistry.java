/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.server.file;

import java.util.HashMap;
import java.util.Map;




public class TargetRegistry {
	
	protected static TargetRegistry instance;
	
	public static final TargetRegistry getInstance()
	{
		if (instance == null) instance = new TargetRegistry();
		return instance;
	}
	
	
	protected Map<String, Target> registry = new HashMap<String, Target>();
	
	/**
	 * Adds a new {@link CSVTarget} to the registry.
	 * @param target the {@link CSVTarget} to add.
	 */
	public void add(Target target)
	{
//		logger.trace("add target: "+target.getId());
		Target old = registry.put(target.getId(), target);
//		if (old!=null) logger.warn("A CSVTarget instance with id "+old.getId()+" and class "+old.getClass().getCanonicalName()+" was already registered. The old one has been replaced by the new one with class "+target.getClass().getCanonicalName()+".");
	}
	
	/**
	 * Retrieves the specified {@link CSVTarget}.
	 * @param targetId the {@link CSVTarget} id.
	 * @return the {@link CSVTarget} if found, <code>null</code> otherwise.
	 */
	public Target get(String targetId)
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
//		logger.trace("remove targetId: "+targetId);
		registry.remove(targetId);
	}

}
