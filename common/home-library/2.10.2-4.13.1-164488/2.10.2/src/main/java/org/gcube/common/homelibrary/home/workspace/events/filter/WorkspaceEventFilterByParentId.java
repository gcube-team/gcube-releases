/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.events.filter;

import java.util.LinkedList;
import java.util.List;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.events.WorkspaceEvent;

/**
 * Filter the event by parent item id.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class WorkspaceEventFilterByParentId implements WorkspaceEventFilterCriteria {

	protected List<String> acceptedParentIds = new LinkedList<String>();
	
	/**
	 * Add a parent id.
	 * @param parentId the parent id to add.
	 */
	public void addParentId(String parentId)
	{
		acceptedParentIds.add(parentId);
	}
	
	/**
	 * Remove a parent id. 
	 * @param parentId the parent id to remove.
	 */
	public void removeParentId(String parentId)
	{
		acceptedParentIds.remove(parentId);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean accept(WorkspaceEvent event) {
		try {
			WorkspaceFolder parent = event.getTarget().getParent();
			if (parent!=null) return acceptedParentIds.contains(parent.getId());
			return false;
		} catch (InternalErrorException e) {
			return false;
		}
	}

}
