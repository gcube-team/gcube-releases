/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.finals.DuplicateBehaviour;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AddColumnAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AggregationPair;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.DeleteColumnAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.NormalizeTableAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.TimeAggregationAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.ValidateExpressionAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnDescription;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.DimensionReference;
import org.gcube.data.analysis.tabulardata.commons.utils.FormatReference;
import org.gcube.data.analysis.tabulardata.commons.utils.Licence;
import org.gcube.data.analysis.tabulardata.commons.utils.LocaleReference;
import org.gcube.data.analysis.tabulardata.commons.utils.TimeDimensionReference;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.OnRowErrorAction;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TemplateDescription;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.DataTypeFormats;
import org.gcube.data.analysis.tabulardata.model.ValueFormat;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.metadata.Locales;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
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
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.TabResourceType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdtemplate.server.TemplateUpdaterForDescription;
import org.gcube.portlets.user.tdtemplate.server.service.ExpressionEvaluatorInstance;
import org.gcube.portlets.user.tdtemplate.server.service.TemplateService;
import org.gcube.portlets.user.tdtemplate.server.session.CacheServerExpressions;
import org.gcube.portlets.user.tdtemplate.server.validator.ColumnCategoryConstraint;
import org.gcube.portlets.user.tdtemplate.server.validator.service.ColumnCategoryTemplateValidator;
import org.gcube.portlets.user.tdtemplate.shared.SPECIAL_CATEGORY_TYPE;
import org.gcube.portlets.user.tdtemplate.shared.TdBehaviourModel;
import org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition;
import org.gcube.portlets.user.tdtemplate.shared.TdFlowModel;
import org.gcube.portlets.user.tdtemplate.shared.TdLicenceModel;
import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;
import org.gcube.portlets.user.tdtemplate.shared.TdTDataType;
import org.gcube.portlets.user.tdtemplate.shared.TdTFormatReference;
import org.gcube.portlets.user.tdtemplate.shared.TdTFormatReferenceIndexer;
import org.gcube.portlets.user.tdtemplate.shared.TdTTemplateType;
import org.gcube.portlets.user.tdtemplate.shared.TdTTimePeriod;
import org.gcube.portlets.user.tdtemplate.shared.TdTemplateDefinition;
import org.gcube.portlets.user.tdtemplate.shared.TdTemplateUpdater;
import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;
import org.gcube.portlets.user.tdtemplate.shared.util.CutStringUtil;
import org.gcube.portlets.user.tdtemplate.shared.validator.ViolationDescription;
import org.gcube.portlets.user.tdtemplateoperation.server.ConvertOperationForGwtModule;
import org.gcube.portlets.user.tdtemplateoperation.shared.AggregatePair;
import org.gcube.portlets.user.tdtemplateoperation.shared.ServerObjectId;
import org.gcube.portlets.user.tdtemplateoperation.shared.ServerObjectType;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdAggregateFunction;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.NormalizeColumnsAction;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataActionDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ConverterToTdTemplateModel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 17, 2014
 */
public class ConverterToTdTemplateModel {
	
	
	public static Logger logger = LoggerFactory.getLogger(ConverterToTdTemplateModel.class);
	
	/**
	 * Gets the td t template type from template category values.
	 *
	 * @return the td t template type from template category values
	 */
	public static List<TdTTemplateType> getTdTTemplateTypeFromTemplateCategoryValues(){
		
		List<TdTTemplateType> templates = new ArrayList<TdTTemplateType>(TemplateCategory.values().length);
		for (TemplateCategory template : TemplateCategory.values()) {
			
			List<ViolationDescription> constraint = getTemplateCategoryConstraint(template);
			templates.add(new TdTTemplateType(template.name(), template.name(), constraint));
		}
		
		logger.info("Returning "+templates.size()+" template types");
		
		return templates;
		
	}
	
