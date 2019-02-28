/**
 *
 */
package org.gcube.portlets.user.tdtemplate.server.service;

import java.util.Collection;
import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.finals.AddToFlowAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.DimensionReference;
import org.gcube.data.analysis.tabulardata.commons.utils.FormatReference;
import org.gcube.data.analysis.tabulardata.commons.utils.LocaleReference;
import org.gcube.data.analysis.tabulardata.commons.utils.TimeDimensionReference;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.OnRowErrorAction;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TemplateDescription;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.metadata.Locales;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.TabularResourceMetadata;
import org.gcube.data.analysis.tabulardata.service.template.TemplateId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 6, 2014
 *
 */
public class TemplateService {

	private TabularDataService service;
	private String scope;
	private String username;

	public static Logger logger = LoggerFactory.getLogger(TemplateService.class);

	/**
	 *
	 * @param columnType
	 * @param valueType
	 * @param reference
	 * @return
	 */
	public static TemplateColumn<? extends DataType> createTemplateColumn(ColumnCategory columnType, Class<? extends DataType> valueType, FormatReference reference){


		 if(valueType.isAssignableFrom(NumericType.class)){
			 return reference==null? new TemplateColumn<NumericType>(columnType, NumericType.class):new TemplateColumn<NumericType>(columnType, NumericType.class, reference);
		 }else if(valueType.isAssignableFrom(TextType.class)){
			 return reference==null? new TemplateColumn<TextType>(columnType, TextType.class):new TemplateColumn<TextType>(columnType, TextType.class, reference);
		 } else if (valueType.isAssignableFrom(IntegerType.class)){
			 return  reference==null? new TemplateColumn<IntegerType>(columnType, IntegerType.class):new TemplateColumn<IntegerType>(columnType, IntegerType.class,reference);
		 } else if (valueType.isAssignableFrom(BooleanType.class)){
			 return reference==null? new TemplateColumn<BooleanType>(columnType, BooleanType.class):new TemplateColumn<BooleanType>(columnType, BooleanType.class,reference);
		 } else if (valueType.isAssignableFrom(DateType.class)){
			 return reference==null? new TemplateColumn<DateType>(columnType, DateType.class):new TemplateColumn<DateType>(columnType, DateType.class,reference);
		 } else if (valueType.isAssignableFrom(GeometryType.class)){
			 return reference==null? new TemplateColumn<GeometryType>(columnType, GeometryType.class):new TemplateColumn<GeometryType>(columnType, GeometryType.class,reference);
		 }

		 return null;
	}

	/**
	 *
	 * @param valueType
	 * @param locale
	 * @return
	 */
	public static TemplateColumn<? extends DataType> createTemplateColumnForCodeName(Class<? extends DataType> valueType, String locale){

		LocaleReference localeReference;

		if(locale==null || locale.isEmpty())
			localeReference = new LocaleReference(Locales.getDefaultLocale());
		else
			localeReference = new LocaleReference(locale);

		 if(valueType.isAssignableFrom(NumericType.class)){
			 return new TemplateColumn<NumericType>(ColumnCategory.CODENAME, NumericType.class, localeReference);
		 }else if(valueType.isAssignableFrom(TextType.class)){
			 return new TemplateColumn<TextType>(ColumnCategory.CODENAME, TextType.class, localeReference);
		 } else if (valueType.isAssignableFrom(IntegerType.class)){
			 return new TemplateColumn<IntegerType>(ColumnCategory.CODENAME, IntegerType.class, localeReference);
		 } else if (valueType.isAssignableFrom(BooleanType.class)){
			 return new TemplateColumn<BooleanType>(ColumnCategory.CODENAME, BooleanType.class, localeReference);
		 } else if (valueType.isAssignableFrom(DateType.class)){
			 return new TemplateColumn<DateType>(ColumnCategory.CODENAME, DateType.class, localeReference);
		 } else if (valueType.isAssignableFrom(GeometryType.class)){
			 return new TemplateColumn<GeometryType>(ColumnCategory.CODENAME, GeometryType.class, localeReference);
		 }

		 return null;
	}

