package org.gcube.portlets.user.accountingdashboard.client.application.mainarea;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.gcube.portlets.user.accountingdashboard.client.resources.AppResources;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class MainAreaView extends ViewWithUiHandlers<MainAreaPresenter> implements MainAreaPresenter.MainAreaView {

	@SuppressWarnings("unused")
	private static Logger logger = java.util.logging.Logger.getLogger("");

	interface Binder extends UiBinder<Widget, MainAreaView> {
	}

	@UiField
	HTMLPanel filterPanel;

	@UiField
	HTMLPanel reportPanel;

	
	@SuppressWarnings("unused")
	private AppResources resources;

	@Inject
	MainAreaView(Binder uiBinder, AppResources resources) {
		this.resources = resources;
		init();
		initWidget(uiBinder.createAndBindUi(this));
		bindSlot(MainAreaPresenter.SLOT_FILTER, filterPanel);
		bindSlot(MainAreaPresenter.SLOT_REPORT, reportPanel);
	
		
	}
	
	private void init() {
	
	}

	
}