	/**
	 * Gets the template category constraint.
	 *
	 * @param cat the cat
	 * @return the template category constraint
	 */
	public static List<ViolationDescription> getTemplateCategoryConstraint(TemplateCategory cat) {

		List<ViolationDescription> violations  = new ArrayList<ViolationDescription>();
		
		ColumnCategoryTemplateValidator templateValidator = null;
		 
		switch (cat) {
			case CODELIST:
				templateValidator = new ColumnCategoryTemplateValidator(TemplateCategory.CODELIST);
				break;
			case DATASET:
				templateValidator = new ColumnCategoryTemplateValidator(TemplateCategory.DATASET);
				break;
			case GENERIC:
				templateValidator = new ColumnCategoryTemplateValidator(TemplateCategory.GENERIC);
				break;

			default:
				break;
		}

		if(templateValidator!=null){
			HashMap<String, ColumnCategoryConstraint> hash  = templateValidator.getTemplateColumnValidator();
			
			if(hash!=null){
				for (String columnType : hash.keySet()) {
					ColumnCategoryConstraint col = hash.get(columnType);
					violations.add(new ViolationDescription(columnType, col.getConstraintDescription()));
				}
			}
			
		}
		
		return violations;

	}
	
	
	/**
	 * Gets the on error values.
	 *
	 * @return the on error values
	 */
	public static List<String> getOnErrorValues(){
		
		List<String> onErrors = new ArrayList<String>(OnRowErrorAction.values().length);
		for (OnRowErrorAction error : OnRowErrorAction.values()) {
			onErrors.add(error.toString());
		}
		
		logger.info("Returning "+onErrors.size()+" on errors types");
		return onErrors;
	}
	
	/**
	 * Gets the td t column category from template category.
	 *
	 * @param template the template
	 * @return the td t column category from template category
	 * @throws Exception the exception
	 */
	public static List<TdTColumnCategory> getTdTColumnCategoryFromTemplateCategory(TemplateCategory template) throws Exception{
		
		if(template==null)
			throw new Exception("TemplateCategory is null");
		
		List<ColumnDescription> listColumns = template.getAllowedColumn();
		if(listColumns==null)
			throw new Exception("ColumnCategory allowed columns is null for template "+template);
		
		
		List<TdTColumnCategory> columns = new ArrayList<TdTColumnCategory>(listColumns.size());
		for (ColumnDescription  col: listColumns) {
			List<Class<? extends DataType>> dataTypes = col.getColumnCategory().getAllowedClasses();
			TdTColumnCategory tdtCC = new TdTColumnCategory(col.getColumnCategory().name(), col.getColumnCategory().name());
			tdtCC.setTdtDataType(getTdTDataTypeFromListDataType(dataTypes));
			columns.add(tdtCC);
		}
		
		logger.info("Returning "+columns.size()+" ColumnCategory");
		
		return columns;
		
	}
	
	/**
	 * Gets the td t format references.
	 *
	 * @param dataType the data type
	 * @return the td t format references
	 */
	public static List<TdTFormatReference> getTdTFormatReferences(Class<? extends DataType> dataType){

		List<ValueFormat> formats = DataTypeFormats.getFormatsPerDataType(dataType);

		List<TdTFormatReference> tdtFormats = new ArrayList<TdTFormatReference>(formats.size());
		for (ValueFormat valueFormat : formats) {
			tdtFormats.add(new TdTFormatReference(valueFormat.getId(), valueFormat.getExample()));
		}
		
		return tdtFormats;
	}
	
	
	/**
	 * Gets the td t column category from column category.
	 *
	 * @param col the col
	 * @return the td t column category from column category
	 * @throws Exception the exception
	 */
	public static TdTColumnCategory getTdTColumnCategoryFromColumnCategory(ColumnCategory col) throws Exception{
		
		if(col==null)
			throw new Exception("ColumnCategory is null");
		
//		if(col.isReferenceRequired()){
//			col.getReferenceClass();
//			return new TdTColumnCategory(col.name(), col.name()  )
//		}
		return new TdTColumnCategory(col.name(), col.name());
	}
	

	
	/**
	 * Gets the td t data type from list data type.
	 *
	 * @param dataTypes the data types
	 * @return the td t data type from list data type
	 * @throws Exception the exception
	 */
	public static List<TdTDataType> getTdTDataTypeFromListDataType(List<Class<? extends DataType>> dataTypes) throws Exception{
		
		List<TdTDataType> listTdTDataType  = new ArrayList<TdTDataType>();
		
		if(dataTypes==null)
			throw new Exception("List of data type is null");
		
		for (Class<? extends DataType> class1 : dataTypes) {
			try{
				listTdTDataType.add(getTdTDataTypeFromDataType(class1));
			}catch (Exception e) {
				logger.error("Errror on converting data type, skypping value",e);
			}
		}
		
		logger.info("Returning "+listTdTDataType.size()+" TdTDataType");
		
		return listTdTDataType;
	}
	
	
	/**
	 * Gets the td t data type from data type.
	 *
	 * @param dataType the data type
	 * @return the td t data type from data type
	 * @throws Exception the exception
	 */
	public static TdTDataType getTdTDataTypeFromDataType(Class<? extends DataType> dataType) throws Exception{
		
		if(dataType==null)
			throw new Exception("Data type is null");
		
		String purgedSimpleName = CutStringUtil.stringPurgeSuffix(dataType.getSimpleName(), "Type");
		
		logger.info("\t \t DataType: "+dataType + " purged simple name: " +purgedSimpleName);
		
		List<TdTFormatReference> formats = getTdTFormatReferences(dataType);
		
		if(formats!=null){
			TdTFormatReferenceIndexer indexer = new TdTFormatReferenceIndexer();
			
			for (TdTFormatReference tdTFormatReference : formats) {
				indexer.putFormat(tdTFormatReference);
			}
			
			return new TdTDataType(dataType.getName(), purgedSimpleName, indexer);
		}
		
		return new TdTDataType(dataType.getName(), purgedSimpleName);
	}
	
