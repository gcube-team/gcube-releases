package org.gcube.application.reporting.reference;

import java.util.List;

import org.gcube.application.reporting.component.AbstractComponent;
import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.application.reporting.component.type.ReportComponentType;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;

public class Column extends AbstractComponent {
	private String name;
	private String value;
	
	public Column() {
		super();
	}
	
	public Column(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public ReportComponentType getType() {
		return ReportComponentType.TB_COLUMN;
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
		return value;
	}
	@Override
	public String toString() {
		return "Column [name=" + name + ", value=" + value + "]";
	}
	
	@Override
	public BasicComponent getModelComponent() {
		return null;
	}	
}