	/**
	 *
	 * @param columnType
	 * @param valueType
	 * @param tableId
	 * @param columnId
	 * @return
	 */
	public static TemplateColumn<? extends DataType> createTemplateColumnForDimension(ColumnCategory columnType, Class<? extends DataType> valueType, int tableId, String columnId){

//		TemplateColumn<IntegerType> dimColumn = new TemplateColumn<IntegerType>(ColumnCategory.DIMENSION, IntegerType.class, new DimensionReference(new TableId(32), new ColumnLocalId("columnId")));

		 if(valueType.isAssignableFrom(NumericType.class)){
			 return new TemplateColumn<NumericType>(columnType, NumericType.class, new DimensionReference(new TableId(tableId), new ColumnLocalId(columnId)));
		 }else if(valueType.isAssignableFrom(TextType.class)){
			 return new TemplateColumn<TextType>(columnType, TextType.class,new DimensionReference(new TableId(tableId), new ColumnLocalId(columnId)));
		 } else if (valueType.isAssignableFrom(IntegerType.class)){
			 return new TemplateColumn<IntegerType>(columnType, IntegerType.class, new DimensionReference(new TableId(tableId), new ColumnLocalId(columnId)));
		 } else if (valueType.isAssignableFrom(BooleanType.class)){
			 return new TemplateColumn<BooleanType>(columnType, BooleanType.class,new DimensionReference(new TableId(tableId), new ColumnLocalId(columnId)));
		 } else if (valueType.isAssignableFrom(DateType.class)){
			 return new TemplateColumn<DateType>(columnType, DateType.class,new DimensionReference(new TableId(tableId), new ColumnLocalId(columnId)));
		 } else if (valueType.isAssignableFrom(GeometryType.class)){
			 return new TemplateColumn<GeometryType>(columnType, GeometryType.class,new DimensionReference(new TableId(tableId), new ColumnLocalId(columnId)));
		 }

		 return null;
	}

	/**
	 *
	 * @param columnType
	 * @param valueType
	 * @param period
	 * @param formatIdentifier
	 * @return
	 */
	public static TemplateColumn<? extends DataType> createTemplateColumnForTimeDimension(ColumnCategory columnType, Class<? extends DataType> valueType, PeriodType period, String formatIdentifier){

		 if(valueType.isAssignableFrom(NumericType.class)){
			 return new TemplateColumn<NumericType>(columnType, NumericType.class, new TimeDimensionReference(period, formatIdentifier));

		 }else if(valueType.isAssignableFrom(TextType.class)){
			 return new TemplateColumn<TextType>(columnType, TextType.class, new TimeDimensionReference(period, formatIdentifier));

		 } else if (valueType.isAssignableFrom(IntegerType.class)){
			 return new TemplateColumn<IntegerType>(columnType, IntegerType.class, new TimeDimensionReference(period, formatIdentifier));

		 } else if (valueType.isAssignableFrom(BooleanType.class)){
			 return  new TemplateColumn<BooleanType>(columnType, BooleanType.class, new TimeDimensionReference(period, formatIdentifier));

		 } else if (valueType.isAssignableFrom(DateType.class)){
			 return new TemplateColumn<DateType>(columnType, DateType.class, new TimeDimensionReference(period, formatIdentifier));

		 } else if (valueType.isAssignableFrom(GeometryType.class)){
			 return new TemplateColumn<GeometryType>(columnType, GeometryType.class, new TimeDimensionReference(period, formatIdentifier));
		 }

		 return null;
	}



	public static Template generateTemplate(TemplateCategory templateCategory, List<TemplateColumn<? extends DataType>> columns, OnRowErrorAction onErrorAction, AddToFlowAction flowAction){

		TemplateColumn<?>[] cms = columns.toArray(new TemplateColumn<?>[columns.size()]);

		Template template = Template.create(templateCategory, cms);

		if(onErrorAction!=null)
			template.setOnErrorAction(onErrorAction);

		if(flowAction!=null)
			template.setAddToFlow(flowAction);

		return template;
	}

	public TemplateService(String scope, String username){
		this.scope = scope;
		this.username = username;
		logger.info("Getting TabularDataServiceFactory...");
		service = TabularDataServiceFactory.getService();
	}

	public TemplateId saveTemplate(String name, String description, String agency, Template template) throws Exception{
		try{
			logger.info("Saving template with parameter: name: "+ name+", description: "+ description+", agency: "+ agency+", template: "+ template);
			logger.info("Username is: "+ username+", scope is: "+ scope);
			return service.saveTemplate(name, description, agency, template);
		}catch (Exception e) {
			throw new Exception("Sorry, an error occurred in saving the template ",e);
		}
	}

	public TemplateDescription updateTemplate(long templateId, Template template) throws Exception{
		try{
			logger.info("Updating template id: "+templateId+ ", template: "+ template);
			logger.info("Username is: "+ username+", scope is: "+ scope);
			return service.update(new TemplateId(templateId), template);
		}catch (Exception e) {
			throw new Exception("Sorry, an error occurred in updating the template ",e);
		}
	}


	/*public TabularDataService getService() {
		return service;
	}*/

	public List<TemplateDescription> getTemplates() throws Exception {

		try{
			return service.getTemplates();
		}catch (Exception e) {
			throw new Exception("Sorry, an error occurred in getting templates ",e);
		}
	}


