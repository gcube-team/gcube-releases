package org.gcube.application.perform.service.engine.model.importer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gcube.application.perform.service.engine.model.DBField;

public class CSVRecordConverter {
	
	private Map<String,DBField> labels;
	
	
	
	public CSVRecordConverter(Map<String, DBField> labels) {
		super();
		this.labels = labels;
	}

	private Map<String,Map<String,String>> mappings=new HashMap<>(); 
	private String conditionField=null;
	private Set<String> conditionValues=new HashSet<>();
	private boolean isAlwaysMap=false;
	
	
	private ResultSetMetaData rsMeta=null;
	
	
	public Object[] convert(ResultSet rs)throws SQLException{
		if(rsMeta==null)rsMeta=rs.getMetaData();
		Object[] toReturn=new Object[rsMeta.getColumnCount()];
		if(mappingCondition(rs)) return map(rs,toReturn);
		else 		
		for(int i=0;i<toReturn.length;i++)
			toReturn[i]=rs.getObject(i+1);
		return toReturn;
		
		
	}
	
	public void setMapping(String field, Map<String,String> mapping) {
		String actualFieldName=labels.get(field).getFieldName();
		actualFieldName=actualFieldName.substring(1, actualFieldName.length()-1);
		mappings.put(actualFieldName, mapping);
	}
	
	public void setCondition(String field, Set<String> values) {
		this.conditionField=labels.get(field).getFieldName();
		conditionField=conditionField.substring(1, conditionField.length()-1);
		this.conditionValues=values;
	}
	
	public void setAlwaysMap(boolean isAlwaysMap) {
		this.isAlwaysMap = isAlwaysMap;
	}
	
	public void reset() {
		rsMeta=null;
	}
	
	private boolean mappingCondition(ResultSet rs) throws SQLException{
		// Optimized pass-all
		if(isAlwaysMap) return true;
		
		// DefaultBehaviour
		if(conditionField==null) return false;
		else {
			String currentValue=rs.getString(conditionField);
			return conditionValues.contains(currentValue);
		}
	}
	
	private Object[] map(ResultSet rs,Object[] toReturn) throws SQLException{
		
		for(int i=0;i<toReturn.length;i++) {
			toReturn[i]=rs.getObject(i+1);
			
			String field=rsMeta.getColumnName(i+1);
			if(mappings.containsKey(field)) {
				Map<String,String> fieldMapping=mappings.get(field);
				String value=rs.getString(i+1);
				if(value!=null&&fieldMapping.containsKey(value))
					toReturn[i]=fieldMapping.get(value);
			}
		}
		
		return toReturn;
	}
	
}
