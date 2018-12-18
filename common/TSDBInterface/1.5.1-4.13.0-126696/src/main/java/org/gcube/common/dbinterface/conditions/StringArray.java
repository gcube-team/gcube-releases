package org.gcube.common.dbinterface.conditions;

import java.util.List;

import org.gcube.common.dbinterface.CastObject;
import org.gcube.common.dbinterface.pool.DBSession;


public class StringArray implements Listable {

	private List<String> listObject;
	
	public StringArray(List<String> objectList){
		this.listObject = objectList;
	}

	public String asStringList(){
		StringBuffer toReturn = new StringBuffer();
		CastObject cast = null;
		try{
			 cast = DBSession.getImplementation(CastObject.class);
		}catch(Exception e){}	
		for (String item: this.listObject)
			toReturn.append("'"+cast.escapeSingleQuote(item)+"',");
		return toReturn.substring(0, toReturn.length()-1);
	}
	
}