	public TemplateDescription getTemplate(long templateId) throws Exception {

		try{
			//TODO REMOVE THIS IN PRODUCTION
//			return getFakeTemplate();
			logger.info("Retrieving Template "+templateId+" from service...");
			logger.info("Scope: ["+scope+"], username: ["+username+"]");
			return service.getTemplate(new TemplateId(templateId));
		}catch (Exception e) {
			throw new Exception("Sorry, an error occurred in getting template by id: "+templateId,e);
		}
	}


	/**
	 * DEBUG FAKE TEMPLATE
	 * @return
	 */
	public TemplateDescription getFakeTemplate(){
		/*
		logger.warn("RETURNING FAKE TEMPLATE TO DEBUG!!!!");
		TemplateColumn<TextType> yearColumn = new TemplateColumn<TextType>(ColumnCategory.ATTRIBUTE, TextType.class, new FormatReference(DataTypeFormats.getFormatsPerDataType(TextType.class).get(0).getId()));

		TemplateColumn<IntegerType> quarterColumn = new TemplateColumn<IntegerType>(ColumnCategory.MEASURE, IntegerType.class, new FormatReference(DataTypeFormats.getFormatsPerDataType(IntegerType.class).get(0).getId()));

		TemplateColumn<IntegerType> column = new TemplateColumn<IntegerType>(ColumnCategory.TIMEDIMENSION, IntegerType.class , new TimeDimensionReference(PeriodType.MONTH, PeriodType.MONTH.getAcceptedFormats().get(0).getId()));

		Template template = Template.create(TemplateCategory.GENERIC,column, yearColumn, quarterColumn );

		Assert.assertEquals(template.getColumns(), template.getActualStructure());

		List<TemplateColumn<?>> groups = new ArrayList<>();
		groups.add(yearColumn);

		TimeAggregationAction taa = new TimeAggregationAction(column, PeriodType.YEAR, groups, new AggregationPair(quarterColumn, AggregationFunction.SUM));

		template.addAction(taa);

		template.setOnErrorAction(OnRowErrorAction.ASK);

		return new TemplateDescription(1,"FAKE OWN", "FAKE NAME", "FAKE description", "FAKE agency", template, new ArrayList<String>());
		*/
		return null;
	}


	public List<org.gcube.data.analysis.tabulardata.service.tabular.TabularResource> getTabularResources() throws Exception{
		try{
			logger.info("Getting Tabular Resources...");
			return service.getTabularResources();
		}catch (Exception e) {
			throw new Exception("Sorry, an error occurred on getting Tabular Resources: ",e);
		}
	}

	public TabularResource getTabularResourceById(long tabularResourceId) throws Exception{
		try{
			logger.info("Getting Tabular Resource by Id: "+tabularResourceId);
			return service.getTabularResource(new TabularResourceId(tabularResourceId));
		}catch (Exception e) {
			throw new Exception("Sorry, an error occurred on getting Tabular Resources: ",e);
		}
	}

	public Table getTable(TableId tableId) throws NoSuchTableException{
		logger.info("Getting Table by tableId "+tableId);
		return service.getTable(tableId);
	}

	public Table getTable(long tabularResourceId) throws NoSuchTableException, NoSuchTabularResourceException{
		logger.info("Getting Table by tabularResourceId "+tabularResourceId);;
		return service.getLastTable(new TabularResourceId(tabularResourceId));
	}

	public TabularResource createFlow(List<TabularResourceMetadata<?>> metadata) throws NoSuchTableException{
		logger.info("Creating Flow...");
		TabularResource resource = service.createFlow();
		resource.setAllMetadata(metadata);
		logger.info("Flow created, returning resource "+resource.getId());
		return resource;
	}

	public AddToFlowAction getFlowByTemplateId(long templateId) throws Exception{

		TemplateDescription tmDescr = getTemplate(templateId);

		if(tmDescr!=null && tmDescr.getTemplate()!=null)
			return tmDescr.getTemplate().getAddToFlow();
		else
			throw new Exception("Template Description or Template is null!");
	}


	public Collection<TabularResourceMetadata<?>> getFlowMetadataByTemplateId(long templateId) throws Exception{

		AddToFlowAction flow = getFlowByTemplateId(templateId);

		try{
			return getFlowMetadataByTemplateFlowAction(flow);
		}catch(Exception e){
			logger.error("Error on recovering flow metadata by template id: "+templateId, e);
			throw new Exception("Error on recovering flow metadata by template id: "+templateId, e);
		}
	}

	public Collection<TabularResourceMetadata<?>> getFlowMetadataByTemplateFlowAction(AddToFlowAction flow) throws Exception{

		if(flow!=null){
			long trId = flow.getTabularResource();
			TabularResource tabularResource = getTabularResourceById(trId);
			return tabularResource.getAllMetadata();
		}else
			throw new Exception("AddToFlowAction is null");

	}
}
