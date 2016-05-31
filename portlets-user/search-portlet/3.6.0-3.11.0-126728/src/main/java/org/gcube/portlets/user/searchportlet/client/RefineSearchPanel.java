package org.gcube.portlets.user.searchportlet.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RefineSearchPanel extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private VerticalPanel basketPanel = new VerticalPanel();
	private VerticalPanel previousQueriesPanel = new VerticalPanel();
	private HorizontalPanel hostRadiosPanel = new HorizontalPanel();
	private RadioButton previousBtn = new RadioButton("refinement", "Refine Previous");
	private RadioButton basketBtn = new RadioButton("refinement", "Select Query from basket");
	private HTML splitLine = new HTML("<div style=\"width:100%; height:2px;  background-color:#92C1F0\"></div>");
	private BasketSavedQueriesPanel basketQueriesP = new BasketSavedQueriesPanel();
	private SearchPreviousResultsPanel previousResultsP = new SearchPreviousResultsPanel();

	public RefineSearchPanel() {
		hostRadiosPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hostRadiosPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		hostRadiosPanel.setSpacing(4);
		hostRadiosPanel.add(previousBtn);
		hostRadiosPanel.add(basketBtn);
		previousBtn.setValue(true);
		basketBtn.setValue(false);
		mainPanel.setWidth("100%");
		//mainPanel.addStyleName("RefinePanel");
		mainPanel.add(hostRadiosPanel);
		mainPanel.add(splitLine);
		basketPanel.add(basketQueriesP);
		previousQueriesPanel.add(previousResultsP);
		mainPanel.add(previousQueriesPanel);

		AsyncCallback<Boolean> getSelectedRadioCallback = new AsyncCallback<Boolean>() {

			public void onFailure(Throwable caught) {

			}

			public void onSuccess(Boolean result) {
				if (result.booleanValue() == true) {
					mainPanel.remove(previousQueriesPanel);
					if (!basketPanel.isAttached())
						mainPanel.add(basketPanel);	
					basketBtn.setValue(true);
				}
				else {
					mainPanel.remove(basketPanel);
					if (!previousQueriesPanel.isAttached())
						mainPanel.add(previousQueriesPanel);
					previousBtn.setValue(true);
				}
			}
		};SearchPortletG.searchService.getSelectedRadioBtn(getSelectedRadioCallback);

		initWidget(mainPanel);

		previousBtn.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mainPanel.remove(basketPanel);
				if (!previousQueriesPanel.isAttached())
					mainPanel.add(previousQueriesPanel);
				setBasketSelectedRadio(false);
			}
		});

		basketBtn.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mainPanel.remove(previousQueriesPanel);
				if (!basketPanel.isAttached())
					mainPanel.add(basketPanel);	
				setBasketSelectedRadio(true);
			}
		});
	}

	private void setBasketSelectedRadio(boolean isBasketSelected) {
		AsyncCallback<Void> setBasketSelectedCallback = new AsyncCallback<Void>() {

			public void onFailure(Throwable caught) {

			}

			public void onSuccess(Void result) {

			}
		};SearchPortletG.searchService.setSelectedRadioBtn(isBasketSelected, setBasketSelectedCallback);
	}

}
