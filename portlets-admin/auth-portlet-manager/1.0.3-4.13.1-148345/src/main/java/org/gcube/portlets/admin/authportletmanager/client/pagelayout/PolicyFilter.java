package org.gcube.portlets.admin.authportletmanager.client.pagelayout;

import org.gcube.portlets.admin.authportletmanager.client.AuthManagerController;
import org.gcube.portlets.admin.authportletmanager.client.event.ListPolicyEvent;
import org.gcube.portlets.admin.authportletmanager.client.widget.BulletList;
import org.gcube.portlets.admin.authportletmanager.client.widget.ListItem;
import org.gcube.portlets.admin.authportletmanager.client.widget.Paragraph;
import org.gcube.portlets.admin.authportletmanager.client.widget.Span;
import org.gcube.portlets.admin.authportletmanager.shared.Caller.TypeCaller;
import org.gcube.portlets.widgets.widgettour.client.extendedclasses.GCubeTour;

import com.ait.toolkit.hopscotch.client.Placement;
import com.ait.toolkit.hopscotch.client.TourStep;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.InputAddOn;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
 * Header Filter for search, add and delete policy
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */

public class PolicyFilter extends Composite {

	private static PolicyFilterUiBinder uiBinder = GWT
			.create(PolicyFilterUiBinder.class);

	interface PolicyFilterUiBinder extends UiBinder<Widget, PolicyFilter> {
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
	Button b_add_policy;

	@UiField
	Button b_delete_policy;
	@UiField
	Button b_group_none;
	@UiField
	Button b_group_user;
	@UiField
	Button b_group_role;
	@UiField
	Button b_group_service;


	@UiField
	ListBox l_context;


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
	public PolicyFilter() {

		initWidget(uiBinder.createAndBindUi(this));
		initFilter();
		
		//	guideTour();

	}

	public void guideTour(){
		GCubeTour tour = new GCubeTour("Tour-auth", "auth-portlet-manager", 1, "Would you like to see again this tour next time ?", "Tour Auth Manager");
		GWT.log("AuthManager - Start Gcube Tour ");
		tour.setShowPrevButton(true);

		TourStep firstStep = new TourStep(Placement.TOP, "idGridPolicy");
		firstStep.setContent("See your rule policYY");
		firstStep.setTitle("List policy active");
		firstStep.centerXOffset();
		firstStep.centerArrowOffset();

		TourStep secondStep = new TourStep(Placement.TOP, "idAddPolicy");
		secondStep.setContent("Use this for add a new policy ");
		secondStep.setTitle("Add Policy");
		secondStep.centerXOffset();
		secondStep.centerArrowOffset();

		TourStep thirdStep = new TourStep(Placement.TOP, "idSearchPolicy");
		thirdStep.setContent("Use this for search an existing policy "
				+ " <br>Use @ for search caller, <br>Use $ for search service, "
				+ "<br>Use * for search access type.");
		thirdStep.setTitle("Search Policy");
		thirdStep.centerXOffset();
		thirdStep.centerArrowOffset();

		TourStep fourthStep = new TourStep(Placement.TOP, "idGroupPolicy");
		fourthStep.setContent("Use this for filter group policy ");
		fourthStep.setTitle("Filter Policy");
		fourthStep.centerXOffset();
		fourthStep.centerArrowOffset();

		//tour.addStep(firstStep);
		tour.addStep(secondStep);
		tour.addStep(thirdStep);
		tour.addStep(fourthStep);

		tour.startTour();

	}


	/**
	 *  Init filter 
	 */
	public void initFilter(){
		list = new BulletList();
		list.setStyleName("input-list-caller");
		list.clear();
	}
	
