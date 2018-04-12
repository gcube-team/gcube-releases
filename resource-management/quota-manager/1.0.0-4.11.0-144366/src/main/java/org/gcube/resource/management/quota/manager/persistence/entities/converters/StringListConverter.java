package org.gcube.resource.management.quota.manager.persistence.entities.converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * StringListConverter
 *  
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 *
 */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String>{
	@Override
	public String convertToDatabaseColumn(List<String> list) {
		if (list!=null && list.size()>0){
			StringBuilder builder = new StringBuilder();
			for (String value: list)
				builder.append(value).append(",||,");
			return builder.substring(builder.length()-4, builder.length());
		} else return "";
	}
	@Override
	public List<String> convertToEntityAttribute(String dbEntry) {
		if (dbEntry!=null && !dbEntry.isEmpty()){
			return Arrays.asList(dbEntry.split(",\\|\\|,"));
		}
		else return new ArrayList<String>(0);
	}



}

