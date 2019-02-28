package org.gcube.portlets.admin.authportletmanager.client.pagelayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.gcube.portlets.admin.authportletmanager.client.AuthManagerController;
import org.gcube.portlets.admin.authportletmanager.client.Entities;
import org.gcube.portlets.admin.authportletmanager.client.event.AddPoliciesEvent;
import org.gcube.portlets.admin.authportletmanager.client.event.UpdatePolicyEvent;
import org.gcube.portlets.admin.authportletmanager.client.resource.AuthResources;
import org.gcube.portlets.admin.authportletmanager.client.widget.InputListWidget;
import org.gcube.portlets.admin.authportletmanager.client.widget.WindowBox;
import org.gcube.portlets.admin.authportletmanager.shared.Access;
import org.gcube.portlets.admin.authportletmanager.shared.Caller;
import org.gcube.portlets.admin.authportletmanager.shared.Caller.TypeCaller;
import org.gcube.portlets.admin.authportletmanager.shared.ConstantsSharing;
import org.gcube.portlets.admin.authportletmanager.shared.PolicyAuth;
import org.gcube.portlets.admin.authportletmanager.shared.Service;
import org.gcube.portlets.widgets.widgettour.client.extendedclasses.GCubeTour;

import com.ait.toolkit.hopscotch.client.Placement;
import com.ait.toolkit.hopscotch.client.TourStep;
import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ButtonGroup;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Row;
import com.github.gwtbootstrap.client.ui.base.AlertBase;
import com.github.gwtbootstrap.client.ui.event.ClosedEvent;
import com.github.gwtbootstrap.client.ui.event.ClosedHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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

public class PolicyAddDialog extends WindowBox {

	private static final Binder binder = GWT.create(Binder.class);
	interface Binder extends UiBinder<Widget, PolicyAddDialog> {
	}

	private Boolean error_display=false;

	private ArrayList<PolicyAuth>listModifyPolicy;

	private Boolean modifyPolicy=false;

	private ArrayList<PolicyAuth>listAddPolicy;

	public InputListWidget ListWidget; 

	@UiField
	FlowPanel text_caller_policy;
	@UiField
	ListBox l_service_class_policy;
	@UiField
	ListBox l_service_name_policy;
	@UiField
	ListBox l_service_id_policy;
	@UiField
	ListBox l_access_policy;
	@UiField
	ButtonGroup b_caller_checkbox;
	@UiField
	Button b_caller_all_user;
	@UiField
	Button b_caller_all_role;	
	@UiField
	Button b_caller_except;
	@UiField
	Button b_add_multiple_user_role;
	@UiField
	Button b_add_multiple_service;
	@UiField
	Button b_exit_dialog_policy;
	@UiField
	Button b_save_policy;
	@UiField
	Row  r_loader_space;

	@UiField
	FlowPanel idAddCallerPolicy;
	@UiField
	FlowPanel idAddServicePolicy;
	@UiField
	FlowPanel idAddAccessPolicy;

