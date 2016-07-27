package org.gcube.portlets.admin.sepeditor.shared;

import com.extjs.gxt.ui.client.data.BaseModel;

public class Category extends BaseModel {  
	private static final long serialVersionUID = 1L;  

	public Category() {  
	}  
	public Category(String name) {  
		set("name", name);  
	}  
	public String getName() {  
		return (String) get("name");  
	}  
	public String toString() {  
		return getName();  
	}  
}  