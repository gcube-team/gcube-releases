package org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientFieldType;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientFilterOperator;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.rpc.IsSerializable;

public class SpeciesFilter extends BaseModel implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5046940499474484329L;

	
	public static final String FIELD_NAME="FIELD_NAME";
	public static final String FIELD_VALUE="FIELD_VALUE";
	public static final String FIELD_TYPE="FIELD_TYPE";
	public static final String OPERATOR="FIELD_OPERATOR";
	public static final String FIELD_LABEL="FIELD_LABEL";
	
	
	public SpeciesFilter() {
		super();
	}
	
	public SpeciesFilter(String name,String label,Object value,ClientFilterOperator operator,ClientFieldType type){
		super();
		setName(name);
		setLabel(label);
		setValue(value);
		setOperator(operator);
		setType(type);
	}
	
	public void setName(String name){set(FIELD_NAME,name);}
	public void setLabel(String label){set(FIELD_LABEL,label);}
	public void setType(ClientFieldType type){set(FIELD_TYPE,type+"");}
	public void setOperator(ClientFilterOperator operator){set(OPERATOR,operator+"");}
	public void setValue(Object value){set(FIELD_VALUE,value);}
	
	
	
	public String getName(){return get(FIELD_NAME);}
	public String getLabel(){return get(FIELD_LABEL);}
	public ClientFieldType getType(){return ClientFieldType.valueOf((String)get(FIELD_TYPE));}
	public ClientFilterOperator getOperator(){return ClientFilterOperator.valueOf((String)get(OPERATOR));}
	public Object getValue(){return get(FIELD_VALUE);}
}
