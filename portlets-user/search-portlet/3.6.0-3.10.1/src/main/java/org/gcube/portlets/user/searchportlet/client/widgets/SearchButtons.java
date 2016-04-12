package org.gcube.portlets.user.searchportlet.client.widgets;

import org.gcube.portlets.user.searchportlet.client.SearchConstants;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This class creates the buttons that are used in search
 * It contains 2 buttons (Search and Reset)
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class SearchButtons extends Composite
{
	private Button searchButton;
	private Button resetButton;
	
	public SearchButtons() {
		this("Search", "Reset");
	}
	
	public SearchButtons(String searchButtonName, String clearButtonName) {
		searchButton = new Button(searchButtonName);
		resetButton = new Button(clearButtonName);
		
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		buttonsPanel.setSpacing(6);
		buttonsPanel.add(searchButton);
		buttonsPanel.add(resetButton);

		VerticalPanel vButtons = new VerticalPanel();
		vButtons.add(buttonsPanel);
		vButtons.setSpacing(SearchConstants.SPACING);
		vButtons.setWidth("100%");
		vButtons.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		
		initWidget(vButtons);
	}
	
	public void setSubmitClickListener(ClickHandler handler) {
		searchButton.addClickHandler(handler);
	}
	
	public void setResetClickListener(ClickHandler handler) {
		resetButton.addClickHandler(handler);
	}
	
	public void enableSubmitButton(boolean enable) {
		searchButton.setEnabled(enable);
	}
	
	public void enableResetButton(boolean enable) {
		resetButton.setEnabled(enable);
	}

	public Button getResetButton()
	{
		return resetButton;
	}

	public void setResetButton(Button resetButton)
	{
		this.resetButton = resetButton;
	}

	public Button getSearchButton()
	{
		return searchButton;
	}

	public void setSearchButton(Button searchButton)
	{
		this.searchButton = searchButton;
	}
}
