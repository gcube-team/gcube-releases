package org.gcube.application.aquamaps.aquamapsspeciesview.client.search;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types.ClientFieldType;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

public class ParameteredValidator implements Validator {

	private ClientFieldType type=ClientFieldType.STRING;
	
	
	@Override
	public String validate(Field<?> field, String value) {
		switch(type){
		case BOOLEAN : try{	Boolean.parseBoolean(value);return null;}
						catch(Exception e){return "This field requires a boolean value.";}
		case DOUBLE : try{	Double.parseDouble(value);return null;}
						catch(Exception e){return "This field requires a float value.";}
		case INTEGER : try{	Integer.parseInt(value);return null;}
						catch(Exception e){return "This field requires an integer value.";}
		default : return null;
		}
	}

	public ClientFieldType getType() {
		return type;
	}
	
	public void setType(ClientFieldType type) {
		this.type = type;
	}
	
}
