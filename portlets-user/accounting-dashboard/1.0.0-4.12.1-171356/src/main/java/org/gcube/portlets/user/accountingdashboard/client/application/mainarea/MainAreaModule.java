package org.gcube.portlets.user.accountingdashboard.client.application.mainarea;


import org.gcube.portlets.user.accountingdashboard.client.application.mainarea.filter.FilterAreaModule;
import org.gcube.portlets.user.accountingdashboard.client.application.mainarea.report.ReportAreaModule;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class MainAreaModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		install(new FilterAreaModule());
		install(new ReportAreaModule());
		bindPresenter(MainAreaPresenter.class, MainAreaPresenter.MainAreaView.class, MainAreaView.class, MainAreaPresenter.MainAreaPresenterProxy.class);
	}
}
