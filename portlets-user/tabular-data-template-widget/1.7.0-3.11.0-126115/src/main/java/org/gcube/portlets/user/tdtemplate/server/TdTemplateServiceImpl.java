package org.gcube.portlets.user.tdtemplate.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.TemplateCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.finals.AddToFlowAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.finals.DuplicateBehaviour;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.AddColumnAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.DeleteColumnAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.impl.ValidateExpressionAction;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.FormatReference;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.OnRowErrorAction;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TemplateDescription;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.metadata.TabularResourceMetadata;
import org.gcube.data.analysis.tabulardata.service.template.TemplateId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService;
import org.gcube.portlets.user.tdtemplate.server.converter.ConverterToTdTemplateModel;
import org.gcube.portlets.user.tdtemplate.server.converter.ConverterToTemplateServiceModel;
import org.gcube.portlets.user.tdtemplate.server.service.TemplateService;
import org.gcube.portlets.user.tdtemplate.server.session.CacheServerExpressions;
import org.gcube.portlets.user.tdtemplate.server.session.SessionUtil;
import org.gcube.portlets.user.tdtemplate.server.validator.TemplateCategoryValidator;
import org.gcube.portlets.user.tdtemplate.server.validator.TemplateValidator;
import org.gcube.portlets.user.tdtemplate.server.validator.service.ColumnCategoryTemplateValidator;
import org.gcube.portlets.user.tdtemplate.shared.ClientReportTemplateSaved;
import org.gcube.portlets.user.tdtemplate.shared.TdBehaviourModel;
import org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition;
import org.gcube.portlets.user.tdtemplate.shared.TdFlowModel;
import org.gcube.portlets.user.tdtemplate.shared.TdLicenceModel;
import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;
import org.gcube.portlets.user.tdtemplate.shared.TdTTemplateType;
import org.gcube.portlets.user.tdtemplate.shared.TdTTimePeriod;
import org.gcube.portlets.user.tdtemplate.shared.TdTemplateDefinition;
import org.gcube.portlets.user.tdtemplate.shared.TdTemplateUpdater;
import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;
import org.gcube.portlets.user.tdtemplate.shared.validator.ViolationDescription;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataAction;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataActionDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 30, 2015
 */
@SuppressWarnings("serial")
public class TdTemplateServiceImpl extends RemoteServiceServlet implements TdTemplateService {

