package org.gcube.portlets.user.speciesdiscovery.client.filterresult;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * 
 */
public enum ResultFilterPanelEnum {
	CLASSIFICATION("By Classification"), 
	RANK("By Rank"), 
	TYPE("By Type"), 
	DATAPROVIDER("By Data Provider"), 
	DATASOURCE("By Data Source");

	private String label;

	ResultFilterPanelEnum() {
	}

	ResultFilterPanelEnum(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
