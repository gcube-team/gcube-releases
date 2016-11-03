package org.gcube.portlets.admin.authportletmanager.client.pagelayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.gcube.portlets.admin.authportletmanager.client.AuthManagerController;
import org.gcube.portlets.admin.authportletmanager.client.event.AddQuoteEvent;
import org.gcube.portlets.admin.authportletmanager.client.event.UpdateQuoteEvent;
import org.gcube.portlets.admin.authportletmanager.client.resource.AuthResources;
import org.gcube.portlets.admin.authportletmanager.client.widget.InputListWidget;
import org.gcube.portlets.admin.authportletmanager.client.widget.WindowBox;
import org.gcube.portlets.admin.authportletmanager.shared.Caller;
import org.gcube.portlets.admin.authportletmanager.shared.Caller.TypeCaller;
import org.gcube.portlets.admin.authportletmanager.shared.Quote;
import org.gcube.portlets.admin.authportletmanager.shared.Quote.ManagerType;
import org.gcube.portlets.admin.authportletmanager.shared.Quote.TimeInterval;
import org.gcube.portlets.widgets.widgettour.client.extendedclasses.GCubeTour;

import com.ait.toolkit.hopscotch.client.Placement;
import com.ait.toolkit.hopscotch.client.TourStep;
import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Row;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.AlertBase;
import com.github.gwtbootstrap.client.ui.event.ClosedEvent;
import com.github.gwtbootstrap.client.ui.event.ClosedHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * Dialog Box for add a policy 
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */
public class QuoteAddDialog extends WindowBox {

	private static final Binder binder = GWT.create(Binder.class);
	interface Binder extends UiBinder<Widget, QuoteAddDialog> {
	}

	private Boolean error_display=false;

	private ArrayList<Quote>listModifyQuote;

	private ArrayList<Quote>listAddQuote;

	public InputListWidget ListWidget; 

	@UiField
	FlowPanel text_caller_quota;
	@UiField
	ListBox l_time_interval_quota;
	@UiField
	ListBox l_type_quota;
	@UiField
	ButtonGroup b_caller_checkbox;
	@UiField
	Button b_caller_all_user;
	@UiField
	Button b_caller_all_role;	
	@UiField
	Button b_caller_except;
	@UiField
	Button b_add_multiple_caller;
	@UiField
	Button b_exit_dialog_quota;
	@UiField
	Button b_save_quota;
	@UiField
	TextBox t_value_quota;
	@UiField
	Row  r_loader_space;
	@UiField
	FlowPanel idAddCallerQuote;
	@UiField
	FlowPanel idAddTimeQuote;
	@UiField
	FlowPanel idAddTypeQuote;
	@UiField
	FlowPanel idAddValueQuote;

	public QuoteAddDialog() {
		super(true, false);
		this.setWidget(binder.createAndBindUi(this));
		//this.setGlassEnabled(true);
		this.setWidth("800px");
		this.setAnimationEnabled(true);
		this.setGlassEnabled(true);
		this.center();
		this.setText("Add/Update Quota");
		idAddCallerQuote.getElement().setId("idAddCallerQuote");
		idAddTimeQuote.getElement().setId("idAddTimeQuote");
		idAddTypeQuote.getElement().setId("idAddTypeQuote");
		idAddValueQuote.getElement().setId("idAddValueQuote");
		//istance a new list policy for modify
		listModifyQuote=new ArrayList<Quote>();
		//Initialize a suggest for caller
		text_caller_quota.setStyleName("auto_suggest");
		ListWidget=new InputListWidget();
		text_caller_quota.add(ListWidget);
		//disable except
		b_caller_except.setEnabled(false);
		for (TimeInterval time : TimeInterval.values()) {
			l_time_interval_quota.addItem(time.toString());

		}
		for (ManagerType managerType : ManagerType.values()) {
			l_type_quota.addItem(managerType.toString());
		}
		r_loader_space.clear();
		GCubeTour tourAddDialogQuote = new GCubeTour("tour-auth add dialog", "auth-portlet-manager", 2, "Would you like to see again this tour next time ?", "Tour Quote Manager use");
		tourAddDialogQuote.setShowPrevButton(true);
		TourStep firstStep = new TourStep(Placement.TOP, "idAddCallerQuote");
		firstStep.setContent("Add a single or multiple caller that you define a quote manager"
				+ "If insert an user digit @ before,if insert a role digit # and $ if you want insert a service");
		firstStep.setTitle("Add Caller");
		firstStep.centerXOffset();
		firstStep.centerArrowOffset();

		TourStep secondStep = new TourStep(Placement.TOP, "idAddTimeQuote");
		secondStep.setContent("Select an interval time for quote ");
		secondStep.setTitle("Specify a Time ");
		secondStep.centerXOffset();
		secondStep.centerArrowOffset();

		TourStep thirdStep = new TourStep(Placement.TOP, "idAddTypeQuote");
		thirdStep.setContent("Select  a type for quote ");
		thirdStep.setTitle("Specify a Type");
		thirdStep.centerXOffset();
		thirdStep.centerArrowOffset();

		TourStep fourthStep = new TourStep(Placement.TOP, "idAddValueQuote");
		fourthStep.setContent("Select  a value for quote ");
		fourthStep.setTitle("Specify a Quota");
		fourthStep.centerXOffset();
		fourthStep.centerArrowOffset();
		tourAddDialogQuote.addStep(firstStep);
		tourAddDialogQuote.addStep(secondStep);
		tourAddDialogQuote.addStep(thirdStep);
		tourAddDialogQuote.addStep(fourthStep);
		tourAddDialogQuote.startTour();

	} 

