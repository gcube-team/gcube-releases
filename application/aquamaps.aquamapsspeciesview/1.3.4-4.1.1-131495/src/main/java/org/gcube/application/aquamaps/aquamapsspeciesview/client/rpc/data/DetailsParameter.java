package org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

public class DetailsParameter extends BaseModel implements IsSerializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7073479182629932572L;
	
	public static String PARAMETER_NAME="PARAMETER_NAME";
	public static String PARAMETER_VALUE="PARAMETER_VALUE";
	public static String PARAMETER_GROUP="PARAMETER_GROUP";
	
	public DetailsParameter() {
		// TODO Auto-generated constructor stub
	}
	
	public DetailsParameter(String name,String value,String group) {
		set(PARAMETER_NAME, name);
		set(PARAMETER_GROUP, group);
		set(PARAMETER_VALUE, value);
	}
	
	public String getGroup(){
		return get(PARAMETER_GROUP);
	}
	
	public String getName(){
		return get(PARAMETER_NAME);
	}
	
	public String getValue(){
		return get(PARAMETER_VALUE);
	}
}
