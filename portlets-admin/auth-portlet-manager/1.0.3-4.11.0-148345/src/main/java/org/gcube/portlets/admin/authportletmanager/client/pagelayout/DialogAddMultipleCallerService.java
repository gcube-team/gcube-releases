package org.gcube.portlets.admin.authportletmanager.client.pagelayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.gcube.portlets.admin.authportletmanager.client.Entities;
import org.gcube.portlets.admin.authportletmanager.client.widget.WindowBox;
import org.gcube.portlets.admin.authportletmanager.shared.Caller;
import org.gcube.portlets.admin.authportletmanager.shared.Caller.TypeCaller;
import org.gcube.portlets.admin.authportletmanager.shared.ConstantsSharing;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog Box for add multiple caller for one policy or quota
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 * 
 */

public class DialogAddMultipleCallerService extends WindowBox   {

	private static final Binder binder = GWT.create(Binder.class);
	interface Binder extends UiBinder<Widget, DialogAddMultipleCallerService> {
	}

	@UiField
	Button b_exit_dialog_caller;

	@UiField
	Button b_add_caller;

	@UiField
	ListBox l_service_class_policy;
	@UiField
	ListBox l_service_name_policy;
	@UiField
	ListBox l_service_id_policy;


	public ListBox rightList= new ListBox(true);
	@UiField
	VerticalPanel leftPanel;
	@UiField
	VerticalPanel rightPanel;
	@UiField
	VerticalPanel buttonPanel;

	@UiField
	Button toRightButton;
	@UiField
	Button allToLeftButton;
	@UiField
	Button toLeftButton;


	public PolicyAddDialog dialogPolicy;

	public QuoteAddDialog dialogQuota;

	public int typeMaster =0;

