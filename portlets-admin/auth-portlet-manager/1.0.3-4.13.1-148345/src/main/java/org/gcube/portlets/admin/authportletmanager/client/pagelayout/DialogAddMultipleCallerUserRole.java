package org.gcube.portlets.admin.authportletmanager.client.pagelayout;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.admin.authportletmanager.client.Entities;
import org.gcube.portlets.admin.authportletmanager.client.widget.PickList;
import org.gcube.portlets.admin.authportletmanager.client.widget.WindowBox;
import org.gcube.portlets.admin.authportletmanager.shared.Caller;
import org.gcube.portlets.admin.authportletmanager.shared.Caller.TypeCaller;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog Box for add multiple caller for one policy or quota
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */

public class DialogAddMultipleCallerUserRole extends WindowBox   {

	private static final Binder binder = GWT.create(Binder.class);
	interface Binder extends UiBinder<Widget, DialogAddMultipleCallerUserRole> {
	}

	@UiField
	Button b_exit_dialog_caller;

	@UiField
	Button b_add_caller;

	@UiField
	PickList pickList;

	@UiField
	ListBox l_select_type_caller;

	public PolicyAddDialog dialogPolicy;

	public QuoteAddDialog dialogQuota;

	public int typeMaster =0;

	public DialogAddMultipleCallerUserRole() {
		this.setWidget(binder.createAndBindUi(this));
		this.setGlassEnabled(true);
		this.setWidth("450px");
		this.setText("Add/Remove User or Role");
		this.setAnimationEnabled(isVisible());
		this.center();
		//load Handler for service class
		l_select_type_caller.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				int indexC = l_select_type_caller.getSelectedIndex();
				String	typeCaller =l_select_type_caller.getValue(indexC);
				GWT.log("AuthManager - Add filter select:"+typeCaller);
				if (typeCaller.equals("All")){
					pickList.clearLeftList();
					ArrayList<Caller> listCaller=new ArrayList<Caller>();
					for (Caller caller :Entities.getCallers()){
						if (!caller.getTypecaller().equals(TypeCaller.service))
							listCaller.add(caller);
					}
					pickList.setLeftListElements(listCaller);
				}
				else{

					List <Caller> filterCaller=new ArrayList<Caller>();
					for (Caller caller:Entities.getCallers()){
						if (caller.getTypecaller().toString().toLowerCase().equals(typeCaller.toLowerCase())) 
							filterCaller.add(caller);
					}
					pickList.clearLeftList();
					pickList.setLeftListElements(filterCaller);

				}

			}
		});
	}
	/**
	 * Click close dialog
	 * @param e
	 */
	@UiHandler("b_exit_dialog_caller")
	void onClickDialogExitCaller(ClickEvent e) {
		this.hide();
		this.clear();
	}
	/**
	 * Click for add a multiple caller
	 * @param e
	 */
	@UiHandler("b_add_caller")
	void onClickDialogAddCaller(ClickEvent e) {
		//resume list
		if (typeMaster==1)
			dialogPolicy.setListCallerUserRole(pickList.getRightListElements());
		if (typeMaster==2)
			dialogQuota.setListCaller(pickList.getRightListElements());
		this.hide();
		this.clear();
	}

	/**
	 * Init for load a multiple caller with default caller selected
	 * @param callerSelected
	 * @param dialog
	 */
	public void initList(List<Caller> callerSelected, PolicyAddDialog dialog) {
		this.dialogPolicy=dialog;
		//configure master for policy
		typeMaster=1;
		ArrayList<Caller> filterList=new ArrayList<Caller>();
		for (Caller caller :Entities.getCallers()){
			if (!caller.getTypecaller().equals(TypeCaller.service)){
				if (!callerSelected.contains(caller))
					filterList.add(caller);
			}			
		}
		pickList.setLeftListElements(filterList);

		ArrayList<Caller> filterListSelected=new ArrayList<Caller>();
		for (Caller caller :callerSelected){
			if (!caller.getTypecaller().equals(TypeCaller.service))
				filterListSelected.add(caller);
		}
		pickList.setRightListElements(filterListSelected);

	}


	/**
	 * Init for load a multiple caller with default caller selected
	 * @param callerSelected
	 * @param dialog
	 */
	public void initList(List<Caller> callerSelected, QuoteAddDialog dialog) {
		this.dialogQuota=dialog;
		//configure master for quota
		typeMaster=2;
		ArrayList<Caller> filterList=new ArrayList<Caller>();
		for (Caller caller :Entities.getCallers()){
			if (!caller.getTypecaller().equals(TypeCaller.service)){
				if (!callerSelected.contains(caller))
					filterList.add(caller);
			}			
		}
		pickList.setLeftListElements(filterList);
		ArrayList<Caller> filterListSelected=new ArrayList<Caller>();
		for (Caller caller :callerSelected){
			if (!caller.getTypecaller().equals(TypeCaller.service))
				filterListSelected.add(caller);
		}
		pickList.setRightListElements(callerSelected);
	}
}
