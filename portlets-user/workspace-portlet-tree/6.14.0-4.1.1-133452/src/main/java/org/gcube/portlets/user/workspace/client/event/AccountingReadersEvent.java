package org.gcube.portlets.user.workspace.client.event;

import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 23, 2013
 * 
 */
public class AccountingReadersEvent extends
		GwtEvent<AccountingReadersEventHandler> {
	public static Type<AccountingReadersEventHandler> TYPE = new Type<AccountingReadersEventHandler>();
	private FileModel targetFileModel;

	public AccountingReadersEvent(FileModel target) {
		this.setTargetFileModel(target);
	}

	@Override
	public Type<AccountingReadersEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AccountingReadersEventHandler handler) {
		handler.onAccountingReadersShow(this);
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