	public static Logger logger = LoggerFactory.getLogger(TdTemplateServiceImpl.class);
	
//	public TdTemplateGxtObjectBuilder builder = new TdTemplateGxtObjectBuilder();
	/**
 * Gets the ASL session.
 *
 * @return the ASL session
 */
	protected ASLSession getASLSession() {
		return SessionUtil.getAslSession(this.getThreadLocalRequest().getSession());
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#getColumnCategoryByTdTemplateDefinition(org.gcube.portlets.user.tdtemplate.shared.TdTemplateDefinition)
	 */
	@Override
	public List<TdTColumnCategory> getColumnCategoryByTdTemplateDefinition(TdTemplateDefinition templateDefinition, boolean isValidTemplate) throws Exception{
		logger.info("getColumnCategoryByTdTemplateDefinition...");
		try{
			
			TemplateCategory tmc = ConverterToTemplateServiceModel.templateCategoryFromTemplateName(templateDefinition.getTemplateType());
			
			if(isValidTemplate){
				logger.info("Putting TdTemplateDefinition "+templateDefinition+" in ASL session!");
				SessionUtil.setTemplateDefinition(getASLSession(), templateDefinition);
			}
			
			return ConverterToTdTemplateModel.getTdTColumnCategoryFromTemplateCategory(tmc);
		}catch (Exception e) {
			
			throw new Exception("Sorry an error occurred contacting the service, Try again later");
		}
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#getTemplateTypes()
	 */
	@Override
	public List<TdTTemplateType> getTemplateTypes(){
		return ConverterToTdTemplateModel.getTdTTemplateTypeFromTemplateCategoryValues();
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#getOnErrorValues()
	 */
	@Override
	public List<String> getOnErrorValues(){
		return ConverterToTdTemplateModel.getOnErrorValues();
	}
	
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#getTimeDimensionPeriodTypes()
	 */
	@Override
	public List<TdTTimePeriod> getTimeDimensionPeriodTypes(){
		return ConverterToTdTemplateModel.getTimeDimensionPeriodTypes();
	}
	
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#getConstraintForTemplateType(org.gcube.portlets.user.tdtemplate.shared.TdTTemplateType)
	 */
	@Override
	public String getConstraintForTemplateType(TdTTemplateType type){
		
		return "";
	}
	
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#submitTemplate(java.util.List, org.gcube.portlets.user.tdtemplate.shared.TdFlowModel, boolean, java.util.List)
	 */
	@Override
	public ClientReportTemplateSaved submitTemplate(List<TdColumnDefinition> listColumns, TdFlowModel flowAttached, boolean save, List<TabularDataAction> actions) throws Exception{
		
		try{
				logger.info("SubmitTemplate starting, save: "+save);
				ServerReportTemplateSaved serverReport = generateServerReportTemplate(listColumns, flowAttached);
				
				/**
				 * There are errors?
				 */
				if(serverReport.getClientReport().isError()){
					logger.info("Returning client report due to errors");
					return serverReport.getClientReport();
				}
				
				Template template = serverReport.getTemplate();
				
				if(actions.size()>0){
					logger.info("Actions found! Trying to add actions to template...");
					for (TabularDataAction tabularDataAction : actions) {
						template = ConverterToTemplateServiceModel.addActionToTemplate(template, tabularDataAction);
					}
					logger.info("Added Actions!");
				}
				
				if(save){
					logger.info("Trying to save template on service...");
					ASLSession session = getASLSession();
					TdTemplateDefinition templateDef = SessionUtil.getTemplateDefinition(session);
					String templateDescription = templateDef.getTemplateDescription() == null?"":templateDef.getTemplateDescription();
					String agency = templateDef.getAgency() == null?"":templateDef.getAgency(); 
					logger.info("Instancing Template service..");
					TemplateService service = new TemplateService(session.getScope(), session.getUsername());
					logger.info("Saving template on service...");
					TemplateId templateId = service.saveTemplate(templateDef.getTemplateName(), templateDescription, agency, serverReport.getTemplate());
					logger.info("Template saved on server with id: "+templateId.getValue());
					invalidTemplateSession();
				}else{
					logger.info("No save on service...");
					setTemplateSession(template);
				}
				
				logger.info("ClientReportTemplateSaved contains errors? "+serverReport.getClientReport().isError());

			
				return serverReport.getClientReport();
			
		}catch (Exception e) {
			logger.error("Template creation error: ",e);
			throw new Exception("Sorry an error occurred during Template creation. ",e);
		}
	}
	
	/**
	 * Saves the template stored in (ASL) session.
	 *
	 * @param isUpdate the is update
	 * @throws Exception the exception
	 */
	@Override
	public void saveTemplate(boolean isUpdate) throws Exception{
		try{
			
			logger.info("Trying to save template on service..., isUpdate: "+isUpdate);
			ASLSession session = getASLSession();
			TdTemplateDefinition templateDef = SessionUtil.getTemplateDefinition(session);
			String templateDescription = templateDef.getTemplateDescription() == null?"":templateDef.getTemplateDescription();
			String agency = templateDef.getAgency() == null?"":templateDef.getAgency(); 
			logger.info("Instancing Template service..");
			TemplateService service = new TemplateService(session.getScope(), session.getUsername());
			
			Template template = getTemplateSession();
//			printColumnName();

			if(template==null)
				throw new Exception("Template session is null");
			
			// SET NEW ON ERROR ACTION
			OnRowErrorAction error = null;
			
			try{
				error = ConverterToTemplateServiceModel.onRowErrorAction(templateDef.getOnError());
				template.setOnErrorAction(error);
			}catch (Exception e) {
				logger.error("Conversion of Row Error action: "+templateDef.getOnError() +", generated an exception, skipping ",e);
			}
			
			logger.info("Instancing Template service..");
			logger.info("on error: "+template.getOnRowErrorAction().toString());	
			logger.info("Saving template on service..., isUpdate: "+isUpdate);
			TemplateId templateId = null;
			if(!isUpdate){
				templateId = service.saveTemplate(templateDef.getTemplateName(), templateDescription, agency, template);
				logger.info("Template saved on server with id: "+templateId.getValue());
			}
			else{
				Long serverId = templateDef.getServerId();
				logger.trace("Template for updating generated");
				
				if(serverId==null){
					throw new Exception("Template id not found");
				}
				service.updateTemplate(serverId, template);
				logger.info("Template updated on server with id: "+serverId);
			}
			
			invalidTemplateSession();
		}catch (Exception e) {
			logger.error("Template saving error: ",e);
			throw new Exception("Sorry an error occurred saving the Template. " + e.getMessage());
		}
	}
	


	/**
	 * Save template as.
	 * Saves the template stored in (ASL) session as newTemplateName
	 * @param isUpdate the is update
	 * @param newTemplateName the new template name
	 * @throws Exception the exception
	 */
	@Override
	public void saveTemplateAs(String newTemplateName) throws Exception{
		try{
			logger.info("Trying to save template as "+newTemplateName+" on service...");
			ASLSession session = getASLSession();
			TdTemplateDefinition templateDef = SessionUtil.getTemplateDefinition(session);
			String templateDescription = templateDef.getTemplateDescription() == null?"":templateDef.getTemplateDescription();
			String agency = templateDef.getAgency() == null?"":templateDef.getAgency(); 
			logger.info("Instancing Template service..");
			TemplateService service = new TemplateService(session.getScope(), session.getUsername());
			
			Template template = getTemplateSession();
//			printColumnName();

			if(template==null)
				throw new Exception("Template session is null");
			
			// SET NEW ON ERROR ACTION
			OnRowErrorAction error = null;
			
			try{
				error = ConverterToTemplateServiceModel.onRowErrorAction(templateDef.getOnError());
				template.setOnErrorAction(error);
			}catch (Exception e) {
				logger.error("Conversion of Row Error action: "+templateDef.getOnError() +", generated an exception, skipping ",e);
			}
			
			logger.info("on error: "+template.getOnRowErrorAction().toString());	
			logger.info("Saving template on service with name: "+newTemplateName);
			TemplateId templateId = null;
			templateId = service.saveTemplate(newTemplateName, templateDescription, agency, template);
			logger.info("Template saved on server with id: "+templateId.getValue() +" and name: "+newTemplateName);
			invalidTemplateSession();
		}catch (Exception e) {
			logger.error("Template saving error: ",e);
			throw new Exception("Sorry an error occurred saving the Template. " + e.getMessage());
		}
	}
	
	
	//TODO DEBUG
	/**
	 * Prints the column name.
	 */
	private void printColumnName(){
		Template template = getTemplateSession();
		
		List<TemplateColumn<?>> cols = template.getActualStructure();
		int i = 0;
		for (TemplateColumn<?> templateColumn : cols) {
			i++;
			logger.info(i+") Column templateColumn " +templateColumn.getId() + " label: "+templateColumn.getLabel());
		}
	}

	
	/**
	 * Invalid template session.
	 */
	public void invalidTemplateSession() {
		logger.info("Invalidating Template Session...");
		ASLSession session = getASLSession();
		SessionUtil.setTemplate(session, null);
		logger.info("Template Session is null!");
	}
	
	/**
	 * Sets the template session.
	 *
	 * @param template the new template session
	 */
	public void setTemplateSession(Template template) {
		logger.info("Setting new Template Session...");
		ASLSession session = getASLSession();
		SessionUtil.setTemplate(session, template);
		logger.info("Template Session saved in ASL!");
	}
	
	/**
	 * Gets the template session.
	 *
	 * @return the template session
	 */
	public Template getTemplateSession() {
		logger.info("Getting Template Session...");
		ASLSession session = getASLSession();
		return SessionUtil.getTemplate(session);
	}

	/**
	 * Generates {@link ServerReportTemplateSaved} The server report template.
	 * 
	 * This class convert client model template in server model template and generates a report {@link ServerReportTemplateSaved}
	 *
	 * @param listColumns the list columns
	 * @param flowAttached the flow attached
	 * @return the server report template saved
	 * @throws Exception the exception
	 */
	protected ServerReportTemplateSaved generateServerReportTemplate(List<TdColumnDefinition> listColumns, TdFlowModel flowAttached) throws Exception{
		
		try{
			
			ServerReportTemplateSaved serverReport = getServerColumns(listColumns);
			
			List<TemplateColumn<? extends DataType>> columns = serverReport.getListConvertedColumn();
			
			logger.info("Converted Server Column are: "+columns.size());
			
			if(columns.size()>0){
				ASLSession session = getASLSession();
				TdTemplateDefinition templateDef = SessionUtil.getTemplateDefinition(session);
				
				logger.info("Retrieve template defitnition from ASL session: "+templateDef);
				
				TemplateCategory tmc = ConverterToTemplateServiceModel.templateCategoryFromTemplateName(templateDef.getTemplateType());
				logger.info("Converted in template category: "+tmc);
				
				// ON ROW ERROR ACTION
				OnRowErrorAction error = null;
				
				try{
					error = ConverterToTemplateServiceModel.onRowErrorAction(templateDef.getOnError());
				}catch (Exception e) {
					logger.error("Conversion of Row Error action: "+templateDef.getOnError() +", generated an exception, skipping ",e);
				}
				
				logger.info("Instancing Template service..");
				TemplateService service = new TemplateService(session.getScope(), session.getUsername());
				

				AddToFlowAction addToFlowAction = null;
				
				if(flowAttached!=null){
					logger.info("Converting flow..."+flowAttached);
					List<TabularResourceMetadata<?>> metadata = ConverterToTemplateServiceModel.convertFlow(flowAttached);
					TabularResource resource = service.createFlow(metadata);

					if(resource!=null){
						org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource remoteResource = resource.getRemoteTabularResource();
						if(remoteResource!=null && flowAttached.getBehaviourId()!=null){
							try{
								DuplicateBehaviour duplicateBehaviour = ConverterToTemplateServiceModel.convertDuplicateBehaviour(flowAttached.getBehaviourId());
								addToFlowAction = ConverterToTemplateServiceModel.addToFlowAction(remoteResource, duplicateBehaviour);
								logger.info("AddToFlowAction created!");
							}catch(Exception e){
								logger.error(e.getMessage(),e);
							}
						}else{
							logger.warn("Remote resource flow or behaviour null, skipping AddToFlowAction");
						}
					}else
						logger.warn("Tabular resource flow is null, skipping AddToFlowAction");
				}
				
				
				Template template = TemplateService.generateTemplate(tmc, columns,error,addToFlowAction);
				logger.info("Template generated!");
				serverReport.setTemplate(template);
				return serverReport;

			}else{
				logger.warn("Column size is 0, skipping template creation");
				throw new Exception("Sorry an error occurred in Template definition, Try again later");
			}
			
		}catch (Exception e) {
			logger.error("Template creation error: ",e);
			throw new Exception("Sorry an error occurred in Template creation, Try again later");
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#updateTemplate(java.util.List, boolean)
	 * 
	 */
	@Override
	public ClientReportTemplateSaved updateTemplate(List<TdColumnDefinition> listColumns, boolean save, List<TabularDataAction> actions, TdFlowModel flowAttached) throws Exception{
		
		try{
			logger.info("Updating Template.., save: "+save);
			ServerReportTemplateSaved serverReport = getServerColumns(listColumns);
			
			List<TemplateColumn<? extends DataType>> columns = serverReport.getListConvertedColumn();
			
			logger.info("Converted Server Column are: "+columns.size());
			
			if(columns.size()>0){
				ASLSession session = getASLSession();
				TdTemplateDefinition templateDef = SessionUtil.getTemplateDefinition(session);
				
				logger.info("Retrieve template defitnition from ASL session: "+templateDef);
				
				TemplateCategory tmc = ConverterToTemplateServiceModel.templateCategoryFromTemplateName(templateDef.getTemplateType());
				logger.info("Converted in template category: "+tmc);
				
				// ON ROW ERROR ACTION
				OnRowErrorAction error = null;
				
				try{
					error = ConverterToTemplateServiceModel.onRowErrorAction(templateDef.getOnError());
				}catch (Exception e) {
					logger.error("Conversion of Row Error action: "+templateDef.getOnError() +", generated an exception, skipping ", e);
				}
				
				//RE-SUBMIT FLOW
				TemplateService service = new TemplateService(session.getScope(), session.getUsername());
				AddToFlowAction flow = service.getFlowByTemplateId(templateDef.getServerId());
					
				Template template = TemplateService.generateTemplate(tmc, columns, error, flow);
				
				Long serverId = templateDef.getServerId();
				logger.trace("Template for updating generated");
				
				if(serverId==null){
					throw new Exception("Template id not found");
				}
				
				if(actions.size()>0){
					//TODO ADD ACTION???
				}
				
				if(save){
					logger.info("Updating template on service...");
//					TemplateService service = new TemplateService(session.getScope(),session.getUsername());
					service.updateTemplate(serverId, template);
				}else{
					logger.info("Save is false, skipping update template on service...");
					setTemplateSession(template);
				}
					
				logger.info("ClientReportTemplateSaved contains errors? "+serverReport.getClientReport().isError());

			}else{
				logger.warn("Column size is 0, skipping template creation");
				throw new Exception("Sorry an error occurred in Template definition, Try again later");
			}
			
			return serverReport.getClientReport();
			
		}catch (Exception e) {
			logger.error("Template updatating error: ",e);
			throw new Exception("Sorry an error occurred in Template creation, Try again later");
		}
	}
	
	
	/**
	 * Gets the server columns.
	 *
	 * @param listColumns the list columns
	 * @return the server columns
	 */
	protected ServerReportTemplateSaved getServerColumns(List<TdColumnDefinition> listColumns){
		
		List<TemplateColumn<? extends DataType>> columns = new ArrayList<TemplateColumn<? extends DataType>>();
		ClientReportTemplateSaved clientReport = new ClientReportTemplateSaved();
		
		for (int i=0; i<listColumns.size(); i++) {
			TdColumnDefinition tdColumnDefinition = listColumns.get(i);
			logger.info("Found TdColumnDefinition: "+tdColumnDefinition +", converting");
			
			try{
				ColumnCategory columnType = ConverterToTemplateServiceModel.tdTdTColumnCategoryToColumnCategory(tdColumnDefinition.getCategory());
				Class<? extends DataType> valueType = ConverterToTemplateServiceModel.tdTdTDataTypeToDataType(tdColumnDefinition.getDataType());

				TemplateColumn<? extends DataType> col = null;
				
				logger.info("SpecialCategoryType is: "+tdColumnDefinition.getSpecialCategoryType());
				logger.info("ColumnCategory as columnType is: "+columnType);
				logger.info("DataType as valueType is: "+valueType);
//				logger.info("Column Name (Label) is: "+tdColumnDefinition.getColumnName());
				
				
				switch (tdColumnDefinition.getSpecialCategoryType()) {
				case NONE:
					FormatReference reference = ConverterToTemplateServiceModel.tdTdTFormatRefereceToFormatReference(tdColumnDefinition.getDataType());
					col = TemplateService.createTemplateColumn(columnType, valueType, reference);
					logger.info("TemplateColumn converted: "+col);
					break;
					
				case CODE:
				case ANNOTATION:
				case CODEDESCRIPTION:
					col = TemplateService.createTemplateColumn(columnType, valueType, null);
					logger.info("TemplateColumn converted: "+col);
					break;

				case TIMEDIMENSION:
					TdTTimePeriod timePeriod = tdColumnDefinition.getTimePeriod();
					logger.info("Read timePeriod: "+timePeriod);
					PeriodType period = ConverterToTemplateServiceModel.periodNameToPeriodType(timePeriod.getName());
					logger.info("PeriodType is: "+period);
					
					Map<String, String> valuesFormat = timePeriod.getValueFormats(); 

					if(valuesFormat==null || valuesFormat.size()==0){
						logger.error("ValueFormat not found for PeriodType: "+period +" skypping column");
						break;
					}
					
					String formatIdentifier = null;
					for (String key : valuesFormat.keySet()) {
						formatIdentifier = key; 
						break; //MUST ONLY ONE
					}
					
					logger.info("formatIdentifier: "+formatIdentifier);

					
					//TODO ADD VALUE FORMAT???
					col = TemplateService.createTemplateColumnForTimeDimension(columnType, valueType, period, formatIdentifier);
					logger.info("TemplateColumn for TimeDimension converted: "+col);
					break;

				case DIMENSION:
					ColumnData cdata = tdColumnDefinition.getColumnDataReference();
					logger.info("Read cdata: "+cdata);
					col = TemplateService.createTemplateColumnForDimension(columnType, valueType, Integer.parseInt(cdata.getTrId().getTableId()), cdata.getColumnId());
					logger.info("TemplateColumn for Dimension converted: "+col);
					break;
					
				case CODENAME:
					String locale = tdColumnDefinition.getLocale();
					logger.info("Read locale: "+locale);
					col = TemplateService.createTemplateColumnForCodeName(valueType, locale);
					logger.info("TemplateColumn for CodeName converted: "+col);
					
				default:
					break;
				}
				
//				TemplateColumn<? extends DataType> col = TemplateService.createTemplateColumn(columnType, valueType);
//				logger.trace("TemplateColumn created: "+col);

				if(col!=null){
					String label = tdColumnDefinition.getColumnName();
					if(label==null || label.isEmpty())
						label = "Column "+(i+1);
					
					logger.info("Setting column label: "+label);
					col.setLabel(label);
					
					//ADDING EXPRESSION
					List<TemplateExpression> rules = tdColumnDefinition.getRulesExtends();
					logger.info("Converting rules expressions: "+rules);
					List<Expression> expressions = null;
					if(rules!=null && rules.size()>0){
						expressions = new ArrayList<Expression>(rules.size());
						
						CacheServerExpressions cachedExpressions = SessionUtil.getCacheExpression(getASLSession());
						for (TemplateExpression templateExpression : rules) {
							Expression expression = null;
							if(templateExpression.getClientExpression()!=null){
								logger.info("is client expression, now converting");
								expression = ConverterToTemplateServiceModel.convertRuleExpression(templateExpression.getClientExpression());
								expressions.add(expression);
							}else if(templateExpression.getServerExpression()!=null){
								logger.info("is server expression, now converting");
								expression = cachedExpressions.getExpression(templateExpression.getServerExpression());
								if(expression!=null){
									logger.info("server expression is cached, adding to column: "+expression);
									expressions.add(expression);
								}else
									logger.info("server expression is not cached, skipping");
							}	
						}
			
						for (Expression expression : expressions) {
							logger.info("Adding expression: "+expression);
							col.addExpression(expression);
						}
					}
					
					columns.add(col);
					clientReport.addValidColumn(tdColumnDefinition);
				}else{
					
					clientReport.setError(true);
					clientReport.addColumnError(tdColumnDefinition);
					logger.warn("Conversion of TdColumnDefinition: "+tdColumnDefinition +", has generate column null, skipping");
				}
				
			}catch (Exception e) {
				clientReport.setError(true);
				clientReport.addColumnError(tdColumnDefinition);
				logger.error("Exception on Conversion of TdColumnDefinition: "+tdColumnDefinition +", skipping", e);
			}
		}
		return new ServerReportTemplateSaved(clientReport, columns);
//		return columns;
		
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#resolveColumnForDimension(org.gcube.portlets.user.td.widgetcommonevent.shared.TRId)
	 */
	@Override
	/**
	 * NOT USED
	 */
	public List<ColumnData> resolveColumnForDimension(TRId trId) throws Exception{
		/*TDGWTServiceImpl tDGWTServiceImpl = new TDGWTServiceImpl();

		try {
			List<ColumnData> listColumnsData = tDGWTServiceImpl.getColumnsForDimension(trId);
			
			return listColumnsData;
		} catch (TDGWTServiceException e) {
			logger.error("ResolveColumnForDimension error",e);
			throw new Exception("Sorry an error occurred on resolving CodeList column, Try again later");
		}
		*/
		return null;
		
	}

	/**
	 * Gets the cache server expressions.
	 *
	 * @return the cache server expressions
	 */
	public CacheServerExpressions getCacheServerExpressions(){
		ASLSession session = getASLSession();
		CacheServerExpressions cache = SessionUtil.getCacheExpression(session);
		if(cache==null){
			logger.info("CacheExpression is null");
			logger.info("Created new cache expression");
			return new CacheServerExpressions();
		}
		logger.info("Returning CacheExpression from ASL");
		return cache;
		
		
	}
	
	/**
	 * Invalid cache server expressions.
	 */
	public void invalidCacheServerExpressions(){
		ASLSession session = getASLSession();
		logger.info("Invalidate old cache expressions");
		SessionUtil.setCacheExpression(session, null);
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#getTemplateUpdaterForTemplateId(long)
	 */
	@Override
	public TdTemplateUpdater getTemplateUpdaterForTemplateId(long templateId) throws Exception{
		logger.info("getTemplateUpdaterForTemplateId for id "+templateId);
		ASLSession session = getASLSession();
		TemplateService service = new TemplateService(session.getScope(), session.getUsername());
	
		try{
			TemplateDescription templateDescr = service.getTemplate(templateId);
			if(templateDescr==null)
				throw new Exception("Sorry, an error occurred recovering template with id "+templateId + " not exists");
		
			logger.info("Invalidate old cache expressions");
			invalidCacheServerExpressions();
			
//			if(cache==null){
//				logger.info("Cache server exception is null, creating");
//				cache = new CacheServerExpressions();
//			}
			CacheServerExpressions cache = getCacheServerExpressions();
	
			TemplateUpdaterForDescription updaterDescription = ConverterToTdTemplateModel.getTdTemplateUpdaterFromTemplateDescription(templateDescr,service, cache);

			logger.info("Cache server updating");
			SessionUtil.setCacheExpression(session, updaterDescription.getCache());
			setTemplateSession(templateDescr.getTemplate());
			
			return updaterDescription.getTdUpdater();
		
		}catch (Exception e) {
			logger.error("GetTemplateUpdaterForTemplateId error",e);
			throw new Exception("Sorry, an error occurred recovering template with id "+templateId);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#getAllowedLocales()
	 */
	@Override
	public List<String> getAllowedLocales(){
		return ConverterToTdTemplateModel.getAllowedLocales();
	}
	
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#isValidTemplate(java.util.List)
	 */
	@Override
	public boolean isValidTemplate(List<TdTColumnCategory> columns) throws Exception{
		logger.info("Validating template.., column size is: "+columns.size());
		
		ASLSession session = getASLSession();
		TdTemplateDefinition templateDef = SessionUtil.getTemplateDefinition(session);
		if(templateDef==null)
			throw new Exception("Sorry an error occurred on recovering template definition from session");
		
		TemplateCategoryValidator templateValidator = null;
		
		if(templateDef.getTemplateType().compareToIgnoreCase(TemplateCategory.CODELIST.toString())==0){
			 templateValidator = new ColumnCategoryTemplateValidator(TemplateCategory.CODELIST);
		}else if(templateDef.getTemplateType().compareToIgnoreCase(TemplateCategory.DATASET.toString())==0){
			templateValidator = new ColumnCategoryTemplateValidator(TemplateCategory.DATASET);
		}if(templateDef.getTemplateType().compareToIgnoreCase(TemplateCategory.GENERIC.toString())==0){
			templateValidator = new ColumnCategoryTemplateValidator(TemplateCategory.GENERIC);
//			logger.info("Current template is  TemplateCategory.DATASET, there are not constraints, skipping validation");
		}
		
		if(templateValidator!=null){
			TemplateValidator validator = new TemplateValidator(columns, templateValidator);
			validator.validate();
			SessionUtil.setConstraintsViolations(session, validator.getViolations());
			logger.info("Returning violations size: "+validator.getViolations().size());
			return (validator.getViolations().size()==0);
		}
		
		return true;
	
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#getTemplateConstraintsViolations()
	 */
	@Override
	public List<ViolationDescription> getTemplateConstraintsViolations(){
		
		ASLSession session = getASLSession();
		List<ViolationDescription> violations = SessionUtil.getConstraintsViolations(session);
		logger.info("Found List<ViolationDescription>: "+violations +", returning");
		return violations;
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#getTemplateHelper()
	 */
	@Override
	public String getTemplateHelper() {
		return new TemplateHelper().getBuilderHTML().toString();
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#getLicences()
	 */
	@Override
	public List<TdLicenceModel> getLicences() throws Exception {
		try {
			List<TdLicenceModel> licences = ConverterToTdTemplateModel.getLicences();
			logger.trace("Licences: " + licences.size());
			return licences;
		}catch (Exception e) {
			logger.error("Licence error",e);
			throw new Exception("Sorry, an error occurred in recovering licence");
		}

	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#getBehaviours()
	 */
	@Override
	public List<TdBehaviourModel> getBehaviours() throws Exception {
		try {
			List<TdBehaviourModel> behaviour = ConverterToTdTemplateModel.getDuplicateBehaviours();
			logger.trace("DuplicateBehaviour: " + behaviour.size());
			return behaviour;
		}catch (Exception e) {
			logger.error("Licence error",e);
			throw new Exception("Sorry, an error occurred in recovering licence");
		}

	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#getFlowByTemplateId(long)
	 */
	@Override
	public TdFlowModel getFlowByTemplateId(long templateId) throws Exception{
		logger.info("getFlowByTemplateId for id "+templateId);
		ASLSession session = getASLSession();
		TemplateService service = new TemplateService(session.getScope(), session.getUsername());
		
		try{
			
			AddToFlowAction flow = service.getFlowByTemplateId(templateId);
			
			if(flow!=null){
				logger.info("AddToFlowAction is not null for template id:" +templateId);
				Collection<TabularResourceMetadata<?>> listMetadata = service.getFlowMetadataByTemplateFlowAction(flow);
				
				TdBehaviourModel bhvModel = ConverterToTdTemplateModel.convertDuplicateBehaviour(flow.getDuplicatesBehaviuor());
			
				TdFlowModel flowModel = ConverterToTdTemplateModel.convertFlow(listMetadata);
				
				flowModel.setBehaviourId(bhvModel.getId());
				
				return flowModel;
			}
			logger.info("AddToFlowAction is null for template id: " +templateId +", returning null");
			return null;
		}catch (Exception e) {
			logger.error("getFlowByTemplateId error",e);
			throw new Exception("Sorry, an error occurred in recovering flow for template with id "+templateId);
		}
	}
	
	/**
	 * Execute tabular data action.
	 *
	 * @param action the action
	 * @return the list ActualStructure. The new template structure.
	 * @throws Exception the exception
	 */
	@Override
	public List<TdColumnDefinition> executeTabularDataAction(TabularDataAction action) throws Exception{
		
		try {
			logger.info("Submitting template action...");
			
			if(action==null)
				throw new Exception("Sorry an error occurred when perfoming action to template, action not found");
			
			Template template = getTemplateSession();
			
			if(template==null)
				throw new Exception("Sorry an error occurred retrieving Template, close and try again");
			
			template = ConverterToTemplateServiceModel.addActionToTemplate(template, action);
			logger.trace("Added Action To Template!");
			
			ASLSession session = getASLSession();
			TemplateService service = new TemplateService(session.getScope(), session.getUsername());

			invalidCacheServerExpressions();
			CacheServerExpressions cache = getCacheServerExpressions();

			logger.trace("Converting  Actual Structure...");
			List<TdColumnDefinition> newColumns = ConverterToTdTemplateModel.getTdColumnDefinitionFromTemplateColumn(template.getActualStructure(), template, service, cache);
			setTemplateSession(template);
			return newColumns;
		} catch (Exception e) {
			logger.error("Sorry an error occurred when perfoming add action to Template", e);
			throw new Exception("Sorry an error occurred when perfoming add action to Template", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#removeLastAction()
	 */
	@Override
	public List<TdColumnDefinition> removeLastAction() throws Exception{
		try{
			logger.info("RemoveLastAction template action...");
			Template template = getTemplateSession();
			template.removeLastAction();
			setTemplateSession(template);
			List<TdColumnDefinition> newColumns = reloadColumns();
			return newColumns;
		}catch(Exception e){
			logger.error("Sorry an error occurred when perfoming 'remove last action', try again later", e);
			throw new Exception("Sorry an error occurred when perfoming 'remove last action', try again later", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#getAppliedActionsOnTemplate()
	 */
	@Override
	public List<TabularDataActionDescription> getAppliedActionsOnTemplate() throws Exception {
		
		try{
			Template template = getTemplateSession();
			if(template==null)
				throw new Exception("getAppliedActionsOnTemplate error: the template is null");
			
			ASLSession session = getASLSession();
			TemplateService service = new TemplateService(session.getScope(), session.getUsername());
			
			return ConverterToTdTemplateModel.getActionDescriptionsToTabularDataActions(template.getActions(), service);
			
		}catch(Exception e){
			logger.error("Error getAppliedActionsOnTemplate: ", e);
			throw new Exception("Sorry an error occurred retrieving the post-operations, refresh and try again later", e);
			
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#reloadColumns()
	 */
	@Override
	public List<TdColumnDefinition> reloadColumns() throws Exception {
		logger.trace("Reload Columns...");
		try{
			Template template = getTemplateSession();
			if(template==null)
				throw new Exception("reloadColumns error: the template is null");
			
			ASLSession session = getASLSession();
			TemplateService service = new TemplateService(session.getScope(), session.getUsername());

			invalidCacheServerExpressions();
			CacheServerExpressions cache = getCacheServerExpressions();
			logger.trace("Converting  Actual Structure...");
			List<TdColumnDefinition> newColumns = ConverterToTdTemplateModel.getTdColumnDefinitionFromTemplateColumn(template.getActualStructure(), template, service, cache);
			logger.trace("Actual Structure generated!");
			return newColumns;
		}catch(Exception e){
			logger.error("Error reloadColumns: ", e);
			throw new Exception("Sorry an error occurred retrieving the template columns, refresh and try again later", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#addColumnAction(org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition)
	 */
	@Override
	public List<TdColumnDefinition> addColumnAction(TdColumnDefinition column, TemplateExpression expression) throws Exception {
		
		try{
			logger.info("Add Column template action...");
			Template template = getTemplateSession();
			
			List<TdColumnDefinition> listColumns = new ArrayList<TdColumnDefinition>(1);
			listColumns.add(column);
			ServerReportTemplateSaved serverReport = getServerColumns(listColumns);
			List<TemplateColumn<? extends DataType>> columns = serverReport.getListConvertedColumn();
			
			Expression initialValue = null;
			if(expression!=null){
				initialValue = ConverterToTemplateServiceModel.convertRuleExpression(expression.getClientExpression());
				logger.info("Converted initial value: "+initialValue);
			}
			AddColumnAction addColumnAction = new AddColumnAction(columns.get(0), initialValue);
			logger.info("Column template action created, adding to template");
			template.addAction(addColumnAction);
			
			setTemplateSession(template);
			logger.info("Reloading columns..");
			List<TdColumnDefinition> newColumns = reloadColumns();
			return newColumns;
			
		}catch(Exception e){
			logger.error("Sorry an error occurred when perfoming 'add column action', try again later", e);
			throw new Exception("Sorry an error occurred when perfoming 'add column action', try again later", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#removeAllActions()
	 */
	@Override
	public boolean removeAllActions() throws Exception {
		try{
			logger.info("Remove All Actions...");
			Template template = getTemplateSession();
			int size = template.getActions().size();
			logger.info("Actions is/are: "+size);
			try{
				for (int i = 0; i < size; i++) {
					logger.info("Removing action: "+(size-i));
					template.removeLastAction();
				}
			}catch (Exception e) {
				logger.error("Sorry an error occurred when perfoming 'remove last action', try again later", e);
			}
			return true;
		}catch(Exception e){
			logger.error("Sorry an error occurred when perfoming 'remove last action', try again later", e);
			throw new Exception("Sorry an error occurred when perfoming 'remove last action', try again later", e);
		}
	}
	

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#tableRuleAction(org.gcube.portlets.user.tdtemplate.shared.TemplateExpression)
	 */
	@Override
	public List<TdColumnDefinition> tableRuleAction(TemplateExpression templateExpression) throws Exception {
		try{
			
			Template template = getTemplateSession();
			
			if(templateExpression==null)
				throw new Exception("Expression is null");
			
			logger.info("Performing table rule action..");
			//TODO ADD NAME
			String name = templateExpression.getClientExpression().getReadableExpression();
			Expression expression = ConverterToTemplateServiceModel.convertRuleExpression(templateExpression.getClientExpression());
			
			ValidateExpressionAction vea = new ValidateExpressionAction(name , expression);
			logger.info("ValidateExpressionAction created, adding to template");
			template.addAction(vea);
			
			setTemplateSession(template);
			logger.info("Reloading columns..");
			List<TdColumnDefinition> newColumns = reloadColumns();
			return newColumns;

		}catch(Exception e){
			logger.error("Exception tableRuleAction:", e);
			throw new Exception("Sorry an error occurred when perfoming 'Validate Expression Action', try again later", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#deleteColumnAction(org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition)
	 */
	@Override
	public List<TdColumnDefinition> deleteColumnAction(TdColumnDefinition column) throws Exception {
		try{
			
			Template template = getTemplateSession();
			
			if(column==null)
				throw new Exception("Column is null");
			
			if(column.getIndex()<0)
				throw new Exception("Invalid column index (< 0)");
			
			logger.info("Delete Column template action, column index: "+column.getIndex());
			TemplateColumn<?> templateColumn = template.getActualStructure().get(column.getIndex());
			
			DeleteColumnAction deleteColumnAction = new DeleteColumnAction(templateColumn);
			logger.info("Column template action created, adding to template");
			template.addAction(deleteColumnAction);
			
			setTemplateSession(template);
			logger.info("Reloading columns..");
			List<TdColumnDefinition> newColumns = reloadColumns();
			return newColumns;
			
		}catch(Exception e){
			logger.error("Exception deleteColumnAction", e);
			throw new Exception("Sorry an error occurred when perfoming 'delete column action', try again later", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplate.client.rpc.TdTemplateService#deleteColumnAction(org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition)
	 */
	@Override
	public boolean changeLabel(int columnIndex, String newLabel) throws Exception {
		try{
			
			Template template = getTemplateSession();
			
			if(template==null){
				logger.warn("Changing label before of template creation, skipping error, returning true");
				return true;
			}
			
			if(columnIndex<0)
				throw new Exception("Invalid column index");
			
			logger.info("Changing label column index: "+columnIndex);
			int size = template.getActualStructure().size();
			logger.info("Template size: "+size);
			
			if(columnIndex>=size){
				logger.info("Skipping server change label columnIndex: "+columnIndex +" > "+size);
				return true;
			}
			
			TemplateColumn<?> templateColumn = template.getActualStructure().get(columnIndex);
			logger.info("Old label: "+templateColumn.getLabel());
			templateColumn.setLabel(newLabel);
			logger.info("Updated label at: "+newLabel);
			
			setTemplateSession(template);
			return true;
			
		}catch(Exception e){
			logger.error("Sorry an error occurred when perfoming 'change label', try again later", e);
			throw new Exception("Sorry an error occurred when perfoming 'change label', try again later", e);
		}
	}
	/*
	public static void main(String[] args) throws Exception {
		
		String user = "francesco.mangiacrapa";
		String scope = "/gcube/devsec/devVRE"; //Development
		long templateID = 3;
		TemplateService service = new TemplateService(scope, user);
		
		try{
			
			AddToFlowAction flow = service.getFlowByTemplateId(templateID);
			
			Collection<TabularResourceMetadata<?>> listMetadata = service.getFlowMetadataByTemplateFlowAction(flow);
			
			TdBehaviourModel bhvModel = ConverterToTdTemplateModel.convertDuplicateBehaviour(flow.getDuplicatesBehaviuor());
		
			TdFlowModel flowModel = ConverterToTdTemplateModel.convertFlow(listMetadata);
			
			flowModel.setBehaviourId(bhvModel.getId());
			
			System.out.println(flowModel);
		
		}catch (Exception e) {
			logger.error("getFlowByTemplateId error",e);
			throw new Exception("Sorry, an error occurred on recovering flow for template with id "+templateID);
		}
		
	}*/


	
}
