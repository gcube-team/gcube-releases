package org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ReportAreaModule extends AbstractPresenterModule {
	@Override
	protected void configure() {

		bindPresenterWidget(ReportAreaPresenter.class, ReportAreaPresenter.ReportAreaView.class, ReportAreaView.class);
	}
}
