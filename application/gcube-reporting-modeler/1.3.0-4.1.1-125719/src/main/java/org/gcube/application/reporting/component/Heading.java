package org.gcube.application.reporting.component;

import java.util.List;

import org.gcube.application.reporting.component.interfaces.IsMedia;
import org.gcube.application.reporting.component.interfaces.IsSequentiable;
import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.application.reporting.component.type.ReportComponentType;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;

public class Heading extends AbstractComponent implements IsMedia, IsSequentiable {

	private int level;
	private String content;
	private boolean isReadOnly;
	private ComponentType modelType;
	
	public Heading(int level, String content) {
		super();
		this.level = level;
		this.content = content;
		this.isReadOnly = true;		
	}

	public Heading(int level, String content, boolean isReadonly) {
		super();
		this.level = level;
		this.content = content;
		this.isReadOnly = isReadonly;		
	}

	public int getLevel() {
		return level;
	}
	public boolean isReadOnly() {
		return isReadOnly;
	}
	
	public ComponentType getModelType() {
		return modelType;
	}

	@Override
	public ReportComponentType getType() {
		return ReportComponentType.HEADING;
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

		switch (level) {
		case 1:
			modelType = ComponentType.HEADING_1;
			break;
		case 2:
			modelType = ComponentType.HEADING_2;
			break;
		case 3:
			modelType = ComponentType.HEADING_3;
			break;
		case 4:
			modelType = ComponentType.HEADING_4;
			break;
		case 5:
			modelType = ComponentType.HEADING_5;
			break;

		default:
			throw new IllegalArgumentException("Only heading level between 1 and 5 are supported");
		}
		BasicComponent bc = new BasicComponent(0, 0, COMP_WIDTH, COMP_HEIGHT, 
				1, modelType, "", content, false, isReadOnly, convertProperties());
		bc.setId(getId());
		return bc;
	}

}