	/**
	 * Click all user 
	 * @param e ClickEvent
	 */
	@UiHandler("b_caller_all_user")
	void onClickAddAllUser(ClickEvent e) {
		if (!b_caller_all_user.isToggled()){
			//selected checkbox all user
			b_caller_all_user.setFocus(false);
			b_caller_all_role.setFocus(false);
			b_caller_all_role.setActive(false);
			b_add_multiple_caller.setEnabled(false);
			b_caller_except.setEnabled(true);
			b_caller_except.setActive(false);
			Caller allCallerUser= new Caller(TypeCaller.user,"ALL");
			ListWidget.addCaller(allCallerUser);
		}
		else{
			//not selected checkbox all user
			b_caller_all_role.setEnabled(true);	
			b_add_multiple_caller.setEnabled(true);
			b_caller_except.setEnabled(false);
			ListWidget.clearList();
		}
	}
	/**
	 * Click all role 
	 * @param e ClickEvent
	 */
	@UiHandler("b_caller_all_role")
	void onClickAddAllRole(ClickEvent e) {
		if (!b_caller_all_role.isToggled()){
			//selected checkbox all role
			b_caller_all_role.setFocus(false);
			b_caller_all_user.setFocus(false);
			b_caller_all_user.setActive(false);

			b_add_multiple_caller.setEnabled(false);
			b_caller_except.setEnabled(true);
			b_caller_except.setActive(false);

			Caller allCallerRole= new Caller(TypeCaller.role,"ALL");
			ListWidget.addCaller(allCallerRole);
		}
		else{
			//not selected checkbox all role
			b_caller_all_user.setEnabled(true);	
			b_add_multiple_caller.setEnabled(true);
			b_caller_except.setEnabled(false);
			ListWidget.clearList();

		}

	}

	/**
	 * Click except 
	 * @param e ClickEvent
	 */
	@UiHandler("b_caller_except")
	void onClickExcept(ClickEvent e) {
		//selected except
		if (!b_caller_except.isToggled())
			ListWidget.enabledList();
		else{			
			ListWidget.clearList();
			if (!b_caller_all_role.isToggled()){
				Caller allCallerUser= new Caller(TypeCaller.user,"ALL");
				ListWidget.addCaller(allCallerUser);
			}
			else if (!b_caller_all_user.isToggled()){
				Caller allCallerRole= new Caller(TypeCaller.role,"ALL");
				ListWidget.addCaller(allCallerRole);
			}
		}
	}


	/**
	 * Click open dialog for multiple caller
	 * @param e
	 */
	@UiHandler("b_add_multiple_caller")
	void onClickAddCaller(ClickEvent e) {

		DialogAddMultipleCallerUserRole popup = new DialogAddMultipleCallerUserRole();
		popup.initList(ListWidget.callerSelected,this);
		popup.show();

	}

	/**
	 * Click close dialog
	 * @param e
	 */
	@UiHandler("b_exit_dialog_quota")
	void onClickExitDialogQuote(ClickEvent e) {
		this.hide();
		this.clear();
	}

	/**
	 * Click add policy 
	 * @param e
	 */