	public PolicyAddDialog() {

		super(true, false);
		this.setWidget(binder.createAndBindUi(this));
		//this.setGlassEnabled(true);
		this.setWidth("800px");
		this.setAnimationEnabled(true);
		this.setGlassEnabled(true);
		this.center();
		this.setText("Add/Update Policy");		

		idAddCallerPolicy.getElement().setId("idAddCallerPolicy");
		idAddServicePolicy.getElement().setId("idAddServicePolicy");
		idAddAccessPolicy.getElement().setId("idAddAccessPolicy");


		//istance a new list policy for modify
		listModifyPolicy=new ArrayList<PolicyAuth>();

		//Initialize a suggest for caller
		text_caller_policy.setStyleName("auto_suggest");
		ListWidget=new InputListWidget();
		text_caller_policy.add(ListWidget);
		//disable except
		b_caller_except.setEnabled(false);
		//load Handler for service class
		l_service_class_policy.addChangeHandler(new ChangeHandler() {
			@SuppressWarnings("rawtypes")
			public void onChange(ChangeEvent event) {
				int indexC = l_service_class_policy.getSelectedIndex();
				String	newValue =l_service_class_policy.getValue(indexC);
				l_service_name_policy.clear();
				if (newValue.equals(ConstantsSharing.Star)){

					l_service_name_policy.setEnabled(false);
					l_service_name_policy.addItem(ConstantsSharing.StarLabel,ConstantsSharing.Star);
					l_service_id_policy.setEnabled(false);
				}
				else{
					l_service_name_policy.setEnabled(true);
					l_service_id_policy.setEnabled(true);
					//order by service name
					@SuppressWarnings("unchecked")
					List<String> sortedServiceName=new ArrayList(Entities.getServicesMap().get(newValue));
					Collections.sort(sortedServiceName);
					//filter distinct service
					List<String> distinctServiceName = new ArrayList<String>();
					l_service_name_policy.addItem(ConstantsSharing.StarLabel,ConstantsSharing.Star);
					for (String serviceName :sortedServiceName )
					{
						
						if (!distinctServiceName.contains(serviceName)){
							
							l_service_name_policy.addItem(serviceName);
							distinctServiceName.add(serviceName);
						}
					}
				}
			}
		});
		loadListService();
		loadListAccessPolicy();
		r_loader_space.clear();
		GCubeTour tourAddDialogPolicy = new GCubeTour("tour-auth add dialog", "auth-portlet-manager", 1, "Would you like to see again this tour next time ?", "Tour Policy use");
		tourAddDialogPolicy.setShowPrevButton(true);
		if (!this.modifyPolicy){
			TourStep firstStep = new TourStep(Placement.RIGHT, "idAddCallerPolicy");
			firstStep.setContent("A single or multiple caller can be added to deny access on a selected service:"
					+ "<br>To insert a user digit @,<br>To insert a role digit #, <br>To insert a service digit $.");
			firstStep.setTitle("Add Caller");
			firstStep.centerXOffset();
			firstStep.centerArrowOffset();
			tourAddDialogPolicy.addStep(firstStep);
		}
		TourStep secondStep = new TourStep(Placement.TOP, "idAddServicePolicy");
		secondStep.setTitle("Select Service");
		secondStep.setContent("Specify a service class and service name ");
		secondStep.centerXOffset();
		secondStep.centerArrowOffset();

		TourStep thirdStep = new TourStep(Placement.TOP, "idAddAccessPolicy");
		thirdStep.setTitle("Select Access");
		thirdStep.setContent("Specify an acces type");
		thirdStep.centerXOffset();
		thirdStep.centerArrowOffset();
		
		tourAddDialogPolicy.addStep(secondStep);
		tourAddDialogPolicy.addStep(thirdStep);
		tourAddDialogPolicy.startTour();

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

			b_add_multiple_user_role.setEnabled(false);
			b_add_multiple_service.setEnabled(false);
			b_caller_except.setEnabled(true);
			b_caller_except.setActive(false);

			//Caller allCallerUser= new Caller("user","ALL");
			Caller allCallerUser= new Caller(TypeCaller.user,"ALL");
			ListWidget.clearList();
			ListWidget.addCaller(allCallerUser);
		}
		else{
			//not selected checkbox all user
			b_caller_all_role.setEnabled(true);	
			b_add_multiple_user_role.setEnabled(true);
			b_add_multiple_service.setEnabled(true);
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

			b_add_multiple_user_role.setEnabled(false);
			b_add_multiple_service.setEnabled(false);
			b_caller_except.setEnabled(true);
			b_caller_except.setActive(false);

			Caller allCallerRole= new Caller(TypeCaller.role,"ALL");
			ListWidget.clearList();
			ListWidget.addCaller(allCallerRole);
		}
		else{
			//not selected checkbox all role
			b_caller_all_user.setEnabled(true);	
			b_add_multiple_user_role.setEnabled(true);
			b_add_multiple_service.setEnabled(true);
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
	 * Click open dialog for multiple caller user or role
	 * @param e
	 */
	@UiHandler("b_add_multiple_user_role")
	void onClickAddCaller(ClickEvent e) {
		DialogAddMultipleCallerUserRole popup = new DialogAddMultipleCallerUserRole();
		popup.initList(ListWidget.callerSelected,this);
		popup.show();	
	}
	/**
	 * Click open dialog for multiple caller service
	 * @param e
	 */
	@UiHandler("b_add_multiple_service")
	void onClickAddCallerService(ClickEvent e) {
		DialogAddMultipleCallerService popup = new DialogAddMultipleCallerService(ListWidget.callerSelected,this);
		//popup.initList();
		popup.show();
	}
	/**
	 * Click close dialog
	 * @param e
	 */
	@UiHandler("b_exit_dialog_policy")
	void onClickExitDialogPolicy(ClickEvent e) {
		this.hide();
		this.clear();
	}
	/**
	 * Click add policy 
	 * @param e
	 */
	@UiHandler("b_save_policy")
	void onClickSavePolicy(ClickEvent e) {
		//verify if a modify any 
		if (this.modifyPolicy){
			//verifico se ho cambiato qualcosa
			for (PolicyAuth verifyPolicy:listModifyPolicy){
				String	serviceClass =l_service_class_policy.getValue(l_service_class_policy.getSelectedIndex());
				String	serviceName =l_service_name_policy.getValue(l_service_name_policy.getSelectedIndex());
				String	serviceId =l_service_id_policy.getValue(l_service_id_policy.getSelectedIndex());
				Service service=new Service(serviceClass,serviceName,serviceId);
				Access	access =Access.valueOf(l_access_policy.getValue(l_access_policy.getSelectedIndex()));
				if ((verifyPolicy.getService().equals(service) )&& (verifyPolicy.getAccess().equals(access))){
					if (!error_display)
						ErrorText("Not changed");
					error_display=true;
					return ;
				}
			}
		}

		if (ListWidget.callerSelected.isEmpty()){
			if (!error_display)
				ErrorText("Insert Caller");
			error_display=true;
		}
		else{
			error_display=false;
			String	serviceClass =l_service_class_policy.getValue(l_service_class_policy.getSelectedIndex());
			String	serviceName =l_service_name_policy.getValue(l_service_name_policy.getSelectedIndex());
			String	serviceId =l_service_id_policy.getValue(l_service_id_policy.getSelectedIndex());
			Service service=new Service(serviceClass,serviceName,serviceId);
			//Access access = Access.valueOf(l_access_policy.getSelectedValue());
			int indexA = l_access_policy.getSelectedIndex();
			Access	access =Access.valueOf(l_access_policy.getValue(indexA));
			if (listModifyPolicy.isEmpty()){
				//policy to be added
				//verify if a insert simple or exception
				if (b_caller_except.isToggled()){
					GWT.log("AuthManager - Insert new policy with execption and service:"+service.getServiceClass());
					PolicyAuth policy= new PolicyAuth();
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
						GWT.log("add"+caller.getCallerName());
						callerList.add(caller);
					}
					policy.setCaller(callerList);
					policy.setService(service);
					policy.setAccess(access);
					policy.setExcludesCaller(true);
					listAddPolicy=new ArrayList<PolicyAuth>();
					listAddPolicy.add(policy);
					AuthManagerController.eventBus.fireEvent(new AddPoliciesEvent(listAddPolicy, this));
				}
				else{
					listAddPolicy=new ArrayList<PolicyAuth>();
					for (Caller caller:ListWidget.callerSelected){
						GWT.log("AuthManager - Insert new policy:"+caller.getCallerName()+"  with type:"+caller.getTypecaller().toString()+"and service:"+service.getServiceClass()+" and access:"+access.toString());
						PolicyAuth policy= new PolicyAuth();
						List<Caller> callerList=new ArrayList<Caller>();
						callerList.add(caller);
						policy.setCaller(callerList);
						policy.setService(service);
						policy.setAccess(access);
						listAddPolicy.add(policy);
					}
					AuthManagerController.eventBus.fireEvent(new AddPoliciesEvent(listAddPolicy, this));
				}
			}
			else{
				for (PolicyAuth modifyPolicy:listModifyPolicy){
					Long idPolicy=modifyPolicy.getIdpolicy();
					GWT.log("AuthManager - Update policy:"+idPolicy);
					modifyPolicy.setService(service);
					modifyPolicy.setAccess(access);
					AuthManagerController.eventBus.fireEvent(new UpdatePolicyEvent(modifyPolicy,this));
				}
			}
		}
	}

