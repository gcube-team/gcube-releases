package org.gcube.accounting.aggregator.aggregation;

/**
 * @author Alessandro Pieve (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public enum DesignID {

	accounting_storage("StorageUsageRecordAggregated","all"),
	StorageUsageRecord("StorageUsageRecordAggregated","all"),
	accounting_service("ServiceUsageRecordAggregated","all"),
	ServiceUsageRecord("ServiceUsageRecordAggregated","all"),
	accounting_portlet("PortletUsageRecordAggregated","all"),
	accounting_job("JobUsageRecordAggregated","all"),
	accounting_task("TaskUsageRecordAggregated","all");
	
	private String designName;
	private String viewName;
	
	private DesignID(String designName, String viewName) {
		this.designName = designName;
		this.viewName = viewName;
	}

	public String getDesignName() {
		return designName;
	}

	public String getViewName() {
		return viewName;
	}
	
}
