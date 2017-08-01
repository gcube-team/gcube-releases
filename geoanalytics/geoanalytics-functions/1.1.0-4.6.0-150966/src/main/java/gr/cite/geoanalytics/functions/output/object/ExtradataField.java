package gr.cite.geoanalytics.functions.output.object;

public class ExtradataField {
	
	private String fieldName;
	private Object value;
	
	public ExtradataField(String fieldName, Object value){
		this.fieldName = fieldName;
		this.value = value;
	}
	
	@Override
	public String toString(){
		if(value instanceof Double)
			return "<"+fieldName+" type="+"\"double\">"+value+"</"+fieldName+">";
		else if(value instanceof Long)
			return "<"+fieldName+" type="+"\"long\">"+value+"</"+fieldName+">";
		else if(value instanceof Integer)
			return "<"+fieldName+" type="+"\"int\">"+value+"</"+fieldName+">";
		else if(value instanceof Boolean)
			return "<"+fieldName+" type="+"\"boolean\">"+value+"</"+fieldName+">";
		else
			return "<"+fieldName+" type="+"\"string\">"+value+"</"+fieldName+">";
	}
	
	
}
