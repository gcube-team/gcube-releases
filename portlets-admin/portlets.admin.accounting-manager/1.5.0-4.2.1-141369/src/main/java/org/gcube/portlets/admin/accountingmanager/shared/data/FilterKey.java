package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class FilterKey implements Serializable, Comparable<FilterKey> {

	private static final long serialVersionUID = 7200526591393559078L;
	private String key;
	
	
	public FilterKey(){
		super();
	}
	
	public FilterKey(String key) {
		super();
		this.key = key;
	}

	public String getId(){
		return key;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}


	@Override
	public int compareTo(FilterKey filterKey) {
		if(filterKey==null && key==null){
			return 0;
		} else {
			if(filterKey==null && key!=null){
				return 1;
			} else {
				return key.compareTo(filterKey.getKey());
			}
		}
	}

	
	@Override
	public String toString() {
		return "FilterKey [key=" + key + "]";
	
	}
	
	
	
}
