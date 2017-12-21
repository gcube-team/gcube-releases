package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client;

import java.util.Map;

public interface ExternalTable {

	public String getId();
	public String getLabel();
	
	public Map<String,String> getColumnsNameAndLabels();
	
}
