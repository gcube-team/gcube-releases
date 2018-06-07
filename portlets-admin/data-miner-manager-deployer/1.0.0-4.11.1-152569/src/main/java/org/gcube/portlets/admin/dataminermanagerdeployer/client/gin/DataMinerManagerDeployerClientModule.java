package org.gcube.portlets.admin.dataminermanagerdeployer.client.gin;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;

import org.gcube.portlets.admin.dataminermanagerdeployer.client.application.ApplicationModule;
import org.gcube.portlets.admin.dataminermanagerdeployer.client.place.NameTokens;
import org.gcube.portlets.admin.dataminermanagerdeployer.client.resources.ResourceLoader;
import org.gcube.portlets.admin.dataminermanagerdeployer.client.rpc.DataMinerDeployerServiceAsync;

import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.RootPresenter;
import com.gwtplatform.mvp.client.annotations.DefaultPlace;
import com.gwtplatform.mvp.client.annotations.ErrorPlace;
import com.gwtplatform.mvp.client.annotations.UnauthorizedPlace;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class DataMinerManagerDeployerClientModule extends AbstractPresenterModule {

	@Override
	protected void configure() {		
		bind(ResourceLoader.class).asEagerSingleton();
		bind(DataMinerDeployerServiceAsync.class).in(Singleton.class);
		bind(RootPresenter.class).to(CustomRootPresenter.class).asEagerSingleton();
		
		install(new DefaultModule());
		install(new ApplicationModule());

		
		// DefaultPlaceManager Places
		bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.DEPLOY);
		bindConstant().annotatedWith(ErrorPlace.class).to(NameTokens.DEPLOY);
		bindConstant().annotatedWith(UnauthorizedPlace.class).to(NameTokens.DEPLOY);
	}

}