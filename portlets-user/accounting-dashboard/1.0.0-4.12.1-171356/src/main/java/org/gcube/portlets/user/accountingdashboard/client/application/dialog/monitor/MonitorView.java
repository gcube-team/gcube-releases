package org.gcube.portlets.user.accountingdashboard.client.application.dialog.monitor;

import javax.inject.Inject;

import org.gcube.portlets.user.accountingdashboard.client.resources.AppResources;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class MonitorView extends PopupViewWithUiHandlers<MonitorPresenter>
		implements MonitorPresenter.MonitorPresenterView {
	interface Binder extends UiBinder<PopupPanel, MonitorView> {

	}

	@UiField
	DialogBox dialogBox;
	
	private AppResources resources;
	
	
	@Inject
	MonitorView(Binder uiBinder, EventBus eventBus, AppResources resources) {
		super(eventBus);
		this.resources=resources;
		init();
		initWidget(uiBinder.createAndBindUi(this));

	}

	private void init() {
		dialogBox = new DialogBox(false, true);
		dialogBox.addStyleName(resources.uiDataCss().uiDataMonitorPopup());
		
	}

	@Override
	public void monitor(boolean enable) {
		if (enable) {
			dialogBox.center();
			dialogBox.show();
		} else {
			dialogBox.hide();
		}

	}

}
