package org.gcube.dataanalysis.ecoengine.datatypes;

import java.util.ArrayList;
import java.util.List;

public class StatisticalTypeList  <C extends StatisticalType> extends StatisticalType {
	
	private List<C> list;
	
	
	public StatisticalTypeList(String name, String description, boolean optional) {
		super(name, description, optional);
		list = new ArrayList<C>();
	}
	
	public void add(C st){
		list.add(st);
	}

	public List<C> getList(){
		return list;
	}
	
}
