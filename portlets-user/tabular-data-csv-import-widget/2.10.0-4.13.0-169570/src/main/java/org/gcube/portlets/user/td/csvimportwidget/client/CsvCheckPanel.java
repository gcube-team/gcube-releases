package org.gcube.portlets.user.td.csvimportwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.csvimportwidget.client.dataresource.ResourceBundle;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVRowError;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.tips.ToolTip;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class CsvCheckPanel extends VerticalPanel {

	private static final int ERRORLIMIT = 100;

	private static final Image successImage = new Image(
			ResourceBundle.INSTANCE.csvCheckSuccess());
	private static final Image failureImage = new Image(
			ResourceBundle.INSTANCE.csvCheckFailure());
	private static final Image informationImage = new Image(
			ResourceBundle.INSTANCE.information());
	private static final Image loadingImage = new Image(
			ResourceBundle.INSTANCE.loading());

	private CardLayoutContainer messagePanel;

	private HorizontalPanel checkMessagePanel;
	private HorizontalPanel failureMessagePanel;
	private HorizontalPanel successMessagePanel;
	private HorizontalPanel infoMessagePanel;

	private TextButton btnCheckConfiguration;
	private CheckBox chBoxSkipInvalid;
	private Anchor errorAnchor;

	private CSVErrorWindow errorWindow = new CSVErrorWindow();

	private CSVImportWizardTDMessages msgs;

	public CsvCheckPanel() {
		initMessages();

		setSpacing(2);

		VerticalPanel checkPanel = new VerticalPanel();
		checkPanel.setSpacing(6);

		btnCheckConfiguration = new TextButton(msgs.btnCheckConfigurationText());
		checkPanel.add(btnCheckConfiguration);

		messagePanel = createMessagesPanel();
		checkPanel.add(messagePanel);

		add(checkPanel);

		HorizontalPanel skipPanel = new HorizontalPanel();
		skipPanel.setSpacing(4);

		chBoxSkipInvalid = new CheckBox();
		chBoxSkipInvalid.setBoxLabel(msgs.chBoxSkipInvalidLabel());
		skipPanel.add(chBoxSkipInvalid);
		add(skipPanel);

		setActiveInfoPanel();
	}

	protected void initMessages() {
		msgs = GWT.create(CSVImportWizardTDMessages.class);
	}

	
	public TextButton getCheckConfiguration() {
		return btnCheckConfiguration;
	}

	public CheckBox getSkipInvalidCheckBox() {
		return chBoxSkipInvalid;
	}

	protected CardLayoutContainer createMessagesPanel() {
		CardLayoutContainer messagesPanel = new CardLayoutContainer();

		infoMessagePanel = createInfoPanel();
		messagesPanel.add(infoMessagePanel);

		checkMessagePanel = createCheckPanel();
		messagesPanel.add(checkMessagePanel);

		failureMessagePanel = createFailurePanel();
		messagesPanel.add(failureMessagePanel);

		successMessagePanel = createSuccessPanel();
		messagesPanel.add(successMessagePanel);

		return messagesPanel;
	}

	public void setActiveInfoPanel() {
		messagePanel.setActiveWidget(infoMessagePanel);
		chBoxSkipInvalid.setVisible(false);
	}

	public void setActiveCheckingPanel() {
		messagePanel.setActiveWidget(checkMessagePanel);
		chBoxSkipInvalid.setVisible(false);
	}

	public void setActiveSuccess() {
		messagePanel.setActiveWidget(successMessagePanel);
		chBoxSkipInvalid.setVisible(false);
	}

	public void setActiveFailure(ArrayList<CSVRowError> errors) {
		if (errors.size() >= ERRORLIMIT)
			errorAnchor.setHTML(msgs.failedMoreThanNumberErrors(ERRORLIMIT));
		else
			errorAnchor.setHTML(msgs.failedErrors(errors.size()));

		errorWindow.updateGrid(errors);
		messagePanel.setActiveWidget(failureMessagePanel);
		chBoxSkipInvalid.setVisible(true);
		chBoxSkipInvalid.setValue(false);

	}

	protected HorizontalPanel createInfoPanel() {
		HorizontalPanel infoPanel = new HorizontalPanel();
		infoPanel.setSpacing(3);
		infoPanel.add(informationImage);
		HTML message = new HTML(msgs.checkTheConfigurationBeforeSubmit());
		infoPanel.add(message);
		return infoPanel;
	}

	protected HorizontalPanel createCheckPanel() {
		HorizontalPanel checkPanel = new HorizontalPanel();
		checkPanel.setSpacing(3);
		checkPanel.add(loadingImage);
		checkPanel.add(new HTML(msgs.checkingTheConfiguration()));
		return checkPanel;
	}

	protected HorizontalPanel createFailurePanel() {
		HorizontalPanel failurePanel = new HorizontalPanel();
		failurePanel.setSpacing(3);
		new ToolTip(failurePanel, new ToolTipConfig(
				msgs.clickToObtainMoreInformation()));

		failurePanel.add(failureImage);

		errorAnchor = new Anchor(msgs.failed());

		errorAnchor.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				errorWindow.show();
			}
		});

		failurePanel.add(errorAnchor);

		return failurePanel;
	}

	protected HorizontalPanel createSuccessPanel() {
		HorizontalPanel successPanel = new HorizontalPanel();
		successPanel.setSpacing(3);
		successPanel.add(successImage);
		successPanel.add(new HTML(msgs.correct()));
		return successPanel;
	}
}
