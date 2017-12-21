package org.gcube.application.reporting.component;

import java.util.List;

import org.gcube.application.reporting.component.interfaces.IsMedia;
import org.gcube.application.reporting.component.interfaces.IsSequentiable;
import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.application.reporting.component.type.ReportComponentType;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;

public class HiddenField extends AbstractComponent implements IsSequentiable {

	private String content;
		
	public HiddenField() {
		super();
		this.content = "";	
	}
	
	public HiddenField(String content) {
		super();
		this.content = content;
	}
	
	@Override
	public ReportComponentType getType() {
		return ReportComponentType.HIDDEN;
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
		return content;
	}

	@Override
	public BasicComponent getModelComponent() {
		BasicComponent bc = new BasicComponent(0, 0, COMP_WIDTH, COMP_HEIGHT, 
				1, ComponentType.HIDDEN_FIELD, "", content, false, true, convertProperties());	
		bc.setId(getId());
		return bc;
	}

}
