package org.gcube.portlets.admin.dataminermanagerdeployer.client.application.help;


import org.gcube.portlets.admin.dataminermanagerdeployer.client.application.ApplicationPresenter;
import org.gcube.portlets.admin.dataminermanagerdeployer.client.place.NameTokens;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;



/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class HelpPresenter extends Presenter<HelpPresenter.PresenterView, HelpPresenter.PresenterProxy> {
	interface PresenterView extends View {
	}

	@ProxyStandard
	@NameToken(NameTokens.HELP)
	interface PresenterProxy extends ProxyPlace<HelpPresenter> {
	}

	@Inject
	HelpPresenter(EventBus eventBus, PresenterView view, PresenterProxy proxy) {
		super(eventBus, view, proxy, ApplicationPresenter.SLOT_MAIN);
	}

}