package org.gcube.portlets.admin.dataminermanagerdeployer.client.application.help;


import javax.inject.Inject;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewImpl;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
class HelpView extends ViewImpl implements HelpPresenter.PresenterView {
	interface Binder extends UiBinder<Widget, HelpView> {
	}

	@Inject
	HelpView(Binder uiBinder) {
		initWidget(uiBinder.createAndBindUi(this));
	}

}