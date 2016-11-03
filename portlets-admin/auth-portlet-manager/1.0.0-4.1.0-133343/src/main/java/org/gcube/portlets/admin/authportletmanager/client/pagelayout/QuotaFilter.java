package org.gcube.portlets.admin.authportletmanager.client.pagelayout;

import org.gcube.portlets.admin.authportletmanager.client.AuthManagerController;
import org.gcube.portlets.admin.authportletmanager.client.event.ListQuoteEvent;
import org.gcube.portlets.admin.authportletmanager.client.widget.BulletList;
import org.gcube.portlets.admin.authportletmanager.client.widget.ListItem;
import org.gcube.portlets.admin.authportletmanager.client.widget.Paragraph;
import org.gcube.portlets.admin.authportletmanager.client.widget.Span;
import org.gcube.portlets.widgets.widgettour.client.extendedclasses.GCubeTour;

import com.ait.toolkit.hopscotch.client.Placement;
import com.ait.toolkit.hopscotch.client.TourStep;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.InputAddOn;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Header Filter for search, add and delete quote
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */

public class QuotaFilter extends Composite {

	private static QuotaFilterUiBinder uiBinder = GWT
			.create(QuotaFilterUiBinder.class);

	interface QuotaFilterUiBinder extends UiBinder<Widget, QuotaFilter> {
	}


	@UiField
	TextBox t_search;
	@UiField
	InputAddOn i_search;
	@UiField
	Button b_search;
	@UiField
	Button b_refresh;
	@UiField
	Button b_add_quota;
	@UiField
	Button b_delete_quota;
	@UiField
	Button b_group_none;
	@UiField
	Button b_group_user;
	@UiField
	Button b_group_role;


	@UiField
	Button b_search_start;

	@UiField
	Button b_search_contains;


	@UiField
	ButtonGroup b_search_type;

	@UiField
	FlowPanel string_search;

	private String typeSearch="start";

	private BulletList list;

	/**
	 * 
	 */
	public QuotaFilter() {

		initWidget(uiBinder.createAndBindUi(this));
		initFilter();
		guideTour();

	}

	public void guideTour(){
		GCubeTour tour = new GCubeTour("Tour-auth", "auth-portlet-manager", 2, "Would you like to see again this tour next time ?", "Tour Auth Manager");
		GWT.log("AuthManager - Start Gcube Tour ");
		tour.setShowPrevButton(true);
		/*	
			TourStep firstStep = new TourStep(Placement.TOP, "idGridQuote");
			firstStep.setContent("See your quote");
			firstStep.setTitle("List quote manager active");
			firstStep.centerXOffset();
			firstStep.centerArrowOffset();
*/
			TourStep secondStep = new TourStep(Placement.TOP, "idAddQuote");
			secondStep.setContent("Use this for add a new quote manager ");
			secondStep.setTitle("Add Quote");
			secondStep.centerXOffset();
			secondStep.centerArrowOffset();
			
			TourStep thirdStep = new TourStep(Placement.TOP, "idSearchQuote");
			thirdStep.setContent("Use this for search an existing quote manager ");
			thirdStep.setTitle("Search Quote");
			thirdStep.centerXOffset();
			thirdStep.centerArrowOffset();
			
			TourStep fourthStep = new TourStep(Placement.TOP, "idGroupQuote");
			fourthStep.setContent("Use this for filter group quote manager ");
			fourthStep.setTitle("Filter Quote");
			fourthStep.centerXOffset();
			fourthStep.centerArrowOffset();

		//	tour.addStep(firstStep);
			tour.addStep(secondStep);
			tour.addStep(thirdStep);
			tour.addStep(fourthStep);

			tour.startTour();
			GWT.log("AuthManager - Quota Tour Complete ");

	
	}


	/**
	 *  Init filter 
	 */
	public void initFilter(){
		list = new BulletList();
		list.setStyleName("input-list-caller");
		list.clear();
	}


