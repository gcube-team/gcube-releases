package org.gcube.application.reporting.component;

import java.util.List;

import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.application.reporting.component.type.ReportComponentType;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;

public class Box extends AbstractComponent {
	/**
	 * 	
	 */
	private String name;
	/**
	 * 
	 */
	private Boolean selected;
	
	public Box(String name, Boolean selected) {
		this.name = name;
		this.selected = selected;
	}

	public Box(String id, String name, Boolean selected) {
		super();
		setId(id);
		this.name = name;
		this.selected = selected;
	}
	
	public String getName() {
		return name;
	}

	public Boolean isSelected() {
		return selected;
	}

	@Override
	public ReportComponentType getType() {
		return ReportComponentType.ATTRIBUTE;
	}
	@Override
	public boolean hasChildren() {
		return false;
	}
	@Override
	public List<ReportComponent> getChildren() {
		return null;
	}
	@Override
	public String getStringValue() {
		return ""+selected;
	}

	@Override
	public BasicComponent getModelComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return "\n\tBox [id= " + getId() + " name=" + name + ", selected=" + selected + "]" + "\n";
	}	
}