	/**
	 * Gets the on error actions.
	 *
	 * @return the on error actions
	 */
	public List<String> getOnErrorActions(){
		//TODO
		return null;
	}
	
	/**
	 * Gets the td template updater from template description.
	 *
	 * @param sdescr the sdescr
	 * @param service the service
	 * @param cache the cache
	 * @return the td template updater from template description
	 */
	public static TemplateUpdaterForDescription getTdTemplateUpdaterFromTemplateDescription(TemplateDescription sdescr, TemplateService service, CacheServerExpressions cache){
		
		if(sdescr==null)
			return null;
		
		logger.info("Converting TemplateDescription...");
		TdTemplateDefinition tdTempDefinition = new TdTemplateDefinition();
		tdTempDefinition.setServerId(sdescr.getId());
		tdTempDefinition.setAgency(sdescr.getAgency());
		tdTempDefinition.setTemplateDescription(sdescr.getDescription());
		tdTempDefinition.setTemplateName(sdescr.getName());
		
		Template stm = sdescr.getTemplate();

		logger.info("Converting Template...");
		List<TdColumnDefinition> listTdColumnDef = null;
		TemplateUpdaterForDescription updaterDescription = null;
		List<TabularDataActionDescription> actions = null;
		if(stm!=null){

			//ON ERROR
			if(stm.getOnRowErrorAction()!=null)
				tdTempDefinition.setOnError(stm.getOnRowErrorAction().toString());
			
			//CATEGORY
			TemplateCategory scat = stm.getCategory();
			if(scat!=null){
				tdTempDefinition.setTemplateType(scat.name());
				List<ViolationDescription> constraint = getTemplateCategoryConstraint(scat);
				tdTempDefinition.setTemplateType(new TdTTemplateType(scat.name(), scat.name(), constraint));
			}
			
			//BASE COLUMNS
			List<TemplateColumn<?>> sCol = stm.getColumns(); 
			listTdColumnDef = getTdColumnDefinitionFromTemplateColumn(sCol, stm, service, cache);
			printColumnName(listTdColumnDef);
			
			//ACTIONS
			actions = ConverterToTdTemplateModel.getActionDescriptionsToTabularDataActions(stm.getActions(), service);
		}
		
		if(listTdColumnDef!=null){
			TdTemplateUpdater tdUpdater = new TdTemplateUpdater(tdTempDefinition, listTdColumnDef);
			tdUpdater.setTabularDataActionDescription(actions);
			updaterDescription = new TemplateUpdaterForDescription(tdUpdater, cache);
			logger.info("Returning TemplateUpdaterForDescription "+updaterDescription);
		}
		return updaterDescription;
	}
	
