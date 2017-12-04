package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client;

import java.util.List;

import org.gcube.portlets.user.tdw.client.TabularData;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.OperatorsClassification;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.resources.Resources;

import com.google.gwt.core.client.GWT;

public class Services {

	static {
		getResources().css().ensureInjected();
	}
	
	private static List<OperatorsClassification> operatorsClassifications = null;
	private static final StatisticalManagerWidgetServiceAsync statisticalService = GWT
			.create(StatisticalManagerWidgetService.class);
	
	
	
	private static TabularData tabularData=null;
	
	public static Resources getResources() {
		return Resources.INSTANCE;
	}
	
	public static List<OperatorsClassification> getOperatorsClassifications() {
		return operatorsClassifications;
	}
	
	public static StatisticalManagerWidgetServiceAsync getStatisticalService() {
		return statisticalService;
	}
	
	public static void setOperatorsClassifications(
			List<OperatorsClassification> operatorsClassifications) {
		Services.operatorsClassifications = operatorsClassifications;
	}
	
	public static OperatorsClassification getOperatorsClassificationByName(
			String classificationName) {
		if (operatorsClassifications == null)
			return null;
		OperatorsClassification find = null;
		for (OperatorsClassification oc : operatorsClassifications)
			if (oc.getName().equals(classificationName))
				find = oc;
		return (find == null ? getDefaultOperatorsClassification() : find);
	}
	
	
	public static OperatorsClassification getDefaultOperatorsClassification() {
		if (operatorsClassifications == null)
			return null;
		OperatorsClassification find = null;
		for (OperatorsClassification oc : operatorsClassifications)
			if (oc.getName().equals(Constants.computationClassificationName))
				find = oc;
		return find;
	}
	
	public static synchronized TabularData getTabularData(){
		if(tabularData==null)tabularData = new TabularData(
				Constants.TD_DATASOURCE_FACTORY_ID);
		return tabularData;
	}
}