	public DialogAddMultipleCallerService(List<Caller> callerSelected,PolicyAddDialog policyAddDialog) {
		this.setWidget(binder.createAndBindUi(this));
		//this.setAutoHideEnabled(true);
		this.setGlassEnabled(true);

		this.setWidth("450px");
		this.setText("Add/Remove Service");
		this.setAnimationEnabled(isVisible());
		this.center();

		rightList.setVisibleItemCount(10);
		rightList.setSize(12);
		rightList.setStyleName("width_select");
		rightPanel.add(rightList);

		//set padding between cells to make the component look better
		this.getElement().setAttribute("cellpadding", "1");
		leftPanel.getElement().setAttribute("cellpadding", "1");
		buttonPanel.getElement().setAttribute("cellpadding", "2");
		rightPanel.getElement().setAttribute("cellpadding", "1");
		l_service_class_policy.addChangeHandler(new ChangeHandler() {
			@SuppressWarnings("rawtypes")
			public void onChange(ChangeEvent event) {
				//String newValue = l_service_class_policy.getSelectedValue();
				int index = l_service_class_policy.getSelectedIndex();
				String	newValue =l_service_class_policy.getValue(index);
				l_service_name_policy.clear();
				if (newValue.equals(ConstantsSharing.Star)){
					l_service_name_policy.setEnabled(false);
					l_service_name_policy.addItem(ConstantsSharing.Star);
					l_service_id_policy.setEnabled(false);
				}
				else{
					l_service_name_policy.setEnabled(true);
					l_service_id_policy.setEnabled(true);
					//order by service name
					@SuppressWarnings("unchecked")
					List<String> sortedServiceName=new ArrayList(Entities.getServicesMap().get(newValue));
					Collections.sort(sortedServiceName);
					for (String serviceName :sortedServiceName )
					{
						l_service_name_policy.addItem(serviceName);	
					}
				}
			}
		});
		initList(callerSelected,policyAddDialog);
		loadListService();
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
	 * Click for add a multiple caller service
	 * @param e
	 */
	@UiHandler("b_add_caller")
	void onClickDialogAddCaller(ClickEvent e) {
		//resume list
		List <Caller> selectedCallerService =new ArrayList<Caller>();
		for(int itemIndex=0; itemIndex<rightList.getItemCount(); itemIndex++) {
			String item = rightList.getItemText(itemIndex);
			//String valueItem = rightList.getValue(itemIndex);
			Caller caller=new Caller(TypeCaller.service, item);
			selectedCallerService.add(caller);
		}
		dialogPolicy.setListCallerService(selectedCallerService);
		this.hide();
		this.clear();
	}

	@UiHandler("toLeftButton")
	public void toLeftButtonClicked(ClickEvent event) {
		String value=rightList.getValue();
		if (value == null) {
			Window.alert("Select an item first!");
			return;
		}
		if (rightList.getItemCount() >= 1) { // !>= 1! is preferred instead of !== 1! to handle multiple selections
			toLeftButton.setEnabled(true);
		}
		SortedMap<String, String> toTempList=new TreeMap<String,String>();
		for(int itemIndex=0; itemIndex<rightList.getItemCount(); itemIndex++) {
			if (rightList.isItemSelected(itemIndex)){
				//String item = rightList.getItemText(itemIndex);
				//String valueItem = rightList.getValue(itemIndex);
			}
			else{
				String item = rightList.getItemText(itemIndex);
				String valueItem=rightList.getValue(itemIndex);
				toTempList.put(item,valueItem);
			}
		}
		// order by type checklist from
		rightList.clear();
		@SuppressWarnings("unchecked")
		Map<String, String> mapFrom = sortByValues(toTempList); 
		for (Entry<String, String> entry : mapFrom.entrySet())
		{
			//rightList.insertItem(item, index);
			rightList.addItem(entry.getKey(),entry.getValue());
			GWT.log("Key"+entry.getKey()+"Value"+entry.getValue());
		}
	}

	@UiHandler("allToLeftButton")
	public void allToLeftButtonClicked(ClickEvent event) {
		rightList.clear();
	}
	@UiHandler("toRightButton")
	public void toRightButtonClicked(ClickEvent event) {
		//select the selected value and insert into list
		int indexC = l_service_class_policy.getSelectedIndex();
		String	serviceClass =l_service_class_policy.getValue(indexC);
		
		int indexN = l_service_name_policy.getSelectedIndex();
		String	serviceName =l_service_name_policy.getValue(indexN);
		
		int indexI = l_service_id_policy.getSelectedIndex();
		String	serviceId =l_service_id_policy.getValue(indexI);
		
		String serviceSelected=serviceClass+":"+serviceName+":"+serviceId;
		
		SortedMap<String, String> toTempList=new TreeMap<String,String>();
		for(int itemIndex=0; itemIndex<rightList.getItemCount(); itemIndex++) {
				String item = rightList.getItemText(itemIndex);
				String valueItem=rightList.getValue(itemIndex);
				toTempList.put(item,valueItem);
		}
		if (!toTempList.containsKey(serviceSelected)){
				rightList.addItem(serviceSelected,TypeCaller.service.toString());
		}
	}

	/**
	 *  Load a service into select service class 
	 */
	protected void loadListService(){
		l_service_class_policy.addItem(ConstantsSharing.Star);

		l_service_name_policy.setEnabled(false);
		l_service_name_policy.addItem(ConstantsSharing.Star);
		l_service_id_policy.setEnabled(false);

		//order by serviceClass 
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<String> sortedServiceClass=new ArrayList(Entities.getServicesMap().keySet());
		Collections.sort(sortedServiceClass);
		for (String serviceClass :sortedServiceClass )
		{
			l_service_class_policy.addItem(serviceClass);	
		}
		l_service_id_policy.addItem(ConstantsSharing.Star);
	}

	protected void initList(List<Caller> callerSelected,PolicyAddDialog dialog) {
		//configure master for policy
		typeMaster=1;
		this.dialogPolicy=dialog;

		for (Caller caller :callerSelected){
			if (caller.getTypecaller().equals(TypeCaller.service))
				rightList.addItem(caller.getCallerName(), caller.getTypecaller().toString());
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static HashMap sortByValues(SortedMap<String, String> tempList) { 
		List list = new LinkedList(tempList.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		} 
		return sortedHashMap;
	}
}