	//TODO DEBUG
	private static void printColumnName(List<TdColumnDefinition> listTdColumnDef){
		int i = 0;
		for (TdColumnDefinition templateColumn : listTdColumnDef) {
			logger.info(i++ +") Column templateColumn " +templateColumn.getColumnName());
		}
	}

	
	/**
	 * Gets the action descriptions to tabular data actions.
	 *
	 * @param actions the actions
	 * @param service 
	 * @return the action descriptions to tabular data actions
	 */
	public static List<TabularDataActionDescription> getActionDescriptionsToTabularDataActions(List<TemplateAction<Long>> actions, TemplateService service) {
		
		if(actions==null){
			logger.warn("Actions are null, returning");
			return null;
		}

		logger.info("Converting "+actions.size() +" action/s...");
		
		List<TabularDataActionDescription> tdActionDescriptions = new ArrayList<TabularDataActionDescription>(actions.size());
	
		for (TemplateAction<Long> templateAction : actions) {
			TabularDataActionDescription tdDescr = null;
			StringBuilder builder = new StringBuilder();
			if(templateAction instanceof TimeAggregationAction){
				TimeAggregationAction timeAction = (TimeAggregationAction) templateAction;

				String description = "Time Aggregation"; 
				String columnLabel = timeAction.getColumn().getLabel()!=null?timeAction.getColumn().getLabel():"";
				if(!columnLabel.isEmpty())
					description+= " on "+columnLabel;
				builder.append(description+"<br/>");
				
				builder.append("for "+timeAction.getPeriodType().getName()+"<br/>");
				builder.append("Grouping column/s:<br/>");
				for (TemplateColumn<?> column : timeAction.getGroupColumns()) {
					builder.append("* "+column.getLabel()+"; <br/>");
				}
				builder.append("Aggregation function/s:<br/>");
				for (AggregationPair aggregate : timeAction.getAggregationPairs()) {
					builder.append("* "+aggregate.getColumn().getLabel()+" for "+aggregate.getFunction().name());
				}

				tdDescr = new TabularDataActionDescription(timeAction.getIdentifier()+"", TimeAggregationAction.class.getSimpleName(), builder.toString());
			}else if(templateAction instanceof AddColumnAction){
				AddColumnAction addAction = (AddColumnAction) templateAction;
				
				String description = "Add Column Operation"; 
				String columnLabel = addAction.getColumn().getLabel()!=null?addAction.getColumn().getLabel():"";
				if(!columnLabel.isEmpty())
					description+= " on "+columnLabel;
				builder.append(description+"<br/>");
				
				Expression initialValue = addAction.getInitialValue();
				String readeable = "";
				try{
					readeable = convertRuleExpression(initialValue).getReadableExpression();
				}catch(Exception e){
					ExpressionEvaluatorInstance evaluator = new ExpressionEvaluatorInstance(service);
					readeable = evaluator.evaluate(initialValue);
				}
				builder.append("with value: "+readeable);
				builder.append("<br/>");
				tdDescr = new TabularDataActionDescription(addAction.getIdentifier()+"", AddColumnAction.class.getSimpleName(), builder.toString());
			}else if(templateAction instanceof DeleteColumnAction){
				DeleteColumnAction deleteAction = (DeleteColumnAction) templateAction;
				builder.append("Delete Column Operation on id: "+deleteAction.getIdentifier()+"<br/>");
				tdDescr = new TabularDataActionDescription(deleteAction.getIdentifier()+"", DeleteColumnAction.class.getSimpleName(), builder.toString());
			}else if(templateAction instanceof NormalizeTableAction){
				NormalizeTableAction normalizeAction = (NormalizeTableAction) templateAction;
				String labels = "";
				for (TemplateColumn<?> col : normalizeAction.getCreatedColumns()) {
					labels+=col.getLabel()+"; ";
				}
				builder.append("Normalized Column/s: "+labels+"<br/>");
				tdDescr = new TabularDataActionDescription(normalizeAction.getIdentifier()+"", NormalizeColumnsAction.class.getSimpleName(), builder.toString());
			}else if(templateAction instanceof ValidateExpressionAction){
				ValidateExpressionAction validateExpressionAction = (ValidateExpressionAction) templateAction;
				builder.append("Table rule action: "+validateExpressionAction.getName());
				//TODO ADD EXPRESSION
//				ConverterToTdTemplateModel.convertRuleExpression(templateExpression.getClientExpression());
				tdDescr = new TabularDataActionDescription(validateExpressionAction.getIdentifier()+"", ValidateExpressionAction.class.getSimpleName(), builder.toString());
			}

			if(tdDescr!=null){
				tdActionDescriptions.add(tdDescr);
			}else
				logger.warn("TabularDataActionDescription not generated for: "+templateAction.toString());
		}
		
		return tdActionDescriptions;
	}
	
