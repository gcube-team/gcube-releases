/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client;

import java.util.ArrayList;

import org.gcube.portlets.user.csvimportwizard.client.data.CSVRowError;
import org.gcube.portlets.user.csvimportwizard.client.util.WizardResources;

import com.extjs.gxt.ui.client.widget.CardPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CsvCheckPanel extends VerticalPanel {
	
	public static final int ERROR_LIMIT = 50;
	
	protected static final Image successImage = new Image(WizardResources.INSTANCE.csvCheckSuccess());
	protected static final Image failureImage = new Image(WizardResources.INSTANCE.csvCheckFailure());
	protected static final Image informationImage = new Image(WizardResources.INSTANCE.information());
	protected static final Image loadingImage = new Image(WizardResources.INSTANCE.loading());
	
	protected CardPanel messagePanel;
	
	protected HorizontalPanel checkMessagePanel;
	protected HorizontalPanel failureMessagePanel;
	protected HorizontalPanel successMessagePanel;
	protected HorizontalPanel infoMessagePanel;
	
	protected Button checkConfiguration;
	protected Button showErrorButton;
	protected CheckBox skipInvalidCheckBox;
	protected Anchor errorAnchor;
	
	protected CSVErrorWindow errorWindow = new CSVErrorWindow();
	
	public CsvCheckPanel()
	{
		setSpacing(2);
		setWidth(350);
		
		HorizontalPanel checkPanel = new HorizontalPanel();
		checkPanel.setSpacing(4);
		
		checkConfiguration = new Button("Check configuration");
		checkPanel.add(checkConfiguration);
		
		messagePanel = createMessagesPanel();
		checkPanel.add(messagePanel);
		
		add(checkPanel);
		
		HorizontalPanel skipPanel = new HorizontalPanel();
		skipPanel.setSpacing(4);
		
		skipInvalidCheckBox = new CheckBox();
		skipInvalidCheckBox.setBoxLabel("Skip invalid lines");
		skipPanel.add(skipInvalidCheckBox);
		add(skipPanel);
		
		setActiveInfoPanel();
	}

	/**
	 * @return
	 */
	public Button getCheckConfiguration() {
		return checkConfiguration;
	}

	/**
	 * @return the skipInvalidCheckBox
	 */
	public CheckBox getSkipInvalidCheckBox() {
		return skipInvalidCheckBox;
	}

	protected CardPanel createMessagesPanel()
	{
		CardPanel messagesPanel = new CardPanel();
		
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
	
	public void setActiveInfoPanel()
	{
		messagePanel.setActiveItem(infoMessagePanel);
		skipInvalidCheckBox.setVisible(false);
	}
	
	public void setActiveCheckingPanel()
	{
		messagePanel.setActiveItem(checkMessagePanel);
		skipInvalidCheckBox.setVisible(false);
	}
	
	public void setActiveSuccess()
	{
		messagePanel.setActiveItem(successMessagePanel);
		skipInvalidCheckBox.setVisible(false);
	}
	
	public void setActiveFailure(ArrayList<CSVRowError> errors)
	{
		if (errors.size() >= ERROR_LIMIT) errorAnchor.setHTML("Failed (more than "+ERROR_LIMIT+" errors)");
		else errorAnchor.setHTML("Failed ("+errors.size()+" errors)");
		
		errorWindow.updateGrid(errors);
				
		skipInvalidCheckBox.setVisible(true);
		messagePanel.setActiveItem(failureMessagePanel);
	}
	
	protected HorizontalPanel createInfoPanel()
	{
		HorizontalPanel infoPanel = new HorizontalPanel();
		infoPanel.setSpacing(2);
		infoPanel.setWidth(200);
		infoPanel.add(informationImage);
		Html message = new Html("Check the configuration before submit it");
		message.setWidth(300);
		infoPanel.add(message);
		return infoPanel;
	}
	
	protected HorizontalPanel createCheckPanel()
	{
		HorizontalPanel checkPanel = new HorizontalPanel();
		checkPanel.setSpacing(2);
		checkPanel.add(loadingImage);
		checkPanel.add(new Html("Checking the configuration..."));
		return checkPanel;
	}
	
	protected HorizontalPanel createFailurePanel()
	{
		HorizontalPanel failurePanel = new HorizontalPanel();
		failurePanel.setSpacing(2);
		failurePanel.setWidth(200);
		failurePanel.setToolTip("Click to obtain more information");
		
		failurePanel.add(failureImage);
		
		errorAnchor = new Anchor("Failed");
		
		errorAnchor.addClickHandler(new ClickHandler() {
			
		
			public void onClick(ClickEvent event) {
				errorWindow.show();
			}
		});
			
		failurePanel.add(errorAnchor);
		
		return failurePanel;
	}
	
	protected HorizontalPanel createSuccessPanel()
	{
		HorizontalPanel successPanel = new HorizontalPanel();
		successPanel.setSpacing(2);
		successPanel.add(successImage);
		successPanel.add(new Html("Correct."));
		return successPanel;
	}
}