	public void setInitContext(){
			for (String context:PolicyDataProvider.get().getContextList()){
				l_context.addItem(context,context);
			}
			
			
		l_context.addChangeHandler(new ChangeHandler() {
			@SuppressWarnings("rawtypes")
			public void onChange(ChangeEvent event) {
				int indexC = l_context.getSelectedIndex();
				String	newValue =l_context.getValue(indexC);
				PolicyDataProvider.get().setContext(newValue);
				GWT.log("AuthManager - initContext"+newValue);
				list.clear();
				b_group_none.setFocus(true);
				b_group_none.setActive(true);

				b_group_user.setFocus(false);
				b_group_user.setActive(false);

				b_group_role.setFocus(false);
				b_group_role.setActive(false);

				b_group_service.setFocus(false);
				b_group_service.setActive(false);
				AuthManagerController.eventBus.fireEvent(new ListPolicyEvent());		
			}
		});
	}

	@UiHandler("t_search")
	public void onKeyDown(KeyDownEvent event) {
		if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			searchPolicy();
		}
	}
	@UiHandler("b_search")
	void onClickSearch(ClickEvent e) {
		searchPolicy();
	}

	@UiHandler("b_search_start")
	void onClickSearchTypeStart(ClickEvent e) {
		typeSearch="start";
		searchPolicy();
	}

	@UiHandler("b_search_contains")
	void onClickSearchTypeContains(ClickEvent e) {
		typeSearch="contains";
		searchPolicy();
	}

	@UiHandler("b_group_none")
	void onClickFilterNone(ClickEvent e) {
		filterPolicy("");
	}

	@UiHandler("b_group_user")
	void onClickFilterUser(ClickEvent e) {
		filterPolicy(TypeCaller.user.toString());
	}

	@UiHandler("b_group_role")
	void onClickFilterRole(ClickEvent e) {
		filterPolicy(TypeCaller.role.toString());
	}

	@UiHandler("b_group_service")
	void onClickFilterService(ClickEvent e) {
		filterPolicy(TypeCaller.service.toString());
	}








	@UiHandler("b_add_policy")
	void onClickAddPolicy(ClickEvent e) {
		GWT.log("AuthManager - Open Dialog Insert/Modify Policy");
		PolicyAddDialog popup = new PolicyAddDialog();

		popup.setAnimationEnabled(true);

		popup.show();	

	}

	@UiHandler("b_delete_policy")
	void onClickRemovePolicy(ClickEvent e) {
		GWT.log("AuthManager - Remove Policy");
		if (!PolicyDataGrid.selectedPolicy.isEmpty()){
			PolicyDeleteDialog confirmDeleteDialog = new PolicyDeleteDialog(PolicyDataGrid.selectedPolicy);
			confirmDeleteDialog.show();
		}

	}
	/**
	 *clear filter and reload all policy
	 * @param e
	 */

	@UiHandler("b_refresh")
	void onClickRefresh(ClickEvent e) {
		GWT.log("AuthManager - Refresh List Policy");		
		list.clear();
		b_group_none.setFocus(true);
		b_group_none.setActive(true);

		b_group_user.setFocus(false);
		b_group_user.setActive(false);

		b_group_role.setFocus(false);
		b_group_role.setActive(false);

		b_group_service.setFocus(false);
		b_group_service.setActive(false);

		AuthManagerController.eventBus.fireEvent(new ListPolicyEvent());		
	}



	public void searchPolicy(){
		final String search = t_search.getText();
		if (!search.isEmpty()){
			GWT.log("AuthManager - Filter Search:"+search);
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
			PolicyDataProvider.get().setAddStringSearch(search);			
			if (b_search_contains.isToggled())
				typeSearch="contains";
			else
				typeSearch="start";

			//refresh a provider for data grid
			PolicyDataProvider.get().refreshlistFromSearch(typeSearch);

			list.add(displayItem);
			string_search.add(list);
			//init textbox search
			t_search.setText(null);
		}
		else{			
			PolicyDataProvider.get().refreshlistFromSearch(typeSearch);
		}
	}


	/**
	 * Filter list policy for radio button and reset box search
	 * @param typefilter
	 */
	private void filterPolicy(String typefilter) {
		// TODO Auto-generated method stub
		//use filter 
		PolicyDataProvider.get().setFilterList(typefilter);
		//reset box search 
		PolicyDataProvider.get().removeAllStringSearch();
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
		PolicyDataProvider.get().removeStringSearch(search);
		PolicyDataProvider.get().refreshlistFromSearch(typeSearch);

	}




}