	/**
	 * Gets the aggregation function template.
	 *
	 * @param aggregationPairs the aggregation pairs
	 * @param service the service
	 * @param cache the cache
	 * @return the aggregation function template
	 * @throws Exception the exception
	 */
	/*
	public static List<TabularDataAction> convertActionsToTabularDataActions(List<TemplateAction<Long>> actions, TemplateService service, CacheServerExpressions cache) {
		
		if(actions==null)
			return null;

		List<TabularDataAction> tdActions = new ArrayList<TabularDataAction>(actions.size());
		
		for (TemplateAction<Long> templateAction : actions) {
			
			if(templateAction instanceof TimeAggregationAction){
				TimeAggregationAction timeAction = (TimeAggregationAction) templateAction;
				TimeAggregationColumnAction timeAggregationCA = new TimeAggregationColumnAction();
				timeAction.getGroupColumns();
				timeAction.getColumnId();
				timeAction.getPeriodType();
				
				//AGGREGATION FUNCTION
				List<AggregatePair> aggregateFunctionPairs = getAggregationFunctionTemplate(timeAction.getAggregationPairs(), service, cache);
				
				//TIME COLUMN
				TdColumnDefinition coumnDef = getTdColumnDefinitionFromTemplateColumn(-1, timeAction.getColumn(), service, cache);
				TdColumnData timeColumn = getTdColumnData(coumnDef, timeAction.getColumn().getId());
				
				List<TdColumnDefinition> groupColumnsDef = getTdColumnDefinitionFromTemplateColumn(timeAction.getGroupColumns(), service, cache);
				List<TdColumnDefinition> 
				
				for (TdColumnDefinition tdColumnDefinition : groupColumnsDef) {
					
				}
				
				TimeAggregationColumnAction taca = new TimeAggregationColumnAction(groupColumns, aggregateFunctionPairs, null, timeColumn, periodType)
				
				TimeAggregationAction timeAggregation = new TimeAggregationAction(timeColumns.get(0), periodType, groupColumns, listAggregationPair.toArray(ag));
			}
		}
		
		return tdActions;
	}*/



	/**
	 * 
	 * @param aggregationPairs
	 * @return
	 * @throws Exception 
	 */
	public static List<AggregatePair> getAggregationFunctionTemplate(List<AggregationPair> aggregationPairs, TemplateService service, CacheServerExpressions cache) throws Exception {
		
		logger.info("Getting AggregationFunctionTemplate...");
		
		if(aggregationPairs==null)
			throw new Exception("List<AggregatePair> is null");
		
		List<AggregatePair> listAggPair = new ArrayList<AggregatePair>(aggregationPairs.size());
		
		for (AggregationPair aggregationPair : aggregationPairs) {
			
			try{
				
				logger.info("Converting: "+aggregationPair.toString());
				
				//TODO INDEX!!!?????"
				TdColumnDefinition tdColumn = getTdColumnDefinitionFromTemplateColumn(-1, aggregationPair.getColumn(), service, cache);
				TdAggregateFunction aggregateFun = ConvertOperationForGwtModule.getAggegateFunction(aggregationPair.getFunction());

				AggregatePair agg = new AggregatePair();
				agg.setAggegrateFunction(aggregateFun);
				
				TdColumnData columnData = getTdColumnData(tdColumn, aggregationPair.getColumn().getId());
				agg.setColumnData(columnData);
				logger.info("Converted: "+agg.toString());
				listAggPair.add(agg);
			}catch (Exception e) {
				logger.error("Error on converting  "+aggregationPair+", skipping",e);
			}
		}
		
		return listAggPair;
	}
	
	/**
	 * Gets the td column data.
	 *
	 * @param tdColumn the td column
	 * @param serverId the server id
	 * @return the td column data
	 */
	public static TdColumnData getTdColumnData(TdColumnDefinition tdColumn, String serverId){
		
		ServerObjectId serId = new ServerObjectId(tdColumn.getIndex(), serverId, tdColumn.getColumnName(), ServerObjectType.TEMPLATE);
		return new TdColumnData(serId, tdColumn.getIndex()+"", tdColumn.getColumnName(), tdColumn.getColumnName(), tdColumn.getDataType().getName(), tdColumn.isBaseColumn());
	}

