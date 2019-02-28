package org.gcube.portlets.user.accountingdashboard.client.application;


import org.gcube.portlets.user.accountingdashboard.client.application.controller.Controller;
import org.gcube.portlets.user.accountingdashboard.client.application.menu.MenuPresenter;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.presenter.slots.NestedSlot;
import com.gwtplatform.mvp.client.presenter.slots.PermanentSlot;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.proxy.NavigationHandler;
import com.gwtplatform.mvp.client.proxy.Proxy;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ApplicationPresenter
		extends Presenter<ApplicationPresenter.PresenterView, ApplicationPresenter.ApplicationPresenterProxy>
		implements NavigationHandler {
	interface PresenterView extends View {
	}

	@ProxyStandard
	interface ApplicationPresenterProxy extends Proxy<ApplicationPresenter> {
	}

	public static final PermanentSlot<MenuPresenter> SLOT_MENU = new PermanentSlot<>();
	public static final NestedSlot SLOT_MAIN = new NestedSlot();

	private MenuPresenter menuPresenter;
	private Controller controller;
	

	@Inject
	ApplicationPresenter(EventBus eventBus, PresenterView view, ApplicationPresenterProxy proxy, MenuPresenter menuPresenter,
			Controller controller) {
		super(eventBus, view, proxy, RevealType.Root);
		this.menuPresenter = menuPresenter;
		this.controller = controller;
		callHello();
	}

	@Override
	protected void onBind() {
		super.onBind();

		setInSlot(SLOT_MENU, menuPresenter);
		addRegisteredHandler(NavigationEvent.getType(), this);

	}

	@Override
	protected void onReveal() {
		super.onReveal();
	}

	@Override
	public void onNavigation(NavigationEvent navigationEvent) {
		Window.scrollTo(0, 0);
		// PlaceRequest placeRequest=navigationEvent.getRequest();
		// placeManager.revealPlace(placeRequest);

	}
	
	private void callHello(){
		controller.callHello();
	}

	

}
