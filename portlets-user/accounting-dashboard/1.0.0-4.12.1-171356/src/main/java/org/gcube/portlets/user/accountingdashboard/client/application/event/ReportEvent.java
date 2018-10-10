package org.gcube.portlets.user.accountingdashboard.client.application.event;

import org.gcube.portlets.user.accountingdashboard.shared.data.ReportData;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ReportEvent extends GwtEvent<ReportEvent.ReportEventHandler> {

	private ReportData reportData;

	public interface ReportEventHandler extends EventHandler {
		void onData(ReportEvent event);
	}

	public static final Type<ReportEventHandler> TYPE = new Type<>();

	public ReportEvent(ReportData reportData) {
		this.reportData = reportData;
	}

	public static void fire(HasHandlers source, ReportEvent event) {
		source.fireEvent(event);
	}

	@Override
	public Type<ReportEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ReportEventHandler handler) {
		handler.onData(this);
	}

	public ReportData getReportData() {
		return reportData;
	}

	@Override
	public String toString() {
		return "ReportEvent [reportData=" + reportData + "]";
	}

}