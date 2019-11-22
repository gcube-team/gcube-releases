package org.gcube.portlets.widgets.wstaskexecutor.client.event;
import org.gcube.common.workspacetaskexecutor.shared.dataminer.TaskConfiguration;
import org.gcube.portlets.widgets.wstaskexecutor.shared.WSItem;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class DeleteCustomFieldEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 8, 2018
 */
public class DeleteConfigurationEvent  extends GwtEvent<DeleteConfigurationEventHandler> {
	public static Type<DeleteConfigurationEventHandler> TYPE = new Type<DeleteConfigurationEventHandler>();

	private WSItem wsItem;

	private TaskConfiguration taskConf;

	/**
	 * Instantiates a new delete custom field event.
	 *
	 * @param removedEntry the removed entry
	 */
	public DeleteConfigurationEvent(WSItem wsItem, TaskConfiguration taskConf) {
		this.wsItem = wsItem;
		this.taskConf = taskConf;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<DeleteConfigurationEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(DeleteConfigurationEventHandler handler) {
		handler.onRemoveConfiguration(this);
	}


	/**
	 * @return the wsItem
	 */
	public WSItem getWsItem() {

		return wsItem;
	}


	/**
	 * @return the taskConf
	 */
	public TaskConfiguration getTaskConf() {

		return taskConf;
	}
}
