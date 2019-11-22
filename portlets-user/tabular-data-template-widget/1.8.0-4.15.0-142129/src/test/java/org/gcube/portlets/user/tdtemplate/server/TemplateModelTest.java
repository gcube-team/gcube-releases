/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnDescription;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.DimensionReference;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.DescriptionMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata;
import org.gcube.data.analysis.tabulardata.service.template.TemplateId;
import org.gcube.portlets.user.tdtemplate.server.converter.ConverterToTdTemplateModel;
import org.gcube.portlets.user.tdtemplate.shared.TdTDataType;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 14, 2014
 *
 */
public class TemplateModelTest {
	
	ConverterToTdTemplateModel builder;
	
	public static void main(String[] args) throws Exception {
		
//		TemplateColumn<NumericType> column = new TemplateColumn<NumericType>(ColumnCategory.ATTRIBUTE, NumericType.class);
//		column.getRules().add(new ColumnRule<NumericType>(null));
//				
//		Template template = Template.create(TemplateCategory.DATASET, column);
//		
//		template.setOnErrorAction(ON_ERROR_ACTION.DISCARD);
//		
//		template.setFinalAction(TemplateFinalAction.none());
//		
//		System.out.println(template.toString());
//		
		
		TemplateColumn<IntegerType> dimColumn = new TemplateColumn<IntegerType>(ColumnCategory.DIMENSION, 
				IntegerType.class, new DimensionReference(new TableId(32), new ColumnLocalId("columnId")));
		
		for (TemplateCategory template : TemplateCategory.values()) {
			
			System.out.println("Template: "+template);
			
//			List<ColumnCategory> categories = new ArrayList<ColumnCategory>(template.getAllowedColumn().size());
			
			for (ColumnDescription columnDescription : template.getAllowedColumn()) {
				
				System.out.println("\t ColumnCategory: "+columnDescription);
				
				List<Class<? extends DataType>> dataTypes = columnDescription.getColumnCategory().getAllowedClasses();
				getTdTDataTypeFromDataType(dataTypes);
				
			}
		}
		
		/*
		List<TdTTemplateType> list = ConverterToTdTemplateModel.getTdTTemplateTypeFromTemplateCategoryValues();

		
		try {
			
			TemplateCategory categ = ConverterToTemplateServiceModel.tdTemplateTypeToTemplateCategory(list.get(1));
			
			System.out.println("category is "+categ.name());
			
			tdTdTDataTypeToDataType(null);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	
	private void createTemplate(){
		
		TabularDataService service = TabularDataServiceFactory.getService();
		org.gcube.data.analysis.tabulardata.service.tabular.TabularResource resource = service.createTabularResource();
		resource.setMetadata(new NameMetadata("templateTest"));
		resource.setMetadata(new DescriptionMetadata("templateTest"));
		
		TemplateId templateId = service.saveTemplate("testTemplate", "a test template", "CNR", null);
	}
	
	public static List<TdTDataType> getTdTDataTypeFromDataType(List<Class<? extends DataType>> dataTypes) throws Exception{
		
		List<TdTDataType> listTdTDataType  = new ArrayList<TdTDataType>();
		
		if(dataTypes==null)
			throw new Exception("List of data type is null");
		
		
		for (Class<? extends DataType> class1 : dataTypes) {
			
//			if(class1.isAssignableFrom(TextType.class)){
//				
//				System.out.println("class get name: "+ ((TextType.class).getName()));
//				System.out.println("name: "+ (class1.getName()));
//			}
	
			listTdTDataType.add(new TdTDataType(class1.getName(), class1.getSimpleName()));
			System.out.println("\t \t DataType: "+class1 + " simpleName: " +class1.getSimpleName());
			
			
			
		}
		return listTdTDataType;
	}
	
	
	public static Class<? extends DataType> tdTdTDataTypeToDataType(TdTDataType tdTdTDataType) throws Exception{
		
		if(tdTdTDataType==null)
			throw new Exception("TdTDataType is null");
		
		if(tdTdTDataType.getId().compareTo(NumericType.class.getName())==0)
			return NumericType.class;
		else if(tdTdTDataType.getId().compareTo(TextType.class.getName())==0)
			return TextType.class;
		else if(tdTdTDataType.getId().compareTo(BooleanType.class.getName())==0)
			return BooleanType.class;
		else if(tdTdTDataType.getId().compareTo(DateType.class.getName())==0)
			return DateType.class;
		else if(tdTdTDataType.getId().compareTo(GeometryType.class.getName())==0)
			return GeometryType.class;
		else if(tdTdTDataType.getId().compareTo(IntegerType.class.getName())==0)
			return IntegerType.class;
		
		return null;
	}
	
	
//	public static List<TdTDataType> tdDataTypeFromTdTDataType(TdTDataType dataType) throws Exception{
//		
//		List<TdTDataType> listTdTDataType  = new ArrayList<TdTDataType>();
//		
//		if(dataTypes==null)
//			throw new Exception("List of data type is null");
//		
//		
//		for (Class<? extends DataType> class1 : dataTypes) {
//			
////			if(class1.isAssignableFrom(TextType.class)){
////				
////				System.out.println("qui "+ ((TextType.class).getName()));
////			}
//	
//			listTdTDataType.add(new TdTDataType(class1.getSimpleName(), class1.getSimpleName()));
//		
////			System.out.println("\t \t DataType: "+class1 + " simpleName: " +class1.getSimpleName());
//			
//			
//			
//		}
//		return listTdTDataType;
//	}
	

}
