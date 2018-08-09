package org.gcube.application.reporting.component;

import java.util.List;

import org.gcube.application.reporting.component.interfaces.IsSequentiable;
import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.application.reporting.component.type.ReportComponentType;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;

public class Instruction extends AbstractComponent implements IsSequentiable {
	private String content;
	
	public Instruction(String content) {
		super();
		this.content = content;

	}

	public String getContent() {
		return content;
	}

	@Override
	public BasicComponent getModelComponent() {
		BasicComponent bc = new BasicComponent(0, 0, COMP_WIDTH, COMP_HEIGHT, 
				1, ComponentType.INSTRUCTION, "", content, false, false, convertProperties());
		bc.setId(getId());
		return bc;
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
	public ReportComponentType getType() {
		return ReportComponentType.INSTRUCTION;
	}
}
