package org.gcube.portlets.admin.dataminermanagerdeployer.client.application.home;


import org.gcube.portlets.admin.dataminermanagerdeployer.client.application.ApplicationPresenter;
import org.gcube.portlets.admin.dataminermanagerdeployer.client.place.NameTokens;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;



/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class HomePresenter extends Presenter<HomePresenter.PresenterView, HomePresenter.PresenterProxy> 
implements HomeUiHandlers {
	interface PresenterView extends View, HasUiHandlers<HomePresenter> {
	}

	@ProxyStandard
	@NameToken(NameTokens.HOME)
	@NoGatekeeper
	interface PresenterProxy extends ProxyPlace<HomePresenter> {
	}

	@Inject
	HomePresenter(EventBus eventBus, PresenterView view, PresenterProxy proxy) {
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);

		getView().setUiHandlers(this);
	}

}
