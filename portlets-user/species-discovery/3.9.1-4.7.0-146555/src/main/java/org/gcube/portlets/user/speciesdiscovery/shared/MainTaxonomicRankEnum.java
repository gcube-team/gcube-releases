/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public enum MainTaxonomicRankEnum {
	
	KINGDOM("kingdom"), 
	PHYLUM("phylum"), 
	CLASS("class"), 
	ORDER("order"), 
	FAMILY("family"), 
	GENUS("genus"), 
	SPECIES("species");
	
	protected String label;
	
	MainTaxonomicRankEnum(String label)
	{
		this.label = label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	public static List<String> getListLabels(){
		
		List<String> listLabels = new ArrayList<String>();
		
		for (MainTaxonomicRankEnum item : MainTaxonomicRankEnum.values())
			listLabels.add(item.getLabel());

		return listLabels;
	}
	
	
	public static MainTaxonomicRankEnum valueOfLabel(String label)
	{
		for (MainTaxonomicRankEnum value:values()) if (value.getLabel().equals(label)) return value;
		return null;
	}
}
