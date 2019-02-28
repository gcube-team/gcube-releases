package org.gcube.portlets.user.accountingdashboard.client.application.mainarea.filter;

import org.gcube.portlets.user.accountingdashboard.shared.data.RequestReportData;

import com.gwtplatform.mvp.client.UiHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public interface FilterAreaUiHandlers extends UiHandlers {

	public void getReport(RequestReportData requestReportData);
}
