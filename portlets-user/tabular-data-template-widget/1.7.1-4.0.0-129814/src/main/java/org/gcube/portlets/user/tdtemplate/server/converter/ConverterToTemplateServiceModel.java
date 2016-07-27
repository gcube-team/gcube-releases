/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.finals.AddToFlowAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.finals.DuplicateBehaviour;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AggregationPair;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.GenerateTimeDimensionGroup;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.NormalizeTableAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.TimeAggregationAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.FormatReference;
import org.gcube.data.analysis.tabulardata.commons.utils.Licence;
import org.gcube.data.analysis.tabulardata.commons.utils.LocaleReference;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.OnRowErrorAction;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Avg;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Count;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Max;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Min;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.ST_Extent;
import org.gcube.data.analysis.tabulardata.expression.composite.aggregation.Sum;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.metadata.Locales;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.AgencyMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.DescriptionMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.LicenceMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.NameMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.RightsMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.TabularResourceMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.ValidSinceMetadata;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.ValidUntilMetadata;
import org.gcube.portlets.user.td.expressionwidget.server.C_ExpressionParser;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.gcube.portlets.user.tdtemplate.shared.TdFlowModel;
import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;
import org.gcube.portlets.user.tdtemplate.shared.TdTDataType;
import org.gcube.portlets.user.tdtemplate.shared.TdTTemplateType;
import org.gcube.portlets.user.tdtemplateoperation.server.ConvertOperationForService;
import org.gcube.portlets.user.tdtemplateoperation.shared.CreateTimeDimensionOptions;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.CreateTimeDimensionColumnAction;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.NormalizeColumnsAction;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataAction;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.TimeAggregationColumnAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ConverterToTemplateServiceModel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 27, 2014
 */
public class ConverterToTemplateServiceModel {
	

	public static Logger logger = LoggerFactory.getLogger(ConverterToTemplateServiceModel.class);
	
	/**
	 * Td td t column category to column category.
	 *
	 * @param tdtColumnCategory the tdt column category
	 * @return the column category
	 * @throws Exception the exception
	 */
	public static ColumnCategory tdTdTColumnCategoryToColumnCategory(TdTColumnCategory tdtColumnCategory) throws Exception{
		
		if(tdtColumnCategory==null)
			throw new Exception("ColumnCategory is null");
		
		String id = tdtColumnCategory.getId();
		
		if(id==null || id.isEmpty())
			throw new Exception("ColumnCategory ID is null");
		
		
		try{
			return ColumnCategory.valueOf(tdtColumnCategory.getId());
			
		}catch (Exception e) {
			throw new Exception("ColumnCategory ID "+ tdtColumnCategory.getId()+" is not a "+ColumnCategory.class);
		}
		
	}
	
	/**
	 * Template category from template name.
	 *
	 * @param templateName the template name
	 * @return the template category
	 * @throws Exception the exception
	 */
	public static TemplateCategory templateCategoryFromTemplateName(String templateName) throws Exception{
		
		try{
			return TemplateCategory.valueOf(templateName);
			
		}catch (Exception e) {
			throw new Exception("The Name "+ templateName+" is not a value of "+TemplateCategory.class);
		}
		
	}
	

	/**
	 * Period name to period type.
	 *
	 * @param periodName the period name
	 * @return the period type
	 * @throws Exception the exception
	 */
	public static PeriodType periodNameToPeriodType(String periodName) throws Exception{
		
		try{
			return PeriodType.valueOf(periodName);
			
		}catch (Exception e) {
			throw new Exception("The PeriodType "+ periodName+" is not a valid fromName of "+PeriodType.class);
		}
		
	}
	
	
	/**
	 * Locale id to lacale reference.
	 *
	 * @param localeId the locale id
	 * @return the locale reference
	 */
	public static LocaleReference localeIdToLacaleReference(String localeId){
		
		if(localeId==null || localeId.isEmpty())
			return  new LocaleReference(Locales.getDefaultLocale());
		
		logger.info("Founding locale for: "+localeId);

		 List<String> list = new ArrayList<String>(Arrays.asList(Locales.ALLOWED_LOCALES));
		 
		 int index = list.indexOf(localeId);
		 
		 logger.info("Locale index is: "+index);
		 
		 if(index >= 0)
			 return new LocaleReference(list.get(index));
		 
		 return new LocaleReference(Locales.getDefaultLocale());
	}
	
