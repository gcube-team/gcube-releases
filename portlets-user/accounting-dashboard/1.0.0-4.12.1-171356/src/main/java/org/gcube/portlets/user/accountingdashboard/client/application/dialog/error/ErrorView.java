package org.gcube.portlets.user.accountingdashboard.client.application.dialog.error;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ErrorView extends PopupViewWithUiHandlers<ErrorPresenter> implements ErrorPresenter.ErrorPresenterView {
	private static Logger logger = java.util.logging.Logger.getLogger("");
	
	interface Binder extends UiBinder<PopupPanel, ErrorView> {

	}

	@UiField
	DialogBox dialogBox;

	@UiField
	Paragraph errorMsg;

	@UiField
	Button okBtn;

	@UiHandler("okBtn")
	void handleClick(ClickEvent e) {
		logger.log(Level.FINE,"Close error dialog");
		dialogBox.hide();
	}

	@Inject
	ErrorView(Binder uiBinder, EventBus eventBus) {
		super(eventBus);
		init();
		initWidget(uiBinder.createAndBindUi(this));

	}

	private void init() {
		dialogBox = new DialogBox(false, true);
		dialogBox.getElement().getStyle().setZIndex(1070);
		errorMsg = new Paragraph();
		okBtn = new Button();
	}

	@Override
	public void errorMessage(String error) {
		errorMsg.setText(error);
		dialogBox.center();
		dialogBox.show();
	}

}
