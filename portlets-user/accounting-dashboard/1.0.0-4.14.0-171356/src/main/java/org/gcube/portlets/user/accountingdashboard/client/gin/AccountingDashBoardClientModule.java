package org.gcube.portlets.user.accountingdashboard.client.gin;

import org.gcube.portlets.user.accountingdashboard.client.application.ApplicationModule;
import org.gcube.portlets.user.accountingdashboard.client.place.NameTokens;
import org.gcube.portlets.user.accountingdashboard.client.resources.ResourceLoader;
import org.gcube.portlets.user.accountingdashboard.client.rpc.AccountingDashboardServiceAsync;

import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.RootPresenter;
import com.gwtplatform.mvp.client.annotations.DefaultPlace;
import com.gwtplatform.mvp.client.annotations.ErrorPlace;
import com.gwtplatform.mvp.client.annotations.UnauthorizedPlace;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AccountingDashBoardClientModule extends AbstractPresenterModule {

	@Override
	protected void configure() {		
		bind(ResourceLoader.class).asEagerSingleton();
		bind(AccountingDashboardServiceAsync.class).in(Singleton.class);
		bind(RootPresenter.class).to(CustomRootPresenter.class).asEagerSingleton();
		
		install(new DefaultModule());
		install(new ApplicationModule());

		
		// DefaultPlaceManager Places
		bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.MAIN_AREA);
		bindConstant().annotatedWith(ErrorPlace.class).to(NameTokens.MAIN_AREA);
		bindConstant().annotatedWith(UnauthorizedPlace.class).to(NameTokens.MAIN_AREA);
	}

}