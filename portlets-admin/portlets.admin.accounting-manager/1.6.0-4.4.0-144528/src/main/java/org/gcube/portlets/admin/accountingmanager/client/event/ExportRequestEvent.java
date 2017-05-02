package org.gcube.portlets.admin.accountingmanager.client.event;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.export.ExportType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ExportRequestEvent extends
		GwtEvent<ExportRequestEvent.ExportRequestEventHandler> {

	public static Type<ExportRequestEventHandler> TYPE = new Type<ExportRequestEventHandler>();
	private ExportType exportType;
	private AccountingType accountingType;

	public interface ExportRequestEventHandler extends EventHandler {
		void onExport(ExportRequestEvent event);
	}

	public interface HasExportRequestEventHandler extends HasHandlers {
		public HandlerRegistration addExportRequestEventHandler(
				ExportRequestEventHandler handler);
	}

	public ExportRequestEvent(ExportType exportType,
			AccountingType accountingType) {
		this.exportType=exportType;
		this.accountingType = accountingType;
		
	}

	@Override
	protected void dispatch(ExportRequestEventHandler handler) {
		handler.onExport(this);
	}

	@Override
	public Type<ExportRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ExportRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			ExportRequestEvent accountingMenuEvent) {
		source.fireEvent(accountingMenuEvent);
	}

	public AccountingType getAccountingType() {
		return accountingType;
	}

	public ExportType getExportType() {
		return exportType;
	}

	@Override
	public String toString() {
		return "ExportRequestEvent [exportType=" + exportType
				+ ", accountingType=" + accountingType + "]";
	}

}