	/**
	 *  Array list for policy modified
	 * 	@param listPolicy
	 */
	public void setModifyPolicy(ArrayList<PolicyAuth> listPolicy) {

		String serviceClass = null;
		String serviceName = null;
		String serviceId = null;
		Access access = null;
		this.listModifyPolicy = listPolicy;
		this.modifyPolicy=true;
		for (PolicyAuth result :listPolicy){
			List<Caller> callerList= result.getCaller();
			Service service = result.getService();
			serviceClass=service.getServiceClass();
			serviceName=service.getServiceName();
			serviceId=service.getServiceId();
			access=result.getAccess();

			if (result.getExcludesCaller()){
				TypeCaller typeCaller=result.getCallerType();
				callerList.add(new Caller(typeCaller,"ALL"));
			}
			ListWidget.addListCaller(callerList, false);
			GWT.log("AuthManager - Modify existing Policy identifier:"+result.getIdpolicy()+" from caller:"+result.getCallerAsString()+ "with access:"+access);
		}
		//DISABLE ADD ANOTHER CALLER
		b_caller_all_user.setEnabled(false);
		b_caller_all_role.setEnabled(false);		
		b_add_multiple_user_role.setEnabled(false);
		b_add_multiple_service.setEnabled(false);
		//RETRIVE A SERVICE SELECT
		
		GWT.log("access:"+serviceClass);
		
		if (!serviceClass.equals(ConstantsSharing.Star)){
			List<String> distinctServiceName = new ArrayList<String>();
			//l_service_name_policy.addItem(ConstantsSharing.StarLabel,ConstantsSharing.Star);
			for (String serviceNameList : 	Entities.getServicesMap().get(serviceClass)){
				if (!distinctServiceName.contains(serviceNameList)){
					l_service_name_policy.addItem(serviceNameList);
					distinctServiceName.add(serviceNameList);
				}
				
			}
			
			//selected the values ​​of policy
			l_service_class_policy.setSelectedValue(serviceClass);
			l_service_name_policy.setSelectedValue(serviceName);
			l_service_id_policy.setSelectedValue(serviceId);
		}
		l_access_policy.setSelectedValue(access.toString());
		l_service_name_policy.setEnabled(true);
	}
	/**
	 * Retrieve information from dialog multiple caller
	 * @param listCaller
	 */
	public void setListCallerUserRole(List<Caller> listCaller){

		List<Caller> selectedCaller= new ArrayList<Caller>();
		for (Caller caller:	ListWidget.callerSelected){
			if (caller.getTypecaller().equals(TypeCaller.service)){
				selectedCaller.add(caller);
			}
		}
		selectedCaller.addAll(listCaller);
		ListWidget.replaceListCaller(selectedCaller);
	}
	/**
	 * 
	 * @param listCaller
	 */
	public void setListCallerService(List<Caller> listCaller) {
		List<Caller> selectedCaller= new ArrayList<Caller>();
		for (Caller caller:	ListWidget.callerSelected){
			if ((caller.getTypecaller().equals(TypeCaller.user))||(caller.getTypecaller().equals(TypeCaller.role))){
				selectedCaller.add(caller);
			}
		}
		selectedCaller.addAll(listCaller);
		ListWidget.replaceListCaller(selectedCaller);
	}


