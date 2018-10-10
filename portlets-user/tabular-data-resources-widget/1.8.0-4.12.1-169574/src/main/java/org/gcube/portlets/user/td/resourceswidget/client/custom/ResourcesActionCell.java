package org.gcube.portlets.user.td.resourceswidget.client.custom;

import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDDescriptor;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.sencha.gxt.cell.core.client.TextButtonCell;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class ResourcesActionCell implements HasCell<ResourceTDDescriptor, String> {

	private TextButtonCell buttonCell;
	private String name;

	
	public ResourcesActionCell(String name){
		buttonCell = new TextButtonCell();
		this.name=name;
	}
	
	@Override
	public Cell<String> getCell() {
		return buttonCell;
	}

	@Override
	public FieldUpdater<ResourceTDDescriptor, String> getFieldUpdater() {
		return new FieldUpdater<ResourceTDDescriptor, String>() {
			@Override
			public void update(int index, ResourceTDDescriptor object, String value) {
			}
		};
	}

	@Override
	public String getValue(ResourceTDDescriptor object) {
		return name;
	}

	
	
	

}
