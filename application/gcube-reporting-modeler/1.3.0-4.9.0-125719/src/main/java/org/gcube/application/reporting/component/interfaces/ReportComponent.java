package org.gcube.application.reporting.component.interfaces;
import java.util.List;

import org.gcube.application.reporting.Property;
import org.gcube.application.reporting.ReportsModeler;
import org.gcube.application.reporting.component.type.ReportComponentType;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;


public interface ReportComponent {
	final int COMP_WIDTH = ReportsModeler.TEMPLATE_WIDTH - 50;
	final int COMP_HEIGHT = 35;
	String getId();

	void setId(String id);

	List<Property> getProperties();

	void setProperties(List<Property> props);
	
	void setProperties(Property... properties);

	ReportComponentType getType();

	boolean hasChildren();

	List<ReportComponent> getChildren();

	String getStringValue();

	BasicComponent getModelComponent();

	
}
