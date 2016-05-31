package org.gcube.portlets.user.reportgenerator.client.uibinder;

import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class SectionSwitchPanel extends Composite {
	
	public static final String PREV = GWT.getModuleBaseURL() + "../images/prev.png";
	public static final String NEXT = GWT.getModuleBaseURL() + "../images/next.png";

	private static SectionSwitchPanelUiBinder uiBinder = GWT
			.create(SectionSwitchPanelUiBinder.class);

	interface SectionSwitchPanelUiBinder extends
	UiBinder<Widget, SectionSwitchPanel> {
	}

	private Presenter presenter;
	public SectionSwitchPanel(Presenter presenter) {
		initWidget(uiBinder.createAndBindUi(this));
		this.presenter = presenter;
		nextButton.setUrl(NEXT);
		prevButton.setUrl(PREV);
	}

	@UiField
	Image nextButton;
	@UiField
	Image prevButton;
	@UiField
	HTML display;

	@UiHandler("nextButton")
	void onNextClick(ClickEvent e) {
		presenter.nextPageButtonClicked();			
	}
	@UiHandler("prevButton")
	void onPrevClick(ClickEvent e) {
		presenter.prevPageButtonClicked(); 
	}
	/**
	 * changes the pages label in the UI : e.g. Page x of y
	 * @param currentPage . 
	 * @param totalPages .
	 */
	public void setPageDisplayer(int currentPage, int totalPages) {
		display.setHTML("Section "+ currentPage + " of " + totalPages);	
	}	
	/**
	 * Shows the previous botton in the UI
	 */
	public void showPrevButton() {
		prevButton.removeStyleName("setVisibilityOff");
		prevButton.addStyleName("setVisibilityOn");
	}
	/**
	 * Shows the next botton in the UI
	 */
	public void showNextButton() {
		nextButton.removeStyleName("setVisibilityOff");
		nextButton.addStyleName("setVisibilityOn");
	}

	/**
	 * Hide the previous botton in the UI
	 */
	public void hidePrevButton() {
		prevButton.removeStyleName("setVisibilityOn");
		prevButton.addStyleName("setVisibilityOff");
	}
	/**
	 * Hide the next botton in the UI
	 */
	public void hideNextButton() {
		nextButton.removeStyleName("setVisibilityOn");
		nextButton.addStyleName("setVisibilityOff");
	}
	
}
