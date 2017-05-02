package org.gcube.accounting.aggregator.plugin;

/**
 * @author Alessandro Pieve (ISTI - CNR)
 *
 */
public enum DesignID {

	accounting_storage("accounting_storage","StorageUsageRecordAggregated","all","scope"),
	accounting_service("accounting_service","ServiceUsageRecordAggregated","all","scope"),
	accounting_portlet("accounting_portlet","PortletUsageRecordAggregated","all","scope"),
	accounting_job("accounting_job","JobUsageRecordAggregated","all","scope"),
	accounting_task("accounting_task","TaskUsageRecordAggregated","all","scope");
	
	
	
	private String nameBucket;	
	private String nameDesign;
	private String nameView;
	private String nameViewScope;
	
	private DesignID(String nameBucket, String nameDesign, String nameView,String nameViewScope) {
		this.nameBucket = nameBucket;
		this.nameDesign = nameDesign;
		this.nameView=nameView;
		this.nameViewScope=nameViewScope;
	}
	
	public String getNameBucket() {
		return nameBucket;
	}

	public String getNameDesign() {
		return nameDesign;
	}

	public String getNameView() {
		return nameView;
	}
	public String getNameViewScope(){
		return nameViewScope;
	}

}
