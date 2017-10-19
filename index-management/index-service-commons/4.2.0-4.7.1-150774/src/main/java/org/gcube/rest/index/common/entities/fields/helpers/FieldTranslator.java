package org.gcube.rest.index.common.entities.fields.helpers;

import org.gcube.rest.index.common.entities.fields.config.FieldType;

public class FieldTranslator {

	public static FieldType fromLuceneDataType(String luceneType){
		FieldType type;
		switch(luceneType){
	        case "long":
	        	type = FieldType.LONG;
	            break;
	        case "integer":
	        	type = FieldType.INTEGER;
	            break;
	        case "short":
	        	type = FieldType.SHORT;
	            break;
	        case "byte":
	        	type = FieldType.BYTE;
	            break;
	        case "double":
	        	type = FieldType.DOUBLE;
	            break;
	        case "float":
	        	type = FieldType.FLOAT;
	            break;
	        case "boolean":
	        	type = FieldType.BOOLEAN;
	            break;
	        case "binary":
	        	type = FieldType.BINARY;
	            break;
	        default:
	        	type = FieldType.STRING;
	        	break;
		}
		return type;
	}
	
	
}
