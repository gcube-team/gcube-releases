package org.gcube.portlets.user.speciesdiscovery.client.detail;

import org.gcube.portlets.user.speciesdiscovery.client.util.SpeciesDetailsFields;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

public class SpeciesDetail extends BaseModel implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7073479182629932572L;
	
	
	public SpeciesDetail() {
	}
	
	public SpeciesDetail(String name, String value, String group) {
		set(SpeciesDetailsFields.NAME.getId(), name);
		set(SpeciesDetailsFields.GROUP.getId(), group);
		set(SpeciesDetailsFields.VALUE.getId(), value);
	}
	
	public String getGroup(){
		return get(SpeciesDetailsFields.GROUP.getId());
	}
	
	public String getName(){
		return get(SpeciesDetailsFields.NAME.getId());
	}
	
	public String getValue(){
		return get(SpeciesDetailsFields.VALUE.getId());
	}
}
