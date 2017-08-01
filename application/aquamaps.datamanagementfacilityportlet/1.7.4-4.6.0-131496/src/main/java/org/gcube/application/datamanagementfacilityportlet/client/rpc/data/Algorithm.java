package org.gcube.application.datamanagementfacilityportlet.client.rpc.data;



import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.AlgorithmType;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

public class Algorithm extends BaseModel implements IsSerializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4892522046578840967L;
	public static final String TYPE="type";
	public static final String LABEL="label";
	
	
	
	
	
	public Algorithm(String type){
		set(TYPE,type);
		AlgorithmType algType=AlgorithmType.valueOf(type);
		switch(algType){
		case NativeRange : set(LABEL,"Native Range");
							break;
		case NativeRange2050 : set(LABEL,"Native Range 2050");
		break; 
		case SuitableRange : set(LABEL,"Suitable Range");
		break;
		case SuitableRange2050 : set(LABEL,"Suitable Range 2050");
		break;
		case HSPENRegeneration: set(LABEL,"HSPEN Regeneration");
		break;
		case LINEAR : set(LABEL,"Linear Interpolation");
		break;
		case PARABOLIC : set(LABEL,"Parabolic Interpolation");
		break;
		}
	}
	
	
	
	
	public Algorithm(String type, String label) {
		set(TYPE,type);
		set(LABEL,label);
	}

	public String getLabel() {
		return (String) get(LABEL);
	}
	public String getType() {
		return (String) get(TYPE);
	}
}