	/**
	 * Template category from column category name.
	 *
	 * @param columnCategoryName the column category name
	 * @return the template category
	 * @throws Exception the exception
	 */
	public static TemplateCategory templateCategoryFromColumnCategoryName(String columnCategoryName) throws Exception{
		
		try{
			return TemplateCategory.valueOf(columnCategoryName);
			
		}catch (Exception e) {
			throw new Exception("The Name "+ columnCategoryName+" is not a value of "+TemplateCategory.class);
		}
		
	}
	
	/**
	 * Td template type to template category.
	 *
	 * @param templateType the template type
	 * @return the template category
	 * @throws Exception the exception
	 */
	public static TemplateCategory tdTemplateTypeToTemplateCategory(TdTTemplateType templateType) throws Exception{
		
		if(templateType==null)
			throw new Exception("TdTTemplateType is null");
		
		String id = templateType.getId();
		
		if(id==null || id.isEmpty())
			throw new Exception("TdTTemplateType ID is null");
		
		
		try{
			return templateCategoryFromTemplateName(templateType.getId());
			
		}catch (Exception e) {
			throw new Exception("TdTTemplateType ID "+ templateType.getId()+" is not a "+TemplateCategory.class);
		}
		
	}
	
	/**
	 * Td td t data type to data type.
	 *
	 * @param tdTdTDataType the td td t data type
	 * @return the class<? extends data type>
	 * @throws Exception the exception
	 */
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
	
	/**
	 * Td td t format referece to format reference.
	 *
	 * @param dataType the data type
	 * @return the format reference
	 */
	public static FormatReference tdTdTFormatRefereceToFormatReference(TdTDataType dataType) {
		
		if(dataType.getFormatReference()!=null){
			return new FormatReference(dataType.getFormatReference().getId());
		}
		return null;
	}
	
	/**
	 * On row error action.
	 *
	 * @param error the error
	 * @return the on row error action
	 * @throws Exception the exception
	 */
	public static OnRowErrorAction onRowErrorAction(String error) throws Exception{
		
		try{
			return OnRowErrorAction.valueOf(error);
			
		}catch (Exception e) {
			throw new Exception("The Error "+ error+" is not a value of "+OnRowErrorAction.class);
		}
		
	}
	
	
	/**
	 * Convert rule expression.
	 *
	 * @param expr the expr
	 * @return the expression
	 * @throws Exception the exception
	 */
	public static Expression convertRuleExpression(C_Expression expr) throws Exception{
		C_ExpressionParser parser = new C_ExpressionParser();
		try{
			logger.info("Converting C_Expression.."+expr);
			return parser.parse(expr);
			
		}catch (Exception e) {
			throw new Exception("C_ExpressionParser generated an error: ", e);
		}
		
	}
	
	/**
	 * Convert licence.
	 *
	 * @param licenseValue the license value
	 * @return the licence
	 * @throws Exception the exception
	 */
	public static Licence convertLicence(String licenseValue) throws Exception{
		
		try{
			return Licence.valueOf(licenseValue);
			
		}catch (Exception e) {
			throw new Exception("The Licence "+ licenseValue+" is not a value of "+Licence.class);
		}
	}
	
