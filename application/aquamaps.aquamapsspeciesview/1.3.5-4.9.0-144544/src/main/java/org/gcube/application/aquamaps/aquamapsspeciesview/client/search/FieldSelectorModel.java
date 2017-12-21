package org.gcube.application.aquamaps.aquamapsspeciesview.client.search;

import com.extjs.gxt.ui.client.data.BaseModel;

public class FieldSelectorModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1344698950088399011L;

	public static final String NAME="NAME";
	public static final String LABEL="LABEL";
	
	public FieldSelectorModel() {
		// TODO Auto-generated constructor stub
	}
	
	public FieldSelectorModel(String name,String label) {
		setName(name);
		setLabel(label);
	}
	
	public void setName(String name){set(NAME,name);}
	public void setLabel(String label){set(LABEL,label);}
	public String getName(){return get(NAME);}
	public String getLabel(){return get(LABEL);}
}
