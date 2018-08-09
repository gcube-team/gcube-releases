package org.gcube.data.analysis.statisticalmanager.experimentspace;

import java.util.List;

import org.gcube.data.analysis.statisticalmanager.stubs.SMParameter;
import org.gcube.data.analysis.statisticalmanager.stubs.SMTypeParameter;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnType;
import org.gcube.dataanalysis.ecoengine.datatypes.ColumnTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.TablesList;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.StatisticalServiceType;



public class FactoryComputationParameter {
	

	public static SMParameter createParameter(StatisticalType type) {
		
		SMTypeParameter parameterType = new SMTypeParameter();
		if (type instanceof InputTable){

			parameterType.setName(StatisticalServiceType.TABULAR);
			List<TableTemplates> templateNames = ((InputTable) type).getTemplateNames();
			
			int i = 0;
			String[] values = new String[templateNames.size()];
			for (TableTemplates template : templateNames){
				values[i++] = template.toString();
			}

			parameterType.setValues(values);
			return new SMParameter(type.getDefaultValue(),type.getDescription(),
					type.getName(),parameterType);
		}
		
		if (type instanceof TablesList) {
			parameterType.setName(StatisticalServiceType.TABULAR_LIST);
			List<TableTemplates> templateNames = ((TablesList) type).getTemplates();
			int i = 0;
			String[] values = new String[templateNames.size()];
			for (TableTemplates template : templateNames){
				values[i] = template.toString();
				i++;
			}

			parameterType.setValues(values);
			return new SMParameter(type.getDefaultValue(),type.getDescription(),
					type.getName(),parameterType);
		}
		
		if (((type instanceof PrimitiveType) && ((PrimitiveType)type).getType() != PrimitiveTypes.CONSTANT)){
			
			String[] values = {((PrimitiveType) type).getClassName()};
			
			if(((PrimitiveType) type).getType() == PrimitiveTypes.ENUMERATED) {
				parameterType.setName(StatisticalServiceType.ENUM);
				
				Enum[] enums = (Enum[]) ((PrimitiveType) type).getContent();
				values = new String[enums.length];
				int i = 0;
				for(Enum en : enums) {
					values[i++] = en.name();
				}	
			} else if(((PrimitiveType) type).getType() == PrimitiveTypes.FILE) {
				parameterType.setName(StatisticalServiceType.FILE);
			} else {
				parameterType.setName(StatisticalServiceType.PRIMITIVE); 	
			}
						
			parameterType.setValues(values);
			
			return new SMParameter(type.getDefaultValue(),type.getDescription(),
					type.getName(),parameterType);
		}
		
		if (type instanceof PrimitiveTypesList) {
			
			parameterType.setName(StatisticalServiceType.LIST);
			
			if ((((PrimitiveTypesList)type).getList() != null) && 
					(!((PrimitiveTypesList)type).getList().isEmpty())) {
				String[] values = {((PrimitiveTypesList)type).getList().get(0).getClassName()};
				parameterType.setValues(values);
			} else {
				String[] values = {((PrimitiveTypesList)type).getType().toString()};
				parameterType.setValues(values);
			}
			
			return new SMParameter(type.getDefaultValue(),type.getDescription(),
					type.getName(),parameterType);
		}
		
		if (type instanceof ColumnTypesList) {
			
			parameterType.setName(StatisticalServiceType.COLUMN_LIST);
			
			String[] values = {((ColumnTypesList)type).getTabelName()};
			parameterType.setValues(values);
		
			return new SMParameter(type.getDefaultValue(), type.getDescription(),
					type.getName(), parameterType);
		}
		
		if (type instanceof ColumnType) {
			
			parameterType.setName(StatisticalServiceType.COLUMN);
			
			String[] values = {((ColumnType)type).getTableName()};
			parameterType.setValues(values);
		
			return new SMParameter(type.getDefaultValue(), type.getDescription(),
					type.getName(), parameterType);
		}
		
		return null;
	}
	

	
	
	
}