	/**
	 * Gets the td column definition from template column.
	 *
	 * @param sCol the s col
	 * @param template the template
	 * @param service the service
	 * @param cache the cache
	 * @return the td column definition from template column
	 */
	public static List<TdColumnDefinition> getTdColumnDefinitionFromTemplateColumn(List<TemplateColumn<?>> sCol, Template template, TemplateService service, CacheServerExpressions cache){
		
		List<TdColumnDefinition> listTdColumnDef = null;
		
		if(sCol!=null){
			listTdColumnDef = new ArrayList<TdColumnDefinition>(sCol.size());
			logger.info("Found column size: "+sCol.size());
			for (int i=0; i<sCol.size(); i++) {
				
				TemplateColumn<?> templateColumn = sCol.get(i);
				TdColumnDefinition tdColumn = getTdColumnDefinitionFromTemplateColumn(i, templateColumn, service, cache);
				boolean isBase = true;
				try {
					isBase = isBaseTemplateColumn(template, templateColumn);
					logger.info("TemplateColumn "+templateColumn.getLabel() +" is base column: "+isBase);
				} catch (Exception e) {
					logger.info("Skipping is base column,return");
				}
				tdColumn.setIsBaseColumn(isBase);
				
				listTdColumnDef.add(tdColumn);
			}
		}
		
		return listTdColumnDef;
	}
	
	/**
	 * Checks if is base template column.
	 *
	 * @param template the template
	 * @param column the column
	 * @return true, if is base template column
	 * @throws Exception the exception
	 */
	public static boolean isBaseTemplateColumn(Template template, TemplateColumn<?> column) throws Exception{
		try {
			if(template==null)
				throw new Exception("Template is null");
			
			List<TemplateColumn<?>> columns = template.getColumns(); //ARE BASE COLUMNS
			return columns.contains(column);
		} catch (Exception e) {
			throw new Exception("Sorry, an error occurred on getting is base template column",e);
		}
	}
	
	/**
	 * Gets the td column definition from template column.
	 *
	 * @param index of the column
	 * @param templateColumn the column
	 * @param service the service
	 * @param cache the cache
	 * @return the td column definition from template column
	 */
	public static TdColumnDefinition getTdColumnDefinitionFromTemplateColumn(int index, TemplateColumn<?> templateColumn, TemplateService service, CacheServerExpressions cache){
		
		//ColumnCategory conversion
		ColumnCategory sColCat = templateColumn.getColumnType(); //SERVER 
		TdTColumnCategory tdtCC = new TdTColumnCategory(sColCat.name(), sColCat.name()); //WIDGET
		
		TdTDataType tdDataType = null;
		//Value type conversion
		Class<? extends DataType> sValueType = templateColumn.getValueType(); //SERVER

		try {
			tdDataType = getTdTDataTypeFromDataType(sValueType); //WIDGET
		}catch (Exception e) {
			logger.error("Errror on converting data type, skypping value",e);
		}
		
		//EXPRESSION TODO??
		List<Expression> sExpression = templateColumn.getExpressions();
		List<TemplateExpression> listExpressionExtend = null;
		
		if(sExpression!=null && sExpression.size()>0){
			listExpressionExtend = new ArrayList<TemplateExpression>(sExpression.size());
			
//			ExpressionEvaluatorInstance evaluator = new ExpressionEvaluatorInstance(service);
			C_ExpressionParser parser = new C_ExpressionParser();
			for (Expression expression : sExpression) {
				
				if(expression!=null){
					logger.info("Found expression : "+expression.toString());
					TemplateExpression templateExpression = null;
					try{	
						templateExpression = new TemplateExpression();
						templateExpression.setServerExpression(expression.toString());
						C_Expression c_Expression = parser.parse(expression);
						templateExpression.setHumanDescription(c_Expression.getReadableExpression());
						templateExpression.setClientExpression(c_Expression);
						listExpressionExtend.add(templateExpression);
						logger.info("Updating expression cached: "+expression.toString());
						cache.addExpression(expression);
					}catch (Exception e) {
						logger.error("Expression evaluation exception: ",e);
					}
				}
//				System.out.println(j +") " +expression.toString());
			}
		}

		TdColumnDefinition tdColumn = new TdColumnDefinition(index, templateColumn.getId(), templateColumn.getLabel(), tdtCC, tdDataType, SPECIAL_CATEGORY_TYPE.UNKNOWN);
		tdColumn = settingSpecificReference(templateColumn, tdColumn, service);
		
		if(listExpressionExtend!=null){
			logger.info("Adding expression description, expression size: "+listExpressionExtend.size());
			tdColumn.setRulesExtends(listExpressionExtend);
		}
		
		return tdColumn;
	}
	
