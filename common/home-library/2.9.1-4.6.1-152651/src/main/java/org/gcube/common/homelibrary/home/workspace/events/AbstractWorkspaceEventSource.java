/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.events;

import java.util.LinkedList;
import java.util.List;

import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public abstract class AbstractWorkspaceEventSource implements WorkspaceEventSource {
	
	protected List<WorkspaceListener> listeners = new LinkedList<WorkspaceListener>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addWorkspaceListener(WorkspaceListener listener) {
		listeners.add(listener);		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeWorkspaceListener(WorkspaceListener listener) {
		listeners.remove(listener);		
	}
	
	/**
	 * Fire an Item Created event.
	 * @param item the created item.
	 */
	public void fireItemCreatedEvent(WorkspaceItem item)
	{
		fireWorkspaceEvent(new WorkspaceEventImpl(WorkspaceEventType.ITEM_CREATED, item));
	}
	
	/**
	 * Fire an Item Removed event.
	 * @param item the removed item.
	 */
	public void fireItemRemovedEvent(WorkspaceItem item)
	{
		fireWorkspaceEvent(new WorkspaceEventImpl(WorkspaceEventType.ITEM_REMOVED, item));
	}
	
	/**
	 * Fire an Item Imported event.
	 * @param item the imported item.
	 */
	public void fireItemImportedEvent(WorkspaceItem item)
	{
		fireWorkspaceEvent(new WorkspaceEventImpl(WorkspaceEventType.ITEM_IMPORTED, item));
	}
	
	/**
	 * Fire an Item Imported event.
	 * @param item the renamed item.
	 */
	public void fireItemRenamedEvent(WorkspaceItem item)
	{
		fireWorkspaceEvent(new WorkspaceEventImpl(WorkspaceEventType.ITEM_RENAMED, item));
	}
	
	/**
	 * Fire an Item Updated event.
	 * @param item the updated item.
	 */
	public void fireItemUpdatedEvent(WorkspaceItem item)
	{
		fireWorkspaceEvent(new WorkspaceEventImpl(WorkspaceEventType.ITEM_UPDATED, item));
	}
	
	/**
	 * Fire an Item sent event.
	 * @param item the sent item.
	 * @param addressees the item addressees.
	 */
	public void fireItemSentEvent(WorkspaceItem item, List<User> addressees)
	{
		fireWorkspaceEvent(new WorkspaceSentEventImpl(item, addressees));
	}
	
	/**
	 * Fire a Workspace event for all listeners.
	 * @param event the event to propagate.
	 */
	protected void fireWorkspaceEvent(final WorkspaceEvent event)
	{
		for (final WorkspaceListener listener:listeners)
		{
			listener.workspaceEvent(event);
//			Thread thEvent = new Thread(){
//
//				/**
//				 * {@inheritDoc}
//				 */
//				@Override
//				public void run() {
//					listener.workspaceEvent(event);
//				}
//				
//			};
//			thEvent.start();
		}
	}

}
