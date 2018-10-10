package org.gcube.portlets.admin.dataminermanagerdeployer.client.application.home;


import javax.inject.Inject;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class HomeView extends ViewWithUiHandlers<HomePresenter> implements HomePresenter.PresenterView {
	interface Binder extends UiBinder<Widget, HomeView> {
	}

	@Inject
	HomeView(Binder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