	/**
	 * Convert flow.
	 *
	 * @param flow the flow
	 * @return the list
	 */
	public static List<TabularResourceMetadata<?>> convertFlow(TdFlowModel flow){
		
//		TabularResource resource = service.createFlow();
		List<TabularResourceMetadata<?>> metadata = new ArrayList<>();
		
		metadata.add(new NameMetadata(flow.getName()));
		metadata.add(new AgencyMetadata(flow.getAgency()));
		metadata.add(new DescriptionMetadata(flow.getDescription()));
		metadata.add(new RightsMetadata(flow.getRights()));
		
		if(flow.getFromDate()!=null)
			metadata.add(new ValidSinceMetadata(DateToCalendar(flow.getFromDate())));
		
		if(flow.getToDate()!=null){
			metadata.add(new ValidUntilMetadata(DateToCalendar(flow.getToDate())));
		}
		
		if(flow.getLicenceId()!=null){
			try {
				Licence licence = convertLicence(flow.getLicenceId());
				metadata.add(new LicenceMetadata(licence));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			
		}

		return metadata;
	}
	
	/**
	 * Period type.
	 *
	 * @param periodType the period type
	 * @return the period type
	 * @throws Exception the exception
	 */
	public static PeriodType periodType(String periodType) throws Exception {

		try {
			return PeriodType.valueOf(periodType);

		} catch (Exception e) {
			throw new Exception("The periodType " + periodType
					+ " is not a value of " + PeriodType.class);
		}
	}
	
	/**
	 * Convert aggregation function for id.
	 *
	 * @param aggregationId the aggregation id
	 * @return the class<? extends expression>
	 */
	public static Class<? extends Expression> convertAggregationFunctionForId(String aggregationId) {
		

			if(Avg.class.getName().compareTo(aggregationId)==0){
				return Avg.class;
			}else if(Count.class.getName().compareTo(aggregationId)==0){
				return Count.class;
//			}else if(First.class.getName().compareTo(aggregationId)==0){
//				return First.class;
//			}else if(Last.class.getName().compareTo(aggregationId)==0){
//				return Last.class;
			}else if(Max.class.getName().compareTo(aggregationId)==0){
				return Max.class;
			}else if(Min.class.getName().compareTo(aggregationId)==0){
				return Min.class;
			}else if(Sum.class.getName().compareTo(aggregationId)==0){
				return Sum.class;
			}else if (ST_Extent.class.getName().compareTo(aggregationId)==0){
				return ST_Extent.class;
			}
			
			return null;
	}
	
	/**
	 * Gets the column reference.
	 *
	 * @param table the table
	 * @param columnName the column name
	 * @return the column reference
	 * @throws Exception the exception
	 */
	public static ColumnReference getColumnReference(Table table, String columnName) throws Exception{
		logger.info("Get Column Reference to column name: "+columnName);
		
		if(table==null){
			logger.error("Get Column Reference , Table is null");
			throw new Exception("Table is null");
		}
		
		logger.info("Table name is: "+table.getName());
		
		Column source = null;
		try{
			
			source = table.getColumnByName(columnName);
		
		}catch (Exception e) {
			logger.error("Error on recovering column by name",e);
			throw new Exception("Error on recovering column by name");
		}
		
		return table.getColumnReference(source);
	}
	

	
	
	/**
	 * Convert duplicate behaviour.
	 *
	 * @param duplicateBehaviourId the duplicate behaviour id
	 * @return the duplicate behaviour
	 * @throws Exception the exception
	 */
	public static DuplicateBehaviour convertDuplicateBehaviour(String duplicateBehaviourId) throws Exception{
		
		try{
			return DuplicateBehaviour.valueOf(duplicateBehaviourId);
			
		}catch (Exception e) {
			throw new Exception("The "+DuplicateBehaviour.class.getName() +" "+duplicateBehaviourId+" is not a value of "+Licence.class);
		}
	}
	
	/**
	 * Adds the to flow action.
	 *
	 * @param tabularResource the tabular resource
	 * @param duplicateBehaviour the duplicate behaviour
	 * @return the org.gcube.data.analysis.tabulardata.commons.templates.model.actions.finals. add to flow action
	 */
	public static org.gcube.data.analysis.tabulardata.commons.templates.model.actions.finals.AddToFlowAction addToFlowAction(org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource tabularResource, DuplicateBehaviour duplicateBehaviour){
		logger.info("Creating new AddToFlowAction...");
		return new AddToFlowAction(tabularResource, duplicateBehaviour);
	}
	
	/**
	 * Date to calendar.
	 *
	 * @param date the date
	 * @return the calendar
	 */
	public static Calendar DateToCalendar(Date date){ 
		  Calendar cal = Calendar.getInstance();
		  cal.setTime(date);
		  return cal;
	}
	
	
	/**
	 * Adds the action to template.
	 *
	 * @param template the template
	 * @param action the action
	 * @return the template with new structure (with the action added)
	 * @throws Exception the exception
	 */
	public static Template addActionToTemplate(Template template, TabularDataAction action) throws Exception{
		
		try {
			logger.trace("Adding Action To Template...");
			
			if(action instanceof TimeAggregationColumnAction){
				logger.trace("Action is AggregateByTime: ");
				TimeAggregationColumnAction aggregationTimeAction = (TimeAggregationColumnAction) action;
				logger.trace("AggregationColumnSession: "+aggregationTimeAction);

				if(aggregationTimeAction.getPeriodType()==null)
					throw new Exception("Sorry an error occurred when perfoming aggregate by time operation, selected period not found");
				
				if(aggregationTimeAction.getTimeColumns()==null || aggregationTimeAction.getTimeColumns().size()==0)
					throw new Exception("Sorry an error occurred when perfoming aggregate by time operation, time column not found");
				
				try{
					if(aggregationTimeAction.getGroupColumns()!=null && !aggregationTimeAction.getGroupColumns().isEmpty()){
						
						if(aggregationTimeAction.getAggregateFunctionPairs()!=null && !aggregationTimeAction.getAggregateFunctionPairs().isEmpty()){

							//PERIOD TYPE
							logger.trace("Coverting periodType...");
							org.gcube.portlets.user.tdtemplateoperation.shared.TdPeriodType period = aggregationTimeAction.getPeriodType();
							PeriodType periodType = org.gcube.portlets.user.tdtemplateoperation.server.ConvertOperationForService.periodType(period.getId());
							logger.info("Converted period: "+periodType.toString());
							
							//AGGREGATION PAIR
							logger.trace("Coverting List<AggregationPair>...");
							List<AggregationPair> listAggregationPair = org.gcube.portlets.user.tdtemplateoperation.server.ConvertOperationForService.aggregationPairListForTemplate(aggregationTimeAction.getAggregateFunctionPairs(), template);
							logger.info("Converted List<AggregationPair>: "+listAggregationPair.toString());

							//CONVERT GROUP COLUMN
							logger.trace("Coverting List of the GroupColumns...");
							List<TemplateColumn<?>> groupColumns = new ArrayList<TemplateColumn<?>>(aggregationTimeAction.getGroupColumns().size());
							for (TdColumnData columnData : aggregationTimeAction.getGroupColumns()) {
								
								try{
									TemplateColumn<?> serverColumn = ConvertOperationForService.getColumnFromTdColumnData(template, columnData);
									groupColumns.add(serverColumn);
								}catch(Exception e){
									logger.warn("Skipping "+columnData + ", conversion error!");
								}
							}
							
							logger.trace("Converted List of the GroupColumns ok!");

							//CONVERT TIME COLUMN
							List<TemplateColumn<?>> timeColumns = new ArrayList<TemplateColumn<?>>(aggregationTimeAction.getTimeColumns().size());
							for (TdColumnData columnData : aggregationTimeAction.getTimeColumns()) {
								TemplateColumn<?> serverColumn = ConvertOperationForService.getColumnFromTdColumnData(template, columnData);
								timeColumns.add(serverColumn);
							}
							
							logger.trace("Converted List of the TimeColumns ok!");
							
							logger.trace("groupColumns are: "+groupColumns.size());
							logger.trace("periodType is: "+periodType);
							logger.trace("listAggregationPair are: "+listAggregationPair.size());
							AggregationPair[] ag = new AggregationPair[listAggregationPair.size()];
							
							logger.trace("timeColumns is: "+timeColumns.get(0).toString());
							TimeAggregationAction timeAggregation = new TimeAggregationAction(timeColumns.get(0), periodType, groupColumns, listAggregationPair.toArray(ag));
							
							template.addAction(timeAggregation);

							return template;
						}else
							throw new Exception("Sorry an error occurred when perfoming aggregate by time operation, aggregation function not found");
						
					}else
						throw new Exception("Sorry an error occurred when perfoming aggregate by time operation, group column not found");
					
				}catch(Exception e){
					logger.error("Sorry an error occurred when perfoming aggregate by time operation", e);
					throw new Exception("Sorry an error occurred when perfoming aggregate by time operation", e);
				}
			}else if(action instanceof CreateTimeDimensionColumnAction){
				logger.trace("Action is GroupTimeColumnAction: ");
				CreateTimeDimensionColumnAction grouTime = (CreateTimeDimensionColumnAction) action;
				try{

					TdColumnData[] columns = grouTime.getColumns();
					CreateTimeDimensionOptions option = grouTime.getOption();

					if(columns==null || option ==null)
						throw new Exception("Columns or Option is null");
					
					GenerateTimeDimensionGroup timeDimensionGroup = null;
					TdColumnData year;
					TemplateColumn<?> yearColumn;
					TdColumnData month;
					TemplateColumn<?> monthColumn;
					
					switch (option) {
					case YEAR:
						if(columns.length==0)
							throw new Exception("Column Year not found");
						year = columns[0];
						yearColumn = ConvertOperationForService.getColumnFromTdColumnData(template, year);
						timeDimensionGroup = new GenerateTimeDimensionGroup((TemplateColumn<TextType>) yearColumn, "Time Dimension Group");
						break;
					case YEAR_MONTH:
						if(columns.length<2)
							throw new Exception("Column Year or Month not found");
						year = columns[0];
						yearColumn = ConvertOperationForService.getColumnFromTdColumnData(template, year);
						month = columns[1];
						monthColumn = ConvertOperationForService.getColumnFromTdColumnData(template, month);
						timeDimensionGroup = new GenerateTimeDimensionGroup((TemplateColumn<TextType>) yearColumn, (TemplateColumn<TextType>) monthColumn, true, "Time Dimension Group");
						break;
					case YEAR_MONTH_DAY:
						if(columns.length<3)
							throw new Exception("Column Year or Month or Day not found");
						year = columns[0];
						yearColumn = ConvertOperationForService.getColumnFromTdColumnData(template, year);
						month = columns[1];
						monthColumn = ConvertOperationForService.getColumnFromTdColumnData(template, month);
						TdColumnData day = columns[2];
						TemplateColumn<?> dayColumn = ConvertOperationForService.getColumnFromTdColumnData(template, day);
						timeDimensionGroup = new GenerateTimeDimensionGroup((TemplateColumn<TextType>) yearColumn, (TemplateColumn<TextType>) monthColumn, (TemplateColumn<TextType>) dayColumn, "Time Dimension Group");
						break;
					case YEAR_QUARTER:
						if(columns.length<2)
							throw new Exception("Column Year or Quarter not found");
						year = columns[0];
						yearColumn = ConvertOperationForService.getColumnFromTdColumnData(template, year);
						TdColumnData quarter = columns[1];
						TemplateColumn<?> quarterColumn = ConvertOperationForService.getColumnFromTdColumnData(template, quarter);
						timeDimensionGroup = new GenerateTimeDimensionGroup((TemplateColumn<TextType>) yearColumn, (TemplateColumn<TextType>) quarterColumn, false, "Time Dimension Group");
					}
					
					template.addAction(timeDimensionGroup);
					return template;
				}catch(Exception e){
					logger.error("Sorry an error occurred when perfoming group by time operation operation", e);
					throw new Exception("Sorry an error occurred when perfoming group by time operation", e);
				}
			}else if(action instanceof NormalizeColumnsAction){
				logger.trace("Action is NormalizeColumnsAction: ");
				NormalizeColumnsAction normalize = (NormalizeColumnsAction) action;
				try{
					if(normalize.getColumns()==null)
						throw new Exception("Sorry an error occurred when perfoming normalize operation, columns not found");
					
					List<TemplateColumn<?>> normalizeColumns = new ArrayList<TemplateColumn<?>>(normalize.getColumns().size());
					for (TdColumnData columnData : normalize.getColumns()) {
						TemplateColumn<?> serverColumn = ConvertOperationForService.getColumnFromTdColumnData(template, columnData);
						normalizeColumns.add(serverColumn);
					}
					String normalizeLabel = normalize.getNormalizeLabel().isEmpty()?"Normalize Label":normalize.getNormalizeLabel();
					String quantityLabel = normalize.getQuantityLabel().isEmpty()?"Quantity Label":normalize.getQuantityLabel();
						
					template.addAction(new NormalizeTableAction(normalizeColumns, normalizeLabel , quantityLabel));
					return template;
				}catch(Exception e){
					logger.error("Sorry an error occurred when perfoming group by time operation operation", e);
					throw new Exception("Sorry an error occurred when perfoming group by time operation", e);
				}
			}
			
		} catch (Exception e) {
			logger.error("Sorry an error occurred when perfoming action on Template", e);
			throw new Exception("Sorry an error occurred when perfoming action on Template", e);
		}
		
		return null;	
	}
}
