package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Field")
@XmlRootElement(namespace=aquamapsTypesNS)
public class Field extends DataModel implements Comparable<Field>{

public static final String VOID="VOID";
	
	
	@XmlElement(namespace=aquamapsTypesNS)
	@XStreamAsAttribute
	private FieldType type=FieldType.STRING;
	
	@XmlElement(namespace=aquamapsTypesNS)
	@XStreamAsAttribute
	private String name;
	
	@XmlElement(namespace=aquamapsTypesNS)
	@XStreamAsAttribute
	private String value;

	public FieldType type() {
		return type;
	}

	public Field() {
		type=FieldType.STRING;
		name="DefaultFieldName";
		value="DefaultValue";
	}

	public Field(String name,String value) {
		this();
		this.name=name;
		this.value=value;
	}
	public Field(String name,String value,FieldType type){
		this(name,value);
		this.type(type);
	}


	public void type(FieldType type) {
		this.type = type;		
	}
	public String name() {
		return name;
	}
	public void name(String name) {
		this.name = name;
	}
	public String value() {
		return value;
	}
	public void value(String value) {
		this.value = value;
	}

	
	public String getOperator(){
		if(name.contains("min")) return ">=";
		else if(name.contains("max")) return "<=";
		else return "=";
	}



	
	public static List<Field> loadRow(ResultSet rs)throws Exception{
		ArrayList<Field> toReturn=new ArrayList<Field>();
		ResultSetMetaData rsMeta=rs.getMetaData();
		int colCount=rsMeta.getColumnCount();
		for(int i=1;i<=colCount;i++)
			toReturn.add(new Field(rsMeta.getColumnName(i),rs.getString(i),getType(rsMeta.getColumnType(i))));
		return toReturn;
	}
	
	public static FieldType getType(int SQLType){
		if(SQLType==Types.TIME||SQLType==Types.TIMESTAMP)return FieldType.TIMESTAMP;
		if(SQLType==Types.BIGINT||SQLType==Types.TINYINT||SQLType==Types.SMALLINT||
				SQLType==Types.INTEGER||SQLType==Types.BIT) return FieldType.INTEGER;
		if(SQLType==Types.FLOAT||SQLType==Types.DOUBLE||SQLType==Types.REAL||SQLType==Types.DECIMAL||SQLType==Types.NUMERIC) return FieldType.DOUBLE;
		if(SQLType==Types.BOOLEAN) return FieldType.BOOLEAN;
		return FieldType.STRING;
	}
	
	
	public static List<List<Field>> loadResultSet(ResultSet rs)throws SQLException{
		List<List<Field>> toReturn=new ArrayList<List<Field>>();
		ResultSetMetaData rsMeta=rs.getMetaData();
		int colCount=rsMeta.getColumnCount();
		while(rs.next()){
			List<Field> row=new ArrayList<Field>();
			for(int i=1;i<=colCount;i++)
				row.add(new Field(rsMeta.getColumnName(i),rs.getString(i),getType(rsMeta.getColumnType(i))));
			toReturn.add(row);
		}
		return toReturn;
	}
	
	
	//**************** VALUE PARSING
	
	public Double getValueAsDouble(){
		if(isNull()) return null;
			return Double.parseDouble(value());			
	}
	
	public Integer getValueAsInteger(){
		if(isNull()) return null;
			return Integer.parseInt(value());
	}
	public Long getValueAsLong(){
		if(isNull()) return null;
			return Long.parseLong(value());
	}
	public Boolean getValueAsBoolean(){
		if(isNull()) return null;
		try{
			Integer i=getValueAsInteger();			
			return Boolean.valueOf(i.equals(Integer.valueOf(1)));
		}catch(Exception e){
			return Boolean.parseBoolean(value());						
		}
	}

	
	//************** 
	
	
	public boolean isNull(){
		return value()==null||value().equals(VOID)||value().equals("")||value().equals(" ")||value().equalsIgnoreCase("null");
	}
	
	public boolean validate(){
		if(isNull()) return true;
		try{
		switch(type()){
			case BOOLEAN : getValueAsBoolean();
			break;
			case DOUBLE : getValueAsDouble();
			break;
			case INTEGER : getValueAsInteger();
			break;
			case LONG : getValueAsLong();
			break;
		}
		return true;
		}catch(Throwable t){
			return false;
		}
	}
	
	//************** USE DEFAULT
	public Double getValueAsDouble(String defaultValue){
		try{
			return Double.parseDouble(value());			
		}catch(Exception e){return Double.parseDouble(defaultValue);}
	}
	
	public Integer getValueAsInteger(String defaultValue){
		try{
			return Integer.parseInt(value());
		}catch(Exception e) {return Integer.parseInt(defaultValue);}
	}
	public Long getValueAsLong(String defaultValue){
		try{
			return Long.parseLong(value());
		}catch(Exception e) {return Long.parseLong(defaultValue);}
	}
	public Boolean getValueAsBoolean(String defaultValue){
		try{
		 return Boolean.parseBoolean(value());
		}catch(Exception e){
			Integer i=getValueAsInteger();
			if(i!=null)
			return  Boolean.valueOf(i==1);
			else return Boolean.parseBoolean(defaultValue);
		}
	}
	
	public JSONObject toJSONObject() throws JSONException{
		JSONObject toReturn=new JSONObject();
		toReturn.put("name", name);
		toReturn.put("value", value);
		toReturn.put("type", type);
		return toReturn;
	}
	
	public Field(JSONObject obj)throws JSONException{
		this.name(obj.getString("name"));
		this.type(FieldType.valueOf(obj.getString("type")));
		this.value(obj.getString("value"));
	}
	
	public static ArrayList<Field> fromJSONArray(JSONArray array)throws JSONException{
		ArrayList<Field> toReturn=new ArrayList<Field>();
		for(int i=0;i<array.length();i++)
			toReturn.add(new Field(array.getJSONObject(i)));
		return toReturn;
	}
	public static JSONArray toJSONArray(List<Field> list) throws JSONException{
		JSONArray array=new JSONArray();
		for(Field f:list)array.put(f.toJSONObject());
		return array;
	}

	@Override
	public int compareTo(Field arg0) {
		return this.name().compareTo(arg0.name());
	}

	@Override
	public String toString() {
		return "Field [type=" + type + ", name=" + name + ", value=" + value
				+ "]";
	}
	
}
