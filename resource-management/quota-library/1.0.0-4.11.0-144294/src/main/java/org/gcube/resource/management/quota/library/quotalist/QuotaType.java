package org.gcube.resource.management.quota.library.quotalist;

import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedStorageStatusRecord;

public enum QuotaType {

	//STORAGE(0,AggregatedStorageUsageRecord.class,"dataVolume"),
	STORAGE(0,AggregatedStorageStatusRecord.class,"dataVolume"),
	SERVICE(1,AggregatedServiceUsageRecord.class,"operationCount");
	
	@SuppressWarnings("unused")
	private int value;
	private Class<? extends AggregatedUsageRecord<?,?>> typeclz;
	private String checkManager;
	private QuotaType(int value,Class<? extends AggregatedUsageRecord<?,?>> clz,String checkManager){
		this.value=value;
		this.typeclz=clz;
		this.checkManager=checkManager;
	}
	public Class<? extends AggregatedUsageRecord<?, ?>> getQuotaTypeClass(){
		return typeclz;
	}
	public String getCheckManager(){
		return checkManager;
	}
}