	/**
	 * Setting specific reference.
	 *
	 * @param templateColumn the template column
	 * @param tdColumn the td column
	 * @param service the service
	 * @return the td column definition
	 */
	public static TdColumnDefinition settingSpecificReference(TemplateColumn<?> templateColumn, TdColumnDefinition tdColumn, TemplateService service){
		
		if(templateColumn==null || templateColumn.getReference()==null)
			return tdColumn;

		Object reference = templateColumn.getReference();
		
		//USED IN CODELIST
		if(reference instanceof LocaleReference){
			logger.info("Found column reference as LocaleReference..");
			LocaleReference locale = (LocaleReference) reference;
			if(locale!=null){
				logger.info("Setting locale as "+locale.getLocale());
				tdColumn.setLocale(locale.getLocale());
//				tdColumn.setSpecialCategoryType(SPECIAL_CATEGORY_TYPE.CODENAME);
			}
		}else if(reference instanceof TimeDimensionReference){
			logger.info("Found column reference as TimeDimensionReference..");
			TimeDimensionReference timeDimension = (TimeDimensionReference) reference;
			if(timeDimension!=null && timeDimension.getPeriod()!=null){

				Map<String, String> timeValues = new HashMap<String, String>();
				
				if(timeDimension.getFormatIdentifier()!=null){
					ValueFormat valueFormat = timeDimension.getPeriod().getTimeFormatById(timeDimension.getFormatIdentifier());
					if(valueFormat!=null)
						timeValues.put(valueFormat.getId(), valueFormat.getExample());
				}
				
				TdTTimePeriod tt = new TdTTimePeriod(timeDimension.getPeriod().name(), timeValues);
				logger.info("Setting TdTTimePeriod as "+tt);
				tdColumn.setTimePeriod(tt);
//				tdColumn.setSpecialCategoryType(SPECIAL_CATEGORY_TYPE.TIMEDIMENSION);
			}
		}else if(reference instanceof FormatReference){
			
			FormatReference formatReference = (FormatReference) reference;
			ValueFormat valueFormat = DataTypeFormats.getFormatPerId(templateColumn.getValueType(), formatReference.getFormatIdentifier());
			tdColumn.getDataType().setFormatReference(new TdTFormatReference(valueFormat.getId(), valueFormat.getExample()));
			
		}else if(reference instanceof DimensionReference){
			logger.info("Found column reference as DimensionReference..");
			DimensionReference dimension = (DimensionReference) reference;
			if(dimension!=null){
				
				String columnId = dimension.getColumnId()!=null?dimension.getColumnId().getValue():null;
				Long tableId = dimension.getTableId()!=null?dimension.getTableId().getValue():null;
				if(columnId!=null && tableId!=null){

					/**
					 * is a codelist so TabResourceType.STANDARD
					 */
					TRId trId = new TRId("", TabResourceType.STANDARD, tableId+"");
					logger.info("Get column for Dimension with TRid "+trId +" and column id: "+columnId);
					ColumnData tempColData = new ColumnData();
					tempColData.setColumnId(columnId);
					tempColData.setTrId(trId);
				
					logger.info("Getting table by table id: "+tableId);
					try{
						Table table = service.getTable(new TableId(tableId));
						if (table.contains(TableDescriptorMetadata.class)){
							TableDescriptorMetadata tdm = table.getMetadata(TableDescriptorMetadata.class);
							if(tdm!=null){
								logger.info("Table name found: "+tdm.getName());
								tempColData.setName(tdm.getName());
							}else
								logger.info("TableDescriptorMetadata for table "+table+" not found");
						}else
							logger.info("Table with id "+tableId+" doesn't contains TableDescriptorMetadata");
						
					}catch (Exception e) {
						logger.warn("Error occurred on recovering table id: "+tableId, e);
					}

					logger.info("Setting Column Data Reference as "+tempColData);
					tdColumn.setColumnDataReference(tempColData);
//					tdColumn.setSpecialCategoryType(SPECIAL_CATEGORY_TYPE.DIMENSION);
				}
			}
		}
		
		return tdColumn;
	}
	
