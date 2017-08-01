/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.events.filter;

import java.util.LinkedList;
import java.util.List;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.events.WorkspaceEvent;

/**
 * Filter the event by item id.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class WorkspaceEventFilterByTargetId implements WorkspaceEventFilterCriteria {

	protected List<String> acceptedTargetIds = new LinkedList<String>();
	
	/**
	 * Add a target id.
	 * @param targetId the target id to add.
	 */
	public void addTargetId(String targetId)
	{
		acceptedTargetIds.add(targetId);
	}
	
	/**
	 * Remove a target id. 
	 * @param targetId the target id to remove.
	 */
	public void removeTargetId(String targetId)
	{
		acceptedTargetIds.remove(targetId);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept(WorkspaceEvent event) {
		try {
			return acceptedTargetIds.contains(event.getTarget().getId());
		} catch (InternalErrorException e) {
			return false;
		}
	}

}
