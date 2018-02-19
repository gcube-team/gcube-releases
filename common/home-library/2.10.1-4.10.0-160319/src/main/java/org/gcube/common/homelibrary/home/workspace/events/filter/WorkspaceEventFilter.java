/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.events.filter;

import java.util.LinkedList;
import java.util.List;

import org.gcube.common.homelibrary.home.workspace.events.AbstractWorkspaceEventSource;
import org.gcube.common.homelibrary.home.workspace.events.WorkspaceEvent;
import org.gcube.common.homelibrary.home.workspace.events.WorkspaceEventSource;
import org.gcube.common.homelibrary.home.workspace.events.WorkspaceListener;

/**
 * Implements an event filter.
 * With this filter are accepted the events with target an item with id in the specified list.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class WorkspaceEventFilter extends AbstractWorkspaceEventSource implements WorkspaceListener{

	protected List<WorkspaceEventFilterCriteria> criterions = new LinkedList<WorkspaceEventFilterCriteria>();
	
	/**
	 * Create a new event filter.
	 * @param source the event source.
	 */
	public WorkspaceEventFilter(WorkspaceEventSource source)
	{
		source.addWorkspaceListener(this);
	}
	
	/**
	 * Add a criteria.
	 * @param criteria the criteria to add.
	 */
	public void addCriteria(WorkspaceEventFilterCriteria criteria)
	{
		criterions.add(criteria);
	}
	
	/**
	 * Remove a criteria.
	 * @param criteria the criteria to remove.
	 */
	public void removeCriteria(WorkspaceEventFilterCriteria criteria)
	{
		criterions.remove(criteria);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void workspaceEvent(WorkspaceEvent event) {
		
		for (WorkspaceEventFilterCriteria criteria:criterions){
			if (!criteria.accept(event)) return;
		}
		
		fireWorkspaceEvent(event);
		
	}

}
