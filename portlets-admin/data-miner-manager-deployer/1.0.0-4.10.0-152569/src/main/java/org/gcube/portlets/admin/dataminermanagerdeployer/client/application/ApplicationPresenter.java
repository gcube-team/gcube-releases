package org.gcube.portlets.admin.dataminermanagerdeployer.client.application;

import org.gcube.portlets.admin.dataminermanagerdeployer.client.application.menu.MenuPresenter;
import org.gcube.portlets.admin.dataminermanagerdeployer.client.rpc.DataMinerDeployerServiceAsync;
import org.gcube.portlets.admin.dataminermanagerdeployer.shared.Constants;
import org.gcube.portlets.admin.dataminermanagerdeployer.shared.session.UserInfo;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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

import gwt.material.design.client.ui.MaterialLoader;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ApplicationPresenter
		extends Presenter<ApplicationPresenter.PresenterView, ApplicationPresenter.PresenterProxy>
		implements NavigationHandler {
	interface PresenterView extends View {
	}

	@ProxyStandard
	interface PresenterProxy extends Proxy<ApplicationPresenter> {
	}

	public static final PermanentSlot<MenuPresenter> SLOT_MENU = new PermanentSlot<>();
	public static final NestedSlot SLOT_MAIN = new NestedSlot();

	private MenuPresenter menuPresenter;
	private DataMinerDeployerServiceAsync service;

	@Inject
	ApplicationPresenter(EventBus eventBus, PresenterView view, PresenterProxy proxy, MenuPresenter menuPresenter,
			DataMinerDeployerServiceAsync service) {
		super(eventBus, view, proxy, RevealType.Root);
		this.menuPresenter = menuPresenter;
		this.service = service;
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

	private void callHello() {
		/* String groupId= GCubeClientContext.getCurrentContextId(); */

		String token = Window.Location.getParameter(Constants.TOKEN);
		GWT.log("Token: " + token);

		MaterialLoader.showLoading(true);
		service.hello(token,new AsyncCallback<UserInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				MaterialLoader.showLoading(false);
				GWT.log("Error in Hello: ", caught);

			}

			@Override
			public void onSuccess(UserInfo result) {
				GWT.log("Hello: " + result.getUsername());
				MaterialLoader.showLoading(false);
			}

		});

	}

}
