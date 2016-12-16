package org.gcube.dataanalysis.ecoengine.datatypes;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;

public class TablesList  extends StatisticalType {
	
	protected List<InputTable> list;
	protected List<TableTemplates> templateNames;
	
	public TablesList(List<TableTemplates> templateNames, String name, String description, boolean optional) {
		super(name, description, optional);
		list = new ArrayList<InputTable>();
		this.templateNames=templateNames;
	}
	
	public void add(InputTable st){
		list.add(st);
	}

	public List<InputTable> getList(){
		return list;
	}
	
	public List<TableTemplates> getTemplates(){
		return templateNames;
	}
	
}
