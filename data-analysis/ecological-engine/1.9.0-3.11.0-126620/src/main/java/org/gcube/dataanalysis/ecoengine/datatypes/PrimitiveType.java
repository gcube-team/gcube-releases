package org.gcube.dataanalysis.ecoengine.datatypes;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;

public class PrimitiveType extends StatisticalType {

	public PrimitiveType(String className, Object content, PrimitiveTypes type, String name, String description, String defaultValue, boolean optional) {
		super(name, description, defaultValue, optional);
		this.className = className;
		this.content = content;
		this.type = type;
	}

	public PrimitiveType(String className, Object content, PrimitiveTypes type, String name, String description, String defaultValue) {
		super(name, description, defaultValue);
		this.className = className;
		this.content = content;
		this.type = type;
	}

	public PrimitiveType(String className, Object content, PrimitiveTypes type, String name, String description) {
		super(name, description);
		this.className = className;
		this.content = content;
		this.type = type;
	}

	protected String className;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	protected Object content;

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	protected PrimitiveTypes type;

	public PrimitiveTypes getType() {
		return type;
	}

	public void setType(PrimitiveTypes type) {
		this.type = type;
	}

	public static LinkedHashMap<String, StatisticalType> stringMap2StatisticalMap(HashMap<String, String> stringmap) {
		LinkedHashMap<String, StatisticalType> map = new LinkedHashMap<String, StatisticalType>();
		if (stringmap != null) {
			for (String key : stringmap.keySet()) {
				String value = stringmap.get(key);
				PrimitiveType string = new PrimitiveType(String.class.getName(), value, PrimitiveTypes.STRING, key, key);
				map.put(key, string);
			}
		}
		return map;
	}
}
