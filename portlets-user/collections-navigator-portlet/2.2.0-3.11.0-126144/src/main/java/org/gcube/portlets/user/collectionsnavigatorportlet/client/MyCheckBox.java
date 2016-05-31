package org.gcube.portlets.user.collectionsnavigatorportlet.client;

import java.util.ArrayList;
import java.util.List;

import org.jsonmaker.gwt.client.Jsonizer;

import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapter;
import net.eliasbalasis.tibcopagebus4gwt.client.PageBusAdapterException;
import net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.Person;
import net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.PersonJsonizer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FormPanel;

public class MyCheckBox extends CheckBox{
	
	protected String altText;
	protected String recNo;
	protected String creationDate;
	protected String schema;
	protected FormPanel form;
	protected List<MyCheckBox> childCheckboxes;
	protected MyCheckBox parentCheckBox;
	protected MyCompositeCheckBox containerCheckBox;
	
	// For the notification mechanism
	final PageBusAdapter pageBusAdapter = new PageBusAdapter();

	protected void init()
	{
		childCheckboxes = new ArrayList<MyCheckBox>();
	}

	public MyCheckBox(Element elem, MyCheckBox parentCheckBox) {
		super(elem);
		this.parentCheckBox = parentCheckBox;
		init();
	}
	
	public MyCheckBox(String label, boolean asHTML, MyCheckBox parentCheckBox) {
		super(label, asHTML);
		super.setTitle(label);
		this.parentCheckBox = parentCheckBox;
		init();
	}

	public MyCheckBox(String label, MyCheckBox parentCheckBox) {
		super(label);
		super.setTitle(label);
		this.parentCheckBox = parentCheckBox;
		init();
	}

	public MyCheckBox(MyCheckBox parentCheckBox) {
		super();
		this.parentCheckBox = parentCheckBox;
		init();
	}
	
	public void setContainerCheckBox(MyCompositeCheckBox container) {
		this.containerCheckBox = container;
	}
	
	/**
	 * Opens or closes the tree item corresponding to this checkbox,
	 * so that its children are displayed or hidden.
	 * @param open the desired state (open or closed)
	 */
	public void setItemState(boolean open) {
		if (this.parentCheckBox != null)
			this.parentCheckBox.setItemState(open);
		containerCheckBox.setItemState(open);
	}
	
	public String getAltText() {
		return altText;
	}

	public String getRecNo() {
		return recNo;
	}
	
	public String getCreationDate() {
		return creationDate;
	}
	
	public void setAltText(String altText) {
		this.altText = altText;
	}
	
	public void setRecNo(String recNo) {
		this.recNo = recNo;
	}
	
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	
	public void addCheckbox(MyCheckBox checkbox) {
		this.childCheckboxes.add(checkbox);
	}
	
	public void doClick()
	{

		ArrayList<String> collectionsSelected = new ArrayList<String>();
		ArrayList<String> collectionsUnselected = new ArrayList<String>();
		updateCheckBoxes(this.getValue(), collectionsSelected, collectionsUnselected);

			AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>()
			{
				public void onFailure(Throwable caught)
				{
					CollectionsNavigatorPortletG.hideLoading();
					Window.alert("Failed to retrieve the information for the current selected collections. Please try again.");
				}

				public void onSuccess(Boolean result)
				{
					CollectionsNavigatorPortletG.hideLoading();
					//Notify the geospatial portlet if it should be enabled or not
					Person geospatialNotification = new Person();
					if (result.booleanValue() == true) {
						geospatialNotification.setName("Enable Geospatial");
					}
					else {
						geospatialNotification.setName("Disable Geospatial");
					}					
					   // publish a message with the geospatial notification
					   try {
						   pageBusAdapter.PageBusPublish("net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.Person", geospatialNotification, (Jsonizer)GWT.create(PersonJsonizer.class));
					} catch (PageBusAdapterException e) {
						e.printStackTrace();
					}
					
					
					//Notify the search portlet that the selected collections have been changed
					Person searchNotification = new Person();
					searchNotification.setName(CollectionsConstants.collections_changed);

					// publish a message with the notification for the search portlet
					try {
						pageBusAdapter.PageBusPublish("net.eliasbalasis.tibcopagebus4gwt.testsubscriber.client.Person", searchNotification, (Jsonizer)GWT.create(PersonJsonizer.class));
					} catch (PageBusAdapterException e) {
						e.printStackTrace();
					}
				}
			};
			/* Update the clicked collection and its children */
			CollectionsNavigatorPortletG.collectionsService.changeCollectionStatus(collectionsSelected, this.getValue(), callback);
			CollectionsNavigatorPortletG.showLoading();
			
	}

	public FormPanel getForm() {
		return form;
	}

	public void setForm(FormPanel form) {
		this.form = form;
	}	

	private void updateCheckBoxes(boolean check, ArrayList<String> colSelected, ArrayList<String> colUnSelected) {
		if (check == false)
			uncheckParent(colUnSelected);

		checkBoxSelection(check, colSelected);
	}
	
	private void uncheckParent(ArrayList<String> colUnSelected){
		if (parentCheckBox != null) {
			parentCheckBox.uncheckParent(colUnSelected);
			parentCheckBox.setValue(false);
			colUnSelected.add(parentCheckBox.getName().substring(16));
		}
	}

	private void checkBoxSelection(boolean check, ArrayList<String> colSelected){
		int childNumber = childCheckboxes.size();
		
		/* Add the collection name (strip off the prefix) */
		colSelected.add(getCollectionName());

		for(int i=0; i<childNumber; i++){
			((MyCheckBox)childCheckboxes.get(i)).setValue(check);
			((MyCheckBox)childCheckboxes.get(i)).checkBoxSelection(check, colSelected);
		}		
	}
	
	private String getCollectionName() {
		return this.getName().substring(16);
	}
	
	/**
	 * Select or unselect a given collection in the tree.
	 * @param colID the ID of the collection to select/unselect
	 * @param select true to select the collection, or false to unselect
	 */
	public void selectCollection(String colID, boolean select) {
		if (this.childCheckboxes.size() > 0) {
			for (int i=0; i<this.childCheckboxes.size(); i++) {
				MyCheckBox c = (MyCheckBox) this.childCheckboxes.get(i);
				c.selectCollection(colID, select);
			}
		}

		/* If this checkbox is the one that it's state should be changed, change it
		 * and invoke the doClick() method.
		 */
		if (this.getCollectionName().equals(colID) && (this.getValue() != select)) {
			this.setValue(!this.getValue());
			this.doClick();
			
			/* if the checkbox is checked invoke the setItemState to apply changes on the tree */
			// Opens the tree if not depending on the state of the checkbox.
			if (this.getValue())
				this.setItemState(true);
		}

	}

}
