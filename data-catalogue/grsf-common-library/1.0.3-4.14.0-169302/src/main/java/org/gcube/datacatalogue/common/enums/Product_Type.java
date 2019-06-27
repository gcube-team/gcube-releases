package org.gcube.datacatalogue.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The type of product, i.e. Stock or Fishery
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum Product_Type {
	
	FISHERY("Fishery"),
	STOCK("Stock");
	
	private String subGroupNameOrig;

	private Product_Type(String origName) {
		this.subGroupNameOrig = origName;
	}

	/**
	 * Return the original name
	 * @return
	 */
	public String getOrigName(){
		return subGroupNameOrig;
	}

	@JsonValue
	public String onSerialize(){
		return subGroupNameOrig.toLowerCase();
	}

	@JsonCreator
	public static Product_Type onDeserialize(String recordTypeString) {
		if(recordTypeString != null) {
			for(Product_Type source : Product_Type.values()) {
				if (source.toString().equalsIgnoreCase(recordTypeString.trim()))
					return source;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return getOrigName();
	}
}
