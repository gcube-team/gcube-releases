package org.gcube.dataanalysis.ecoengine.datatypes;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;

public class PrimitiveTypesList  extends StatisticalType {
	
	protected List<PrimitiveType> list;
	PrimitiveTypes type;
	String className;
	
	public PrimitiveTypesList(String className, PrimitiveTypes type, String name, String description, boolean optional) {
		super(name, description, optional);
		list = new ArrayList<PrimitiveType>();
		this.type = type;
		this.className=className;
	}
	
	public void add(PrimitiveType st){
		list.add(st);
	}

	public List<PrimitiveType> getList(){
		return list;
	}
	
	public PrimitiveTypes getType(){
		return type;
	}
	
	public String getClassName(){
		return className;
	}
	
}
