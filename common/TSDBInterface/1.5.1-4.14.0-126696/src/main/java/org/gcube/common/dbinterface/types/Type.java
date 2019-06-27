package org.gcube.common.dbinterface.types;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Date;
import java.text.Format;
import java.text.ParseException;
import java.util.List;


public class Type implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6608583434054878283L;

	public enum Types{
		INTEGER("setInt","getInt",int.class,0),
		STRING("setString","getString",String.class,""),
		TEXT("setString","getString",String.class,""),
		FLOAT("setFloat","getFloat",float.class,0),
		DATE("setDate","getDate",Date.class,0),
		TIME("setTime","getTime",Time.class,0),
		TIMESTAMP("setTimestamp","getTimestamp",Timestamp.class,0),
		//REAL("setFloat","getFloat",float.class),
		LONG("setLong","getLong",long.class,0),
		BOOLEAN("setBoolean","getBoolean", boolean.class,"t");
		
		private String value;
		private String methodSet;
		private String methodGet;
		private String relatedFunction;
		private List<String> listSqlTypes;
		private Object defaultValue;
		
		@SuppressWarnings(value = "rawtypes")
		private Class javaClass;
		
		
		@SuppressWarnings("rawtypes")
		Types(String methodSet, String methodGet, Class javaClass, Object defaultValue){
			this.methodSet= methodSet;
			this.methodGet= methodGet;
			this.javaClass= javaClass;
			this.relatedFunction=null;
			this.defaultValue = defaultValue;
		}
		
		
		
		/**
		 * @return the defaultValue
		 */
		public Object getDefaultValue() {
			return defaultValue;
		}



		public String getReflectionMethodSet(){
			return methodSet;
		}
		
		public String getReflectionMethodGet(){
			return methodGet;
		}
		
		public void setType(String dbType){
			this.value= dbType;
		}
		
		@SuppressWarnings("rawtypes")
		public Class getJavaClass(){
			return this.javaClass;
		}
		
		public String getValue(){
			return this.value;
		}
						
		public void setSpecificFunction(String functionName){
			this.relatedFunction=functionName;
		}
		
		public String getSpecificFunction(){
			return this.relatedFunction;
		}



		public List<String> getListSqlTypes() {
			return listSqlTypes;
		}



		public void setListSqlTypes(List<String> listSqlTypes) {
			this.listSqlTypes = listSqlTypes;
		}
	}
	
	private Types type; 
	private int[] precision;
	private Format format;
	private boolean autoincrement=false;
	private boolean primaryKey= false;
	
	public Type(Types type, int ... precision){
		this.setPrecision(precision);
		this.setType(type);
	}
	
	public int[] getPrecisionArray(){
		return this.precision;
	}
	
	protected String getPrecision(){
		if (precision==null || precision.length==0) return "";
		//if (precision.length==1) return "("+precision[0]+")";
		switch (this.type) {
		case STRING:case INTEGER:
			    return "("+precision[0]+")";
		case FLOAT: return "("+precision[0]+","+precision[1]+")";
		default:
			return "";
		}
	}
	
	public String getTypeDefinition(){
		return this.type.getValue()+this.getPrecision();
	}
	
	public Types getType() {
		return type;
	}
	
	
	public static Type parseType(String typeAsString, int charMaxLength, int numericPrecision, int numericScale ) throws TypeMappingException{
		String type= typeAsString.toLowerCase();
		if (Types.INTEGER.getListSqlTypes().contains(type)) return new Type(Types.INTEGER,numericPrecision,0);
		if (Types.BOOLEAN.getListSqlTypes().contains(type)) return new Type(Types.BOOLEAN,0,0);
		if (Types.DATE.getListSqlTypes().contains(type)) return new Type(Types.DATE,0,0);
		if (Types.TIME.getListSqlTypes().contains(type)) return new Type(Types.TIME,0,0);
		if (Types.TIMESTAMP.getListSqlTypes().contains(type)) return new Type(Types.TIMESTAMP,0,0);
		//if (Types.FLOAT.getListSqlTypes().contains(type)) return new Type(Types.FLOAT);
		if (Types.LONG.getListSqlTypes().contains(type)) return new Type(Types.LONG, numericPrecision,0);
		if (Types.FLOAT.getListSqlTypes().contains(type)) return new Type(Types.FLOAT, numericPrecision, numericScale);
		if (Types.STRING.getListSqlTypes().contains(type)) return new Type(Types.STRING, charMaxLength,0);
		if (Types.TEXT.getListSqlTypes().contains(type)) return new Type(Types.TEXT,0,0);
		throw new TypeMappingException(type);
	}
	
	@SuppressWarnings("rawtypes")
	public static Type getTypeByJavaClass(Class clazz) throws Exception{
		if (clazz==int.class || clazz==Integer.class) return new Type(Types.INTEGER);
		if (clazz==long.class || clazz==Long.class) return new Type(Types.LONG);
		if (clazz==float.class || clazz==Float.class) return new Type(Types.FLOAT);
		if (clazz==double.class || clazz==Double.class) return new Type(Types.FLOAT);
		if (clazz==Date.class) return new Type(Types.DATE);
		if (clazz==Time.class) return new Type(Types.TIME);
		if (clazz==Timestamp.class) return new Type(Types.TIMESTAMP);
		if (clazz==String.class || clazz==Enum.class) return new Type(Types.STRING);
		if (clazz==boolean.class || clazz==Boolean.class) return new Type(Types.BOOLEAN);
		if (clazz.isEnum()) return new Type(Types.STRING);
		return null;
	}
	
	public void setPrecision(int... precision) {
		this.precision = precision;
	}
	
	public String toString(Object o){
		return o.toString();
	}
	
	public int toInteger(Object o) throws NumberFormatException{
		return Integer.parseInt(o.toString());
	}
	
	public long toLong(Object o) throws NumberFormatException{
		return Long.parseLong(o.toString());
	}
	
	public float toReal(Object o)throws NumberFormatException{
		return Float.parseFloat(o.toString());
	}
	
	public double toFloat(Object o) throws NumberFormatException{
		return Double.parseDouble(o.toString());
	}
	
	public Date toDate(Object o, Format format) throws ParseException{
		return new Date(((Date)format.parseObject(o.toString())).getTime());
	}
	
	public Time toTime(Object o, Format format) throws ParseException{
		return new Time(((Time)format.parseObject(o.toString())).getTime());
	}
	
	public Timestamp toTimestamp(Object o) throws ParseException{
		return new Timestamp(Date.valueOf(o.toString()).getTime());
	}
	
	public Boolean toBoolean(Object o) throws ParseException{
		return Boolean.parseBoolean(o.toString());
	}

		

	public void setType(Types type) {
		this.type = type;
	}



	public Format getFormat() {
		return format;
	}



	public void setFormat(Format format) {
		this.format = format;
	}



	public boolean isAutoincrement() {
		return autoincrement;
	}

    

	public boolean isPrimaryKey() {
		return primaryKey;
	}



	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}



	public void setAutoincrement(boolean autoincrement) {
		this.autoincrement = autoincrement;
	}
	
}
