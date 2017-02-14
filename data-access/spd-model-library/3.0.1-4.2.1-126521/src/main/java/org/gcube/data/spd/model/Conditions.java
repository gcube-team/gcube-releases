package org.gcube.data.spd.model;

import java.util.Calendar;

@SuppressWarnings("rawtypes")
public enum Conditions {

	DATE(Calendar.class),
	COORDINATE(Coordinate.class);
	
	
	private Class type;
	
	Conditions(Class type){
		this.type = type;
	}
	
	public Class getType(){return this.type;}
	
}
