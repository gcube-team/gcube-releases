package org.gcube.portlets.user.td.widgetcommonevent.shared;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class RequestProperties implements Serializable {

	private static final long serialVersionUID = -3084969072634114974L;

	HashMap<RequestPropertiesParameterType, Object> map;
	
	public RequestProperties(){
		
	}
	
	public RequestProperties(HashMap<RequestPropertiesParameterType,Object> map){
		this.map=map;
	}
	
	
	public HashMap<RequestPropertiesParameterType, Object> getMap() {
		return map;
	}

	public void setMap(HashMap<RequestPropertiesParameterType, Object> map) {
		this.map = map;
	}

	@Override
	public String toString() {
		return "RequestProperties [map=" + map + "]";
	}

}
