package org.gcube.portlets.user.accountingdashboard.client.application.menu;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.proxy.NavigationHandler;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class MenuPresenter extends PresenterWidget<MenuPresenter.PresenterView>
		implements MenuUiHandlers, NavigationHandler {
	interface PresenterView extends View, HasUiHandlers<MenuUiHandlers> {

	}

	@Inject
	MenuPresenter(EventBus eventBus, PresenterView view) {
		super(eventBus, view);
		getView().setUiHandlers(this);
	}

	@Override
	protected void onBind() {
		super.onBind();
		addRegisteredHandler(NavigationEvent.getType(), this);
	}

	

	@Override
	public void onNavigation(NavigationEvent navigationEvent) {

	}

}