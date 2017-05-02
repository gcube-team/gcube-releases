package org.gcube.application.reporting.component;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.reporting.Property;
import org.gcube.application.reporting.component.interfaces.IsMedia;
import org.gcube.application.reporting.component.interfaces.IsSequentiable;
import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.application.reporting.component.type.ReportComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Attribute;
import org.gcube.portlets.d4sreporting.common.shared.AttributeArea;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;

public class BoxesArea extends AbstractComponent implements IsSequentiable, IsMedia {
	private String name; 
	private List<Box> values;
	boolean multiSelectionEnabled;
	
	
	public BoxesArea(String name, List<Box> values, boolean allowMultiselection) {
		super();
		this.name = name;
		this.values = values;
		this.multiSelectionEnabled = allowMultiselection;
		//TODO: should be done by RSG
		setProperties(new Property("display", "block"));
	}

	
	public boolean isMultiSelectionEnabled() {
		return multiSelectionEnabled;
	}


	@Override
	public ReportComponentType getType() {
		return ReportComponentType.BOXAREA;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public List<ReportComponent> getChildren() {
		List<ReportComponent> toReturn = new ArrayList<ReportComponent>();
		toReturn.addAll(values);
		return toReturn;
	}

	@Override
	public String getStringValue() {
		return name;
	}

	@Override
	public BasicComponent getModelComponent() {
		ArrayList<Attribute> toCreate = new ArrayList<Attribute>();
		for (Box attr : values) {
			Attribute toAdd = new Attribute(attr.getName(), attr.isSelected());
			toAdd.setOptionalValue(attr.getId());
			toCreate.add(toAdd);
		}
		Attribute defaultAttr = new Attribute("Not applicable", false);
		toCreate.add(defaultAttr);
		
		ComponentType type = multiSelectionEnabled ? ComponentType.ATTRIBUTE_MULTI : ComponentType.ATTRIBUTE_UNIQUE;
		BasicComponent bc = new BasicComponent(0, 0, COMP_WIDTH, COMP_HEIGHT, 1, type, "", 
				new AttributeArea(name, toCreate), false,	false, convertProperties());
		bc.setId(getId());
		return bc;
	}

	@Override
	public String toString() {
		return "\nBoxesArea [name=" + name + ", multiSelectionEnabled=" + multiSelectionEnabled + "] props=" + getProperties().toString()+
				"\n" + ", children=" + values;
	}	
}
