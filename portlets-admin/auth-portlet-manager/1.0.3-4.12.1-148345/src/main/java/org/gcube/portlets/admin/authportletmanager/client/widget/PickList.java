package org.gcube.portlets.admin.authportletmanager.client.widget;

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

import org.gcube.portlets.admin.authportletmanager.shared.Caller;
import org.gcube.portlets.admin.authportletmanager.shared.Caller.TypeCaller;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 * Usage Example:<br/>
 * <ul><li>
 * Put following code into your ui.xml file:<br/>
 * <code>
 *<bi:PickList ui:field="pickList" >
 * </code><br/><br/>
 *
 * </li><li>
 * Populate your picklist using {@link NameValuePairImpl}:<br/>
 * <code>
 *     List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();<br/>
 *     nameValuePairs.add(new NameValuePairImpl("item 1", "item_1"));<br/>
 *     nameValuePairs.add(new NameValuePairImpl("item 2", "item_2"));<br/>
 *     nameValuePairs.add(new NameValuePairImpl("item 3", "item_3"));<br/>
 *     pickList.setLeftListElements(nameValuePairs);
 *     pickList.setRightListElements(nameValuePairs);
 * </code><br/>
 * </li></ul>
 *
 * Screenshot:
 * <br/>
 * <img src="http://gamenism.com/gwt/picklist.png"/>
 * <br/>
 *
 * User: Halil Karakose
 * Date: 10/18/13
 * Time: 3:53 PM
 *
 * @see NameValuePairImpl
 */
public class PickList extends Composite {
	private static PickListUiBinder ourUiBinder = GWT.create(PickListUiBinder.class);


	public ListBox leftList= new ListBox(true);

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
	Button toLeftButton;
	@UiField
	Button allToRightButton;
	@UiField
	Button allToLeftButton;

	@UiField
	Label rightPanelLabel;
	@UiField
	Label leftPanelLabel;

	public PickList() {
		initWidget(ourUiBinder.createAndBindUi(this));


		leftList.setVisibleItemCount(10);
		leftList.setSize(12);
		leftList.setStyleName("width_select");
		leftPanel.add(leftList);



		rightList.setVisibleItemCount(10);
		rightList.setSize(12);
		rightList.setStyleName("width_select");
		rightPanel.add(rightList);

		//set padding between cells to make the component look better
		this.getElement().setAttribute("cellpadding", "1");
		leftPanel.getElement().setAttribute("cellpadding", "1");
		buttonPanel.getElement().setAttribute("cellpadding", "2");
		rightPanel.getElement().setAttribute("cellpadding", "1");

		setLeftListElements(new ArrayList<Caller>());
		setRightListElements(new ArrayList<Caller>());
	}

	public void clearLeftList() {
		clear(leftList);
	}

	public void clearRightList() {
		clear(rightList);
	}

	private void clear(ListBox listBox) {
		listBox.clear();
	}

	public void addElementToLeftList(Caller element) {
		addElement(leftList, element);
	}

	public void addElementToRightList(Caller element) {
		addElement(rightList, element);
	}

	private void addElement(ListBox listBox, Caller element) {
		listBox.addItem(element.getCallerName(), element.getTypecaller().toString());
	}

	public List<Caller> getLeftListElements() {
		return getListBoxElements(leftList);
	}

	public void setLeftListElements(List<Caller> elements) {

		ArrayList<Caller> callerFilter = new ArrayList<Caller>();
		List<Caller> callerSelected=new ArrayList<Caller>();
		callerSelected=getRightListElements();
		//verify a list elements not present in right list 
		for (Caller element : elements) {
			boolean trovato=false;
			for (Caller elementRight : callerSelected) {
				if (elementRight.equals(element)){
					trovato=true;
					break; 
				}


			}
			if (!trovato)	
				callerFilter.add(element);
		}
		populate(callerFilter, leftList);
	}

	public List<Caller> getRightListElements() {
		return getListBoxElements(rightList);
	}

	public void setRightListElements(List<Caller> elements) {
		populate(elements, rightList);
	}

	public String getSelectedLabelText() {
		return rightPanelLabel.getText();
	}

	public void setSelectedLabelText(String selectedLabelText) {
		rightPanelLabel.setText(selectedLabelText);
	}

	public String getCandidateLabelText() {
		return leftPanelLabel.getText();
	}

	public void setCandidateLabelText(String selectedLabelText) {
		leftPanelLabel.setText(selectedLabelText);
	}

