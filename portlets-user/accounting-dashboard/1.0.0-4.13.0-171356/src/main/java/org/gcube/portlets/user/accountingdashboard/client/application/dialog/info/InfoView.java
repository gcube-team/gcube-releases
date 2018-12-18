package org.gcube.portlets.user.accountingdashboard.client.application.dialog.info;

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
public class InfoView extends PopupViewWithUiHandlers<InfoPresenter> implements InfoPresenter.InfoPresenterView {
	private static Logger logger = java.util.logging.Logger.getLogger("");
	
	interface Binder extends UiBinder<PopupPanel, InfoView> {

	}

	@UiField
	DialogBox dialogBox;

	@UiField
	Paragraph infoMsg;

	@UiField
	Button okBtn;

	@UiHandler("okBtn")
	void handleClick(ClickEvent e) {
		logger.log(Level.FINE,"Close info dialog");
		dialogBox.hide();
	}

	@Inject
	InfoView(Binder uiBinder, EventBus eventBus) {
		super(eventBus);
		init();
		initWidget(uiBinder.createAndBindUi(this));

	}

	private void init() {
		dialogBox = new DialogBox(false, true);
		dialogBox.getElement().getStyle().setZIndex(1070);
		infoMsg = new Paragraph();
		okBtn = new Button();
	}

	@Override
	public void infoMessage(String error) {
		infoMsg.setText(error);
		dialogBox.center();
		dialogBox.show();
	}

}
