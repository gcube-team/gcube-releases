package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HCAF_SFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;

public class Cell extends DataModel{
	
	
 	private String cSquareCode;
 	private List<Field> attributesList=new ArrayList<Field>();
 	
 	public Cell(String code){this.cSquareCode=code;}
 	public void setCode(String code){this.cSquareCode=code;}
 	public String getCode(){return cSquareCode;}
 	
 	
	public List<Field> getAttributesList() {
		if(attributesList==null) setAttributesList(new ArrayList<Field>());
		return attributesList;
	}
	public void setAttributesList(List<Field> attributesList) {
		this.attributesList = attributesList;
	}
	public Field getFieldbyName(String fieldName){
		for(Field field:getAttributesList()){
			if(field.name().equals(fieldName)) return field;
		}
		return new Field(fieldName,Field.VOID);	
	}
	
	public void addField(Field toAddField){
		getAttributesList().add(toAddField);
	}
	
	public Cell(List<Field> initFields){
		this("DUMMYCODE");
		getAttributesList().addAll(initFields);
		this.setCode(this.getFieldbyName(HCAF_SFields.csquarecode+"").value());
	}
	
	
	public static Set<Cell> loadRS(ResultSet rs) throws SQLException{
		HashSet<Cell> toReturn=new HashSet<Cell>();
		List<List<Field>> rows=Field.loadResultSet(rs);
		for(List<Field> row:rows){
			toReturn.add(new Cell(row));
		}
		return toReturn;
	}
	
	
	
//	public String toXML(){
//		StringBuilder toReturn=new StringBuilder();
//		toReturn.append("<Cell>");
//		toReturn.append("<"+HCAF_SFields.csquarecode+">"+cSquareCode+"</"+HCAF_SFields.csquarecode+">");
//		toReturn.append("<Attributes>");
//		for(Field field:attributesList) toReturn.append(field.toXML());
//		toReturn.append("</Attributes>");
//		toReturn.append("</Cell>");
//		return toReturn.toString();
//	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cSquareCode == null) ? 0 : cSquareCode.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Cell))
			return false;
		Cell other = (Cell) obj;
		if (cSquareCode == null) {
			if (other.cSquareCode != null)
				return false;
		} else if (!cSquareCode.equals(other.cSquareCode))
			return false;
		return true;
	}
	
	
}