	private List<Caller> getListBoxElements(ListBox listBox) {
		ArrayList<Caller> callerPairs = new ArrayList<Caller>();
		for (int i = 0; i < listBox.getItemCount(); i++) {
			TypeCaller typecaller =TypeCaller.valueOf(listBox.getValue(i));
			Caller nameValuePair = new Caller(typecaller,listBox.getItemText(i) );
			callerPairs.add(nameValuePair);
		}
		return callerPairs;
	}
	/**
	 * Popolate a leftlist element and sorted by Caller Type
	 * @param leftListElements
	 * @param listBox
	 */
	private void populate(List<Caller> leftListElements, ListBox listBox) {
		SortedMap<String, String> fromTempList=new TreeMap<String,String>();
		for (Caller element : leftListElements) {
				fromTempList.put(element.getCallerName(), element.getTypecaller().toString());
		}
		// order by type checklist from
		listBox.clear();
		@SuppressWarnings("unchecked")
		Map<String, String> mapFrom = sortByValues(fromTempList); 
		for (Entry<String, String> entry : mapFrom.entrySet())
			listBox.addItem(entry.getKey(),entry.getValue());
	}

	@UiHandler("toRightButton")
	public void toRightButtonClicked(ClickEvent event) {
		moveItem(leftList, rightList, event);
		if (leftList.getItemCount() == 0) {
			toRightButton.setEnabled(false);
			allToRightButton.setEnabled(false);
		}

		if (rightList.getItemCount() >= 1) { // !>= 1! is preferred instead of !== 1! to handle multiple selections
			toLeftButton.setEnabled(true);
			allToLeftButton.setEnabled(true);
		}
	}

	@UiHandler("allToRightButton")
	public void allToRightButtonClicked(ClickEvent event) {
		moveAllItem(leftList, rightList, event);		
		allToRightButton.setEnabled(false);
		toRightButton.setEnabled(false);
		
		if (rightList.getItemCount() >= 1) { // !>= 1! is preferred instead of !== 1! to handle multiple selections
			allToLeftButton.setEnabled(true);
			toLeftButton.setEnabled(true);
			
		}
	}

	@UiHandler("toLeftButton")
	public void toLeftButtonClicked(ClickEvent event) {
		moveItem(rightList, leftList, event);
		if (rightList.getItemCount() == 0) {
			toLeftButton.setEnabled(false);
			allToLeftButton.setEnabled(false);
		}

		if (leftList.getItemCount() >= 1) { // !>= 1! is preferred instead of !== 1! to handle multiple selections
			toRightButton.setEnabled(true);
			allToRightButton.setEnabled(true);
		}
	}

	@UiHandler("allToLeftButton")
	public void allToLeftButtonClicked(ClickEvent event) {		
		moveAllItem(rightList,leftList , event);
		allToLeftButton.setEnabled(false);
		toLeftButton.setEnabled(false);
	
		if (leftList.getItemCount() >= 1) { // !>= 1! is preferred instead of !== 1! to handle multiple selections
			allToRightButton.setEnabled(true);
			toRightButton.setEnabled(true);
		}
	}

	private void moveItem(ListBox from, ListBox to, ClickEvent event) {
		String value=from.getValue();
		if (value == null) {
			Window.alert("Select an item first!");
			return;
		}
		SortedMap<String, String> fromTempList=new TreeMap<String,String>();
		SortedMap<String, String> toTempList=new TreeMap<String,String>();
		for(int itemIndex=0; itemIndex<from.getItemCount(); itemIndex++) {
			if (from.isItemSelected(itemIndex)){
				String item = from.getItemText(itemIndex);
				String valueItem = from.getValue(itemIndex);
				toTempList.put(item,valueItem);
			}
			else{
				String item = from.getItemText(itemIndex);
				String valueItem=from.getValue(itemIndex);
				fromTempList.put(item,valueItem);
			}
		}

		// order by type checklist from
		from.clear();
		@SuppressWarnings("unchecked")
		Map<String, String> mapFrom = sortByValues(fromTempList); 
		for (Entry<String, String> entry : mapFrom.entrySet())
			from.addItem(entry.getKey(),entry.getValue());
		
		//order by type checklist to
		for(int itemIndex=0; itemIndex<to.getItemCount(); itemIndex++) 
			toTempList.put(to.getItemText(itemIndex),to.getValue(itemIndex));		
		to.clear();
		@SuppressWarnings("unchecked")
		Map<String, String> mapTo = sortByValues(toTempList); 
		for (Entry<String, String> entry : mapTo.entrySet())
			to.addItem(entry.getKey(),entry.getValue());
		
	}






	private void moveAllItem(ListBox from, ListBox to, ClickEvent event) {
		SortedMap<String, String> toTempList=new TreeMap<String,String>();
		for(int itemIndex=0; itemIndex<from.getItemCount(); itemIndex++)
			toTempList.put(from.getItemText(itemIndex),from.getValue(itemIndex));		
			
		for(int itemIndex=0; itemIndex<to.getItemCount(); itemIndex++) 
			toTempList.put(to.getItemText(itemIndex),to.getValue(itemIndex));	
		
		to.clear();
		
		@SuppressWarnings("unchecked")
		Map<String, String> mapTo = sortByValues(toTempList); 
		for (Entry<String, String> entry : mapTo.entrySet())
			to.addItem(entry.getKey(),entry.getValue());
		from.clear();

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

	interface PickListUiBinder extends UiBinder<HorizontalPanel, PickList> {
	}

}