package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.FilterArray;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FilterType;

public class Filter extends DataModel{


	private FilterType type=FilterType.is;
	private Field field;

	public FilterType getType() {
		return type;
	}

	public void setType(FilterType type) {
		this.type = type;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Field getField() {
		return field;
	}

	public Filter(FilterType type,Field f){
		this.field=f;
		this.type=type;
	}
	
	
	public Filter (org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Filter toLoad){
		Field field= new Field();
		field.name(toLoad.name());
		field.value(toLoad.value());
		field.type(FieldType.valueOf(toLoad.fieldType()));
		this.setField(field);
		this.setType(FilterType.valueOf(toLoad.type()));
	}

	public static List<Filter> load(FilterArray toLoad){
		List<Filter> toReturn=new ArrayList<Filter>();
		if((toLoad!=null)&&(toLoad.theList()!=null))
			for(org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Filter f: toLoad.theList())toReturn.add(new Filter(f));
		return toReturn;
	}

	public static FilterArray toStubsVersion(List<Filter> toConvert){
		List<org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Filter> list=new ArrayList<org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Filter>();
		if(toConvert!=null)
			for(Filter obj:toConvert)
				list.add(obj.toStubsVersion());
		return new FilterArray(list);
	}

	public org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Filter toStubsVersion(){
		org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Filter toReturn= new org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Filter();
		toReturn.name(this.getField().name());
		toReturn.type(this.getType().toString());
		toReturn.value(this.getField().value());
		toReturn.fieldType(this.getField().type()+"");
		return toReturn;
	}

	public String toSQLString() throws IllegalArgumentException{
		switch(type){
		case begins: return " ilike '"+field.value()+"%'";
		case contains: return " ilike '%"+field.value()+"%'";
		case ends: return " ilike '%"+field.value()+"'";
		case is: {
					switch(field.type()){
					case STRING : return " = '"+field.value()+"'";
					case INTEGER : return " = "+field.getValueAsInteger();
					case DOUBLE : return " = "+field.getValueAsDouble();
					case LONG : return " = "+field.getValueAsLong();
					default : return " = "+(field.getValueAsBoolean()?"1":"0");
					}
				}
		case greater_then : return " >= "+field.value();
		case smaller_then : return " <= "+field.value();
		default : throw new IllegalArgumentException("invalid filter type");
		}
	}

}
