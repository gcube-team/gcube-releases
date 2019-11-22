package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * May 23, 2013
 * 
 */
public class AccountingHistoryEvent extends
		GwtEvent<AccountingHistoryEventHandler> {
	public static Type<AccountingHistoryEventHandler> TYPE = new Type<AccountingHistoryEventHandler>();
	private FileModel targetFileModel;

	public AccountingHistoryEvent(FileModel target) {
		this.setTargetFileModel(target);
	}

	@Override
	public Type<AccountingHistoryEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AccountingHistoryEventHandler handler) {
		handler.onAccountingHistoryShow(this);
	}

	/**
	 * @return the targetFileModel
	 */
	public FileModel getTargetFileModel() {
		return targetFileModel;
	}

	/**
	 * @param targetFileModel the targetFileModel to set
	 */
	public void setTargetFileModel(FileModel targetFileModel) {
		this.targetFileModel = targetFileModel;
	}
	

}
