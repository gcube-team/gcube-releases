package org.gcube.application.reporting.component;

import java.util.List;

import org.gcube.application.reporting.component.interfaces.IsMedia;
import org.gcube.application.reporting.component.interfaces.IsSequentiable;
import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.application.reporting.component.type.ReportComponentType;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;

public class Image extends AbstractComponent implements IsSequentiable, IsMedia {
	String base64Encoding;
	
	public Image() {
		this.base64Encoding = null;		
	}

	public Image(String base64Encoding) {
		this.base64Encoding = base64Encoding;		
	}
	
	@Override
	public ReportComponentType getType() {
		return ReportComponentType.IMAGE;
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
		return base64Encoding;
	}

	@Override
	public BasicComponent getModelComponent() {
		BasicComponent bc = new BasicComponent(0, 0, COMP_WIDTH, COMP_HEIGHT, 
				1, ComponentType.IMAGE, "", base64Encoding, false, false, convertProperties());
		bc.setId(getId());
		return bc;
	}

}