	/**
	 * Gets the allowed locales.
	 *
	 * @return the allowed locales
	 */
	public static List<String> getAllowedLocales(){
		return new ArrayList<String>(Arrays.asList(Locales.ALLOWED_LOCALES));
	}

	/**
	 * Gets the time dimension period types.
	 *
	 * @return the time dimension period types
	 */
	public static List<TdTTimePeriod> getTimeDimensionPeriodTypes() {
		
		List<TdTTimePeriod> listTimePeriods = new ArrayList<TdTTimePeriod>(PeriodType.values().length);
		for (PeriodType period : PeriodType.values()){
			
			Map<String, String> valuesFormat = new HashMap<String, String>(period.getAcceptedFormats().size());
			for (ValueFormat vf : period.getAcceptedFormats()) {
				valuesFormat.put(vf.getId(), vf.getExample());
			}
			listTimePeriods.add(new TdTTimePeriod(period.name(),valuesFormat));
		}
		return listTimePeriods;
	}
	
	/**
	 * Gets the licences.
	 *
	 * @return the licences
	 */
	public static List<TdLicenceModel> getLicences(){
		
		List<TdLicenceModel> licences = new ArrayList<TdLicenceModel>(Licence.values().length);
		for (Licence licence : Licence.values()) {
			licences.add(new TdLicenceModel(licence.toString(), licence.getName()));	
		}
		
		return licences;
	}
	
	/**
	 * Gets the duplicate behaviours.
	 *
	 * @return the duplicate behaviours
	 */
	public static List<TdBehaviourModel> getDuplicateBehaviours(){

		List<TdBehaviourModel> behs = new ArrayList<TdBehaviourModel>(DuplicateBehaviour.values().length);
		for (DuplicateBehaviour behav : DuplicateBehaviour.values()) {
			behs.add(convertDuplicateBehaviour(behav));	
		}
		
		return behs;
	}
	
	/**
	 * Convert duplicate behaviour.
	 *
	 * @param bhv the bhv
	 * @return the td behaviour model
	 */
	public static TdBehaviourModel convertDuplicateBehaviour(DuplicateBehaviour bhv){

		if(bhv!=null)
			return new TdBehaviourModel(bhv.toString(), bhv.getDescription());
		
		return null;
	}
	
	/**
	 * Convert flow.
	 *
	 * @param listMetadata the list metadata
	 * @return the td flow model
	 */
	public static TdFlowModel convertFlow(Collection<TabularResourceMetadata<?>> listMetadata){
		
		TdFlowModel flowModel = new TdFlowModel();
		
		for (TabularResourceMetadata<?> trMetadata : listMetadata) {
			if(trMetadata instanceof NameMetadata){
				flowModel.setName((String) trMetadata.getValue());
			}else if(trMetadata instanceof AgencyMetadata){
				flowModel.setAgency((String) trMetadata.getValue());
			}else if(trMetadata instanceof DescriptionMetadata){
				flowModel.setDescription((String) trMetadata.getValue());
			}else if(trMetadata instanceof RightsMetadata){
				flowModel.setRights((String) trMetadata.getValue());
			}else if(trMetadata instanceof ValidSinceMetadata){
				Calendar calendar = (Calendar) trMetadata.getValue();
				flowModel.setFromDate(calendar.getTime());
			}else if(trMetadata instanceof ValidUntilMetadata){
				Calendar calendar = (Calendar) trMetadata.getValue();
				flowModel.setToDate(calendar.getTime());
			}else if(trMetadata instanceof LicenceMetadata){
				Licence licence = (Licence) trMetadata.getValue();
				flowModel.setLicenceId(licence.getName());
			}
				
		}
		return flowModel;
	}

	/**
	 * Convert rule expression.
	 *
	 * @param expr the expr
	 * @return the c_ expression
	 * @throws Exception the exception
	 */
	public static C_Expression convertRuleExpression(Expression expr) throws Exception{
		C_ExpressionParser parser = new C_ExpressionParser();
		try{
			logger.info("Converting Expression expr.."+expr);
			return parser.parse(expr);
			
		}catch (Exception e) {
			throw new Exception("C_ExpressionParser generated an error: ", e);
		}
		
	}
}
