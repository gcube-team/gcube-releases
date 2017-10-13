package org.gcube.portlets.admin.dataminermanagerdeployer.client.application;


import javax.inject.Inject;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ApplicationView extends ViewImpl implements ApplicationPresenter.PresenterView {
	interface Binder extends UiBinder<Widget, ApplicationView> {
	}

	@UiField
	HTMLPanel menu;
	@UiField
	HTMLPanel main;

	@Inject
	ApplicationView(Binder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
		bindSlot(ApplicationPresenter.SLOT_MENU, menu);
		bindSlot(ApplicationPresenter.SLOT_MAIN, main);
	}
	

}