	@UiHandler("b_save_quota")
	void onClickSaveQuote(ClickEvent e) {


		if (ListWidget.callerSelected.isEmpty()){
			if (!error_display)
				ErrorText("Insert Caller");
			error_display=true;

		}
		else{
			error_display=false;

			//TimeInterval timeIntervalQuota=TimeInterval.valueOf(l_time_interval_quota.getSelectedValue());
			int indexI = l_time_interval_quota.getSelectedIndex();
			TimeInterval	timeIntervalQuota =TimeInterval.valueOf(l_time_interval_quota.getValue(indexI));
			
			//ManagerType typeQuota=ManagerType.valueOf(l_type_quota.getSelectedValue());
			int indexQ = l_type_quota.getSelectedIndex();
			ManagerType	typeQuota =ManagerType.valueOf(l_type_quota.getValue(indexQ));
			
			Double  valueQuota=Double.parseDouble(t_value_quota.getText());
			if (listModifyQuote.isEmpty()){
				//quota to be added
				//verify if a insert simple or exception
				if (b_caller_except.isToggled()){
					GWT.log("AuthManager - Insert new quota with execption and type:"+typeQuota);

					Quote quote= new Quote();
					List<Caller> callerList=new ArrayList<Caller>();
					//order a list caller
					if (ListWidget.callerSelected.size() > 0) {
						Collections.sort(ListWidget.callerSelected, new Comparator<Caller>() {
							@Override
							public int compare(final Caller o1, final Caller o2) {
								return o1.getCallerName().compareTo(o2.getCallerName());
							}
						} );
					}
					for (Caller caller:ListWidget.callerSelected){
					//	GWT.log("add"+caller.getCallerName());
						callerList.add(caller);
					}
					quote.setCaller(callerList);
					quote.setManager(typeQuota); 
					quote.setTimeInterval(timeIntervalQuota);
					quote.setQuota(valueQuota);
					listAddQuote=new ArrayList<Quote>();
					listAddQuote.add(quote);
					AuthManagerController.eventBus.fireEvent(new AddQuoteEvent(listAddQuote, this));
				}
				else{
					listAddQuote=new ArrayList<Quote>();
					for (Caller caller:ListWidget.callerSelected){
						GWT.log("AuthManager - Insert new quote "+caller.getCallerName()+" and type:"+typeQuota);
						Quote quote= new Quote();
						List<Caller> callerList=new ArrayList<Caller>();
						callerList.add(caller);
						quote.setCaller(callerList);
						quote.setManager(typeQuota); 
						quote.setTimeInterval(timeIntervalQuota);
						quote.setQuota(valueQuota);
						listAddQuote.add(quote);
					}
					AuthManagerController.eventBus.fireEvent(new AddQuoteEvent(listAddQuote, this));
				}
			}
			else{
				for (Quote modifyQuote:listModifyQuote){
					Long idQuote=modifyQuote.getIdQuote();
					GWT.log("AuthManager - Update quote:"+idQuote);
					modifyQuote.setManager(typeQuota); 
					modifyQuote.setTimeInterval(timeIntervalQuota);
					modifyQuote.setQuota(valueQuota);
					AuthManagerController.eventBus.fireEvent(new UpdateQuoteEvent(modifyQuote,this));
				}
			}
		}
	}
	/**
	 *  Array list for policy modified
	 * 	@param listPolicy
	 */

	public void setModifyQuote(ArrayList<Quote> listQuote) {

		String timeInterval = null;
		String managerType = null;
		String quota = null;

		this.listModifyQuote = listQuote;
		for (Quote result :listQuote){

			List<Caller> callerList= result.getCaller();
			timeInterval = result.getTimeInterval().toString();
			managerType=result.getManager().toString();

			//String target=result.getTarget();
			quota =result.getQuota().toString();

			ListWidget.addListCaller(callerList,false);
			GWT.log("AuthManager - Modify existing Quote identifier:"+result.getIdQuote()+" from caller:"+result.getCallerAsString());
		}

		//DISABLE ADD ANOTHER CALLER
		b_caller_all_user.setEnabled(false);
		b_caller_all_role.setEnabled(false);		
		b_add_multiple_caller.setEnabled(false);

		/*
		for (TimeInterval time : TimeInterval.values()) {
			l_time_interval_quota.addItem(time.toString());

		}
		 */
		l_time_interval_quota.setSelectedValue(timeInterval);

		/*
		for (ManagerType manager : ManagerType.values()) {
			l_type_quota.addItem(manager.toString());
		}
		 */
		l_type_quota.setSelectedValue(managerType);		
		t_value_quota.setText(quota);

	}
	/**
	 * Retrieve information from dialog multiple caller
	 * @param listCaller
	 */
	public void setListCaller(List<Caller> listCaller){
		ListWidget.addListCaller(listCaller,true);

	}
	/**
	 * Alert for msg error 
	 * @param stringMsg
	 */
	public void ErrorText(String stringMsg) {
		Alert msg = new Alert();
		msg.setAnimation(true);
		msg.setText(stringMsg);
		msg.addClosedHandler(new ClosedHandler<AlertBase>() {

			@Override
			public void onClosed(ClosedEvent<AlertBase> event) {
				// TODO Auto-generated method stub
				error_display=false;
			}
		});
		r_loader_space.add(msg);
	}

	/**
	 * Start Animation loading 
	 */
	public void AppLoadingView() {

		b_exit_dialog_quota.setEnabled(false);
		b_save_quota.setEnabled(false);
		Image imgLoading = new Image(AuthResources.INSTANCE.loaderIcon());
		r_loader_space.clear();
		r_loader_space.add(imgLoading);
	}

	/**
	 * Stop animation loading
	 */
	public void StopAppLoadingView() {
		// TODO Auto-generated method stub
		b_exit_dialog_quota.setEnabled(true);
		b_save_quota.setEnabled(true);
		clear();
		hide();
	}
}