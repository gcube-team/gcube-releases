package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class ExecuteDataMinerTaskEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * May 25, 2018
 */
public class ExecuteDataMinerTaskEvent extends
		GwtEvent<ExecuteDataMinerTaskEventHandler> {
	public static Type<ExecuteDataMinerTaskEventHandler> TYPE = new Type<ExecuteDataMinerTaskEventHandler>();
	private FileModel targetFileModel;

	/**
	 * Instantiates a new execute data miner task event.
	 *
	 * @param target the target
	 */
	public ExecuteDataMinerTaskEvent(FileModel target) {
		this.setTargetFileModel(target);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ExecuteDataMinerTaskEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ExecuteDataMinerTaskEventHandler handler) {
		handler.onExecuteDMTask(this);
	}

	/**
	 * Gets the target file model.
	 *
	 * @return the targetFileModel
	 */
	public FileModel getTargetFileModel() {
		return targetFileModel;
	}

	/**
	 * Sets the target file model.
	 *
	 * @param targetFileModel the targetFileModel to set
	 */
	public void setTargetFileModel(FileModel targetFileModel) {
		this.targetFileModel = targetFileModel;
	}


}
