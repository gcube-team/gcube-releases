package org.gcube.data_catalogue.grsf_publish_ws.utils.groups;


/**
 * The type of record, i.e. Aggregated or Original
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum Record_Type {

	AGGREGATED("Aggregated"),
	SOURCE("Source");

	private String name;

	private Record_Type(String name) {
		this.name = name;
	}
	
	public String getRecordType(){
		return name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
}