	/**
	 *  Load a service into select service class 
	 */
	public void loadListService(){
		l_service_class_policy.addItem(ConstantsSharing.StarLabel,ConstantsSharing.Star);

		l_service_name_policy.setEnabled(false);
		l_service_name_policy.addItem(ConstantsSharing.StarLabel,ConstantsSharing.Star);
		l_service_id_policy.setEnabled(false);

		//order by serviceClass 
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<String> sortedServiceClass=new ArrayList(Entities.getServicesMap().keySet());
		Collections.sort(sortedServiceClass);
		for (String serviceClass :sortedServiceClass )
		{
			l_service_class_policy.addItem(serviceClass);	
		}
		l_service_id_policy.addItem(ConstantsSharing.StarLabel,ConstantsSharing.Star);
	}

	/**
	 *  Load a list access into select access 
	 */
	public void loadListAccessPolicy(){
		l_access_policy.ensureDebugId("l_access_policy");	
		l_access_policy.setHeight("30px");
		l_access_policy.setVisibleItemCount(1);
		//order by Access
		/*
		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<String> sortedAccess=new ArrayList(Entities.getAccess());
		Collections.sort(sortedAccess);
		 */
		for( Access access : Access.values() ) {
			l_access_policy.addItem(access.toString());

		}
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
				error_display=false;
			}
		});
		r_loader_space.add(msg);
	}

	/**
	 * Start Animation loading 
	 */
	public void AppLoadingView() {

		b_exit_dialog_policy.setEnabled(false);
		b_save_policy.setEnabled(false);
		Image imgLoading = new Image(AuthResources.INSTANCE.loaderIcon());
		r_loader_space.clear();
		r_loader_space.add(imgLoading);
	}

	/**
	 * Stop animation loading
	 */
	public void StopAppLoadingView() {		
		b_exit_dialog_policy.setEnabled(true);
		b_save_policy.setEnabled(true);
		clear();
		hide();
	}
}