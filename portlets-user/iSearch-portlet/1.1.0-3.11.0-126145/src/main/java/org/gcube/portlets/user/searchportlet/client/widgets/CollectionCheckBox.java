package org.gcube.portlets.user.searchportlet.client.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.gcube.portlets.user.searchportlet.client.SearchPortlet;
import org.gcube.portlets.user.searchportlet.shared.CollectionBean;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;

/**
 * This class represents a custom checkbox that supports automatic selection/un-selection of parent and child elements
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class CollectionCheckBox extends CheckBox {

	private CollectionBean refCol;
	protected List<CollectionCheckBox> childCheckboxes;
	protected CollectionCheckBox parentCheckBox;	

	public CollectionCheckBox(CollectionBean col, CollectionCheckBox parentCheckBox) {
		super(col.getCollectionName());
		setTitle(col.getCollectionName());
		setName(col.getCollectionID());
		this.refCol = col;
		this.parentCheckBox = parentCheckBox;
		this.childCheckboxes = new ArrayList<CollectionCheckBox>();
		this.addClickHandler(new CollectionCheckBoxClickHandler());
	}
	
	private void changeState(Boolean value) {
		//SearchPortlet.logger.log(Level.SEVERE, "Got a click event with state: " + value + " for collection -> " + this.refCol.getCollectionID());
		this.refCol.setSelected(value);
		if (!value && parentCheckBox != null)
			parentCheckBox.setValue(value);
		
		for (CollectionCheckBox childCheckBox : this.childCheckboxes) {
			childCheckBox.setValue(value);
			childCheckBox.changeState(value);
		}
	}
	
	public void addChildCheckBox(CollectionCheckBox child) {
		//SearchPortlet.logger.log(Level.SEVERE, "Adding child checkbox with id -> " + child.getReferencedCollection().getCollectionID());
		this.childCheckboxes.add(child);
	}
	
	public CollectionBean getReferencedCollection() {
		return this.refCol;
	}
	
	/**
	 * Checkbox click handler
	 * 
	 * @author Panagiota Koltsida, NKUA
	 *
	 */
	private class CollectionCheckBoxClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			boolean isChecked = ((CheckBox) event.getSource()).getValue();
			changeState(isChecked);
		}
		
	}

}
