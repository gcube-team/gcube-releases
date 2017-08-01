package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class FilterValue implements Serializable, Comparable<FilterValue> {

	private static final long serialVersionUID = -346123619404369336L;
	private String value;
	
	
	public FilterValue(){
		super();
		value="";
	}
	
	public FilterValue(String value) {
		super();
		this.value = value;
	}

	public String getId(){
		return value;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


	@Override
	public int compareTo(FilterValue filterValue) {
		if(filterValue==null && value==null){
			return 0;
		} else {
			if(filterValue==null && value!=null){
				return 1;
			} else {
				return value.compareTo(filterValue.getValue());
			}
		}
	}

	@Override
	public String toString() {
		return "FilterValue [value=" + value + "]";
	}
	
}