	@UiHandler("t_search")
	public void onKeyDown(KeyDownEvent event) {
		if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			searchQuote();
		}
	}
	@UiHandler("b_search")
	void onClickSearch(ClickEvent e) {
		searchQuote();
	}

	@UiHandler("b_search_start")
	void onClickSearchTypeStart(ClickEvent e) {
		typeSearch="start";
		searchQuote();
	}

	@UiHandler("b_search_contains")
	void onClickSearchTypeContains(ClickEvent e) {
		typeSearch="contains";
		searchQuote();
	}

	@UiHandler("b_group_none")
	void onClickFilterNone(ClickEvent e) {
		filterQuote("");
	}

	@UiHandler("b_group_user")
	void onClickFilterUser(ClickEvent e) {
		filterQuote("User");
	}

	@UiHandler("b_group_role")
	void onClickFilterRole(ClickEvent e) {
		filterQuote("role");
	}








	@UiHandler("b_add_quota")
	void onClickAddQuote(ClickEvent e) {
		GWT.log("AuthManager - Add New Quota");
		QuoteAddDialog popupQuote = new QuoteAddDialog();
		popupQuote.setAnimationEnabled(true);
		popupQuote.show();	

	}
	

	@UiHandler("b_delete_quota")
	void onClickRemoveQuote(ClickEvent e) {
		GWT.log("AuthManager - Remove Quote");
		if (!QuoteDataGrid.selectedQuote.isEmpty()){
			QuoteDeleteDialog confirmDeleteDialog = new QuoteDeleteDialog(QuoteDataGrid.selectedQuote);
			confirmDeleteDialog.show();
		}

	}
	/**
	 *clear filter and reload all quote
	 * @param e
	 */

	@UiHandler("b_refresh")
	void onClickRefresh(ClickEvent e) {
		GWT.log("AuthManager - Refresh List Quote");		
		list.clear();
		AuthManagerController.eventBus.fireEvent(new ListQuoteEvent());	
	}



	public void searchQuote(){
		final String search = t_search.getText();
		if (!search.isEmpty()){
			GWT.log("Search text:"+search);
			//build a list item 
			final ListItem displayItem = new ListItem();
			displayItem.setStyleName("token-input-token-facebook");
			Paragraph p = new Paragraph(search);	
			displayItem.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent clickEvent) {
					displayItem.addStyleName("token-input-selected-token-facebook");
				}
			});

			Span span = new Span("x");
			span.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent clickEvent) {
					removeStringSearch(displayItem, list,search);
				}
			});
			//add a new filter into list
			displayItem.add(p);
			displayItem.add(span);

			//add a filter into new list search 
			QuoteDataProvider.get().setAddStringSearch(search);			
			if (b_search_contains.isToggled())
				typeSearch="contains";
			else
				typeSearch="start";

			//refresh a provider for data grid
			QuoteDataProvider.get().refreshlistFromSearch(typeSearch);

			list.add(displayItem);
			string_search.add(list);
			//init textbox search
			t_search.setText(null);
		}
		else{			
			QuoteDataProvider.get().refreshlistFromSearch(typeSearch);
		}
	}


	/**
	 * Filter list quote for radio button and reset box search
	 * @param typefilter
	 */
	private void filterQuote(String typefilter) {
		// TODO Auto-generated method stub
		//use filter 
		QuoteDataProvider.get().setFilterList(typefilter);
		//reset box search 
		QuoteDataProvider.get().removeAllStringSearch();
		list.clear();


	}



	/**
	 * Remove a string filter from list and provider
	 * 
	 * @param displayItem
	 * @param list
	 * @param search
	 */
	public void removeStringSearch(ListItem displayItem, BulletList list,String search){
		list.remove(displayItem);
		QuoteDataProvider.get().removeStringSearch(search);
		QuoteDataProvider.get().refreshlistFromSearch(typeSearch);

	}
}
