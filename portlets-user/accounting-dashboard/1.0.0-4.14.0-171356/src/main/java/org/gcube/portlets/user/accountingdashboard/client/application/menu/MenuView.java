package org.gcube.portlets.user.accountingdashboard.client.application.menu;

import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Nav;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
class MenuView extends ViewWithUiHandlers<MenuUiHandlers> implements MenuPresenter.PresenterView {

	interface Binder extends UiBinder<Widget, MenuView> {
	}

	@UiField
	Nav nav;

	@Inject
	MenuView(Binder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));

	}

}