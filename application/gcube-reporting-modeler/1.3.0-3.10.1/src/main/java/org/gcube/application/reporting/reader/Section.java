package org.gcube.application.reporting.reader;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.reporting.component.interfaces.ReportComponent;

public class Section {
	List<ReportComponent> components;

	public Section() {
		super();
		this.components = new ArrayList<ReportComponent>();
	}

	public void add(ReportComponent c) {
		components.add(c);
	}
	public List<ReportComponent> getComponents() {
		return components;
	}

	public void setComponents(List<ReportComponent> components) {
		this.components = components;
	}	
}
