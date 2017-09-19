package org.gcube.portlets.admin.dataminermanagerdeployer.client.application.menu;


import javax.inject.Inject;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

import gwt.material.design.client.ui.MaterialHeader;
import gwt.material.design.client.ui.MaterialNavBar;
import gwt.material.design.client.ui.MaterialSideNav;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
class MenuView extends ViewWithUiHandlers<MenuUiHandlers> implements MenuPresenter.PresenterView {

	interface Binder extends UiBinder<Widget, MenuView> {
	}

	@UiField
	MaterialHeader header;
	@UiField
	MaterialNavBar navBar;
	@UiField
	MaterialSideNav sideNav;

	@Inject
	MenuView(Binder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));

		sideNav.addOpenedHandler(event -> getUiHandlers().setContentPush());
		sideNav.addClosedHandler(event -> getUiHandlers().setContentPush());
		
	}

}