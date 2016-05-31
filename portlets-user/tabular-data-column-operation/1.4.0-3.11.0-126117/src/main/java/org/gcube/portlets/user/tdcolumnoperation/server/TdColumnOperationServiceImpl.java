package org.gcube.portlets.user.tdcolumnoperation.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Addition;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Concat;
import org.gcube.data.analysis.tabulardata.expression.composite.text.Length;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByIndex;
import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringPosition;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.portlets.user.td.gwtservice.server.SessionUtil;
import org.gcube.portlets.user.td.gwtservice.server.TDGWTServiceImpl;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.MergeColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.column.SplitColumnSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.groupby.GroupBySession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.groupby.TimeAggregationSession;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdcolumnoperation.client.rpc.TdColumnOperationService;
import org.gcube.portlets.user.tdcolumnoperation.server.util.ConvertForGwtModule;
import org.gcube.portlets.user.tdcolumnoperation.server.util.ConvertForService;
import org.gcube.portlets.user.tdcolumnoperation.server.util.GroupByOperationIdentifier;
import org.gcube.portlets.user.tdcolumnoperation.shared.AggregationColumnSession;
import org.gcube.portlets.user.tdcolumnoperation.shared.OperationID;
import org.gcube.portlets.user.tdcolumnoperation.shared.OperationNotAvailable;
import org.gcube.portlets.user.tdcolumnoperation.shared.SplitAndMergeColumnSession;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdAggregateFunction;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdOperatorComboOperator;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdOperatorEnum;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdPeriodType;
import org.gcube.portlets.user.tdcolumnoperation.shared.operation.TdIndexValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TdColumnOperationServiceImpl extends RemoteServiceServlet implements TdColumnOperationService {

	public static Logger logger = LoggerFactory.getLogger(TdColumnOperationServiceImpl.class);
	

	@Override
	public List<TdOperatorComboOperator> loadOperatorForOperationId(OperationID operation) {
		
		logger.info("loadOperatorForOperationId : "+operation);
		List<TdOperatorComboOperator> listOperations = new ArrayList<TdOperatorComboOperator>();
		
		if (operation.equals(OperationID.SPLIT)) {
			listOperations.add(new TdOperatorComboOperator(
					Operator.SUBSTRING_BY_INDEX.toString(),
					TdOperatorEnum.CHAR_SEQUENCE, String.class.getName()));
			listOperations.add(new TdOperatorComboOperator(
					Operator.SUBSTRING_BY_REGEX.toString(),
					TdOperatorEnum.REGEX, String.class.getName()));
			listOperations.add(new TdOperatorComboOperator(
					Operator.SUBSTRING_BY_INDEX.toString(),
					TdOperatorEnum.INDEX, Integer.class.getName()));
//			listOperations.add(new TdOperatorComboDescription(Operator.TRIM.toString(),
//							"TRIM",
//							"Split as string with leading and trailing whitespace omitted"));
		} else if (operation.equals(OperationID.MERGE)) {
			listOperations.add(new TdOperatorComboOperator(Operator.CONCAT
					.toString(), TdOperatorEnum.MERGE,
					String.class.getName()));
		}
		logger.info("returning listOperations size : "+listOperations.size());
		return listOperations;
	}

	/**
	 * 
	 * @param operationColumnSession
	 * @return 
	 * @throws Exception
	 */
	@Override
	public String startSplitAndMergeOperation(SplitAndMergeColumnSession operationColumnSession) throws Exception{
		
		try {
			logger.trace("startSplitAndMergeOperation...");
			logger.info("SplitAndMergeColumnSession is: "+operationColumnSession);
			
			if(operationColumnSession==null)
				throw new Exception("Split and Merge Column Session is null");
			
			if(operationColumnSession.getFirstColumnData()==null)
				throw new Exception("Split and Merge Column Session, first column data is null");
			
			logger.info("startSplitAndMergeOperation read First Column Data: "+operationColumnSession.getFirstColumnData());
			
			TRId trID = operationColumnSession.getFirstColumnData().getTrId();
			logger.info("TRId is: "+trID);
			
			String tableId = trID.getTableId();
			logger.info("Table Id is: "+tableId);
			if(tableId==null || tableId.isEmpty()){
				throw new Exception("Table id is null or empty");
			}
			
		
			try{
				
				TdTabularDataService service = columnOperationService();
				Table table = ConvertForService.getTable(trID, service);
				
				ColumnReference firstRef = ConvertForService.getColumnReference(table, operationColumnSession.getFirstColumnData().getName());

				String label1 = operationColumnSession.getLabelColumn1();
				String label2 = operationColumnSession.getLabelColumn2();

				TDGWTServiceImpl gwtService = new TDGWTServiceImpl();
				
				String taskId=null;
				if(operationColumnSession.getOperatorID().equals(OperationID.SPLIT)){
					logger.trace("SPLIT invocation...");
					ArrayList<Expression> expressions = (ArrayList<Expression>) resolveSplitOperation(operationColumnSession, firstRef);
					logger.trace("Generated expressions to split: "+expressions);
					//CALL GWT SERVICE OPERATIONS
					
					label1 = (label1!=null && !label1.isEmpty())?label1:"Splitted Column 1";
					label2 = (label2!=null && !label2.isEmpty())?label2:"Splitted Column 2";
					
//					SplitColumnSession splitSession = new SplitColumnSession(operationColumnSession.getFirstColumnData(), label1, label2, expressions);
				
					SplitColumnSession splitSession = new SplitColumnSession(operationColumnSession.getFirstColumnData(), expressions, label1, operationColumnSession.getColumnType1(), operationColumnSession.getDataType1(), label2, operationColumnSession.getColumnType2(), operationColumnSession.getDataType2(),operationColumnSession.isDeleteSourceColumn());
					logger.trace("Created split session to TDGWTServiceImpl...");
					logger.trace("delete source column/s? "+operationColumnSession.isDeleteSourceColumn());
					logger.trace("Calling startSplitColumn...");
					
					taskId=gwtService.startSplitColumn(splitSession, this.getThreadLocalRequest().getSession());
					logger.trace("Operation return task id: "+taskId, " returning");
					
				}else if(operationColumnSession.getOperatorID().equals(OperationID.MERGE)){
					logger.trace("MERGE invocation...");
					ColumnData secondCD = operationColumnSession.getSecondColumnData();
					if(secondCD==null || secondCD.getName().isEmpty())
						throw new Exception("Second column reference is null or column name is empty");
					
					Column second = table.getColumnByName(secondCD.getName());
					ColumnReference secondRef = table.getColumnReference(second);
					Expression mergeExpression = resolveMergerOperation(operationColumnSession, firstRef, secondRef);
					logger.trace("Generated expressions to merge: "+mergeExpression);
					
					label1 = (label1!=null && !label1.isEmpty())?label1:"Merged Column";
					
//					MergeColumnSession mergeSession = new MergeColumnSession(operationColumnSession.getFirstColumnData(), label1, mergeExpression);
					MergeColumnSession mergeSession = new MergeColumnSession(operationColumnSession.getFirstColumnData(), operationColumnSession.getSecondColumnData(), label1, operationColumnSession.getColumnType1(), operationColumnSession.getDataType1(), mergeExpression, operationColumnSession.isDeleteSourceColumn());
					logger.trace("Created merge session to TDGWTServiceImpl...");
					logger.trace("delete source column/s? "+operationColumnSession.isDeleteSourceColumn());
					
					logger.trace("Calling startMergeColumn..");
					//returning task id
					taskId = gwtService.startMergeColumn(mergeSession,this.getThreadLocalRequest().getSession());
					logger.trace("Operation return task id: "+taskId, " returning");
				}
				
				return taskId;
			}catch (TDGWTSessionExpiredException e){
				logger.error("TDGWTSessionExpiredException, session expired", e);
				throw e;
			} catch (TDGWTServiceException e) {
				logger.error("TDGWTServiceException", e);
				throw e;
			} catch (SecurityException e) {
				logger.error("SecurityException", e);
				throw e;
			}catch (Exception e) {
				logger.error("Sorry, an error occurred on recovering the source column, try again later", e);
				throw new Exception("Sorry, an error occurred on recovering the source column, try again later");
			}

		}catch (Exception e) {
			throw new Exception("Sorry, an error occurred on executing split operation, try again later");
		}
		
	}
	
	/**
	 * 
	 * @param operationColumnSession
	 * @param sourceRef
	 * @return
	 * @throws Exception
	 */
	private List<Expression> resolveSplitOperation(SplitAndMergeColumnSession operationColumnSession, ColumnReference sourceRef) throws Exception{
		
		try{

			TdOperatorComboOperator tdOperator = operationColumnSession.getOperator();
			
			List<Expression> sequentialExpression = new ArrayList<Expression>();
			
			if(tdOperator==null)
				throw new Exception("TdOperator is null");
			
			TdOperatorEnum op = tdOperator.getOperator();
			if(op==null)
				throw new Exception("TdOperatorEnum is null");
			
			TdIndexValue start = null;
			TdIndexValue end = null;
			
			
			if(op.equals(TdOperatorEnum.REGEX)){  //SPLIT BY REGEX
				logger.info("Case REGEX");
				start = new TdIndexValue(TdIndexValue.INDEX_MIN_DEFAULT_VALUE);
			
				logger.trace("Start index value is "+start +" end index value is "+end);
				Expression exp1 = ConvertForService.convertExpressionByTdIndexValue(start, sourceRef);
				logger.trace("Converted expression 1 is :"+exp1);
				Expression expByRegex = ConvertForService.substringByRegex(sourceRef, operationColumnSession.getValue());
				
				Expression exp2 = new SubstringPosition(sourceRef, expByRegex);
				logger.trace("Converted expression 2: "+exp2);
				
				Expression firstExpression = new SubstringByIndex(sourceRef, exp1, exp2);
				
				logger.trace("First expression created is: " +firstExpression);
				sequentialExpression.add(firstExpression);

				Expression addLenghtRegex = new Addition(exp2, new Length(expByRegex));
				Expression expEnd = new SubstringByIndex(sourceRef, addLenghtRegex, new Length(sourceRef));
				
				logger.trace("Second expression created is: " +expEnd);
				sequentialExpression.add(expEnd);

			}else if(op.equals(TdOperatorEnum.CHAR_SEQUENCE)){ //SPLIT BY CHAR SEQUENCE
				logger.info("Case CHAR_SEQUENCE");
				
				start = new TdIndexValue(TdIndexValue.INDEX_MIN_DEFAULT_VALUE);
				end = new TdIndexValue(operationColumnSession.getValue());

				logger.trace("Start index value is "+start +" end index value is "+end);
				Expression exp1 = ConvertForService.convertExpressionByTdIndexValue(start, sourceRef);
//				logger.trace("Converted expression 1 is :"+exp1);
					
				Expression exp2 = ConvertForService.convertExpressionByTdIndexValue(end, sourceRef);
//				logger.trace("Converted expression 2 is :"+exp2);
				
				logger.info("First expression created is: " +exp1);
				logger.info("Second expression created is: " +exp2);
				sequentialExpression.add(new SubstringByIndex(sourceRef, exp1, exp2));
				sequentialExpression.add(new SubstringByIndex(sourceRef, exp2, new Addition(new Length(sourceRef), new TDInteger(1))));

			}else if(op.equals(TdOperatorEnum.INDEX)){ //SPLIT BY INDEX
				logger.info("Case INDEX");
				
				start = new TdIndexValue(TdIndexValue.INDEX_MIN_DEFAULT_VALUE);
				int index = Integer.parseInt(operationColumnSession.getValue());	
				end = new TdIndexValue(index);
				
				logger.trace("Start index value is "+start +" end index value is "+end);
				Expression exp1 = ConvertForService.convertExpressionByTdIndexValue(start, sourceRef);
//				logger.trace("Converted expression 1 is :"+exp1);
					
				Expression exp2 = ConvertForService.convertExpressionByTdIndexValue(end, sourceRef);
//				logger.trace("Converted expression 2 is :"+exp2);

				logger.info("First expression created is: " +exp1);
				logger.info("Second expression created is: " +exp2);
				sequentialExpression.add(new SubstringByIndex(sourceRef, exp1, exp2));
				sequentialExpression.add(new SubstringByIndex(sourceRef, exp2, new Length(sourceRef)));
			}
			
			return sequentialExpression;
			
		}catch (TDGWTSessionExpiredException e){
			logger.error("TDGWTSessionExpiredException, session expired", e);
			throw e;
		}catch (Exception e) {
			logger.error("Sorry an error occurred when resolving SPLIT operation", e);
			throw new Exception("Sorry an error occurred when resolving SPLIT operation");
		}
	}
	
	private Expression resolveMergerOperation(SplitAndMergeColumnSession operationColumnSession, ColumnReference firstRef, ColumnReference secondRef){
		logger.trace("Resolving Merge Operation: ");
		String value = operationColumnSession.getValue();
		logger.trace("value is: "+value);
		Expression merge=new Concat(firstRef, new Concat(new TDText(value), secondRef));
		
		logger.info("Merge expression is: "+merge);
		
		return merge;
	}
	
	@Override
	public List<TdAggregateFunction> getListAggregationFunctionIds(){
		
//		HttpSession session = this.getThreadLocalRequest().getSession();
//		getAslSession(session);
		
		return ConvertForGwtModule.getAggregationFunctionIds();
	}
	
	@Override
	public List<TdPeriodType> getListTimeTypes(){
		
//		HttpSession session = this.getThreadLocalRequest().getSession();
//		getAslSession(session);
		
		return ConvertForGwtModule.getTimeTypes();
	}
	
	@Override
	public String startGroupByOperation(AggregationColumnSession aggregationSession) throws Exception{
		logger.trace("Starting group by Operation: ");
		logger.trace("AggregationColumnSession: "+aggregationSession);
		
		if(aggregationSession==null)
			throw new Exception("Sorry an error occurred when perfoming group by operation, aggregation session not found");
		
		try{
			if(aggregationSession.getGroupColumns()!=null && !aggregationSession.getGroupColumns().isEmpty()){
				
				if(aggregationSession.getAggregateFunctionPairs()!=null && !aggregationSession.getAggregateFunctionPairs().isEmpty()){
					HashMap<String, Object> map = resolveGroupByOperation(aggregationSession, false);
					TDGWTServiceImpl gwtService = new TDGWTServiceImpl();
					String taskId = gwtService.startGroupBy(new GroupBySession(aggregationSession.getTrId(), map), this.getThreadLocalRequest().getSession());
					logger.info("Returning task id: "+taskId +" generated by GroupByOperation");
					return taskId;
				}else
					throw new Exception("Sorry an error occurred when perfoming group by operation, aggregation function not found");
				
			}else
				throw new Exception("Sorry an error occurred when perfoming group by operation, group column not found");
			
		}catch (TDGWTSessionExpiredException e){
			logger.error("TDGWTSessionExpiredException, session expired", e);
			throw e;
		} catch (TDGWTServiceException e) {
			logger.error("TDGWTServiceException", e);
			throw e;
		} catch (SecurityException e) {
			logger.error("SecurityException", e);
			throw e;
		}catch(Exception e){
			logger.error("Sorry an error occurred when perfoming group by operation", e);
			throw new Exception("Sorry an error occurred when perfoming group by operation", e);
		}
	}
	
	@Override
	public String startAggregateByTimeOperation(AggregationColumnSession aggregationSession, TdPeriodType period, List<ColumnData> timeDimensionsColumns) throws Exception{
		logger.trace("Starting AggregateByTime: ");
		logger.trace("AggregationColumnSession: "+aggregationSession);
		logger.trace("TdPeriodType: "+period);
		
		if(aggregationSession==null)
			throw new Exception("Sorry an error occurred when perfoming aggregate by time operation, aggregation session not found");
		
		if(period==null)
			throw new Exception("Sorry an error occurred when perfoming aggregate by time operation, selected period not found");
		
		if(timeDimensionsColumns==null || timeDimensionsColumns.size()==0)
			throw new Exception("Sorry an error occurred when perfoming aggregate by time operation, time column not found");
		
		try{
			if(aggregationSession.getGroupColumns()!=null && !aggregationSession.getGroupColumns().isEmpty()){
				
				if(aggregationSession.getAggregateFunctionPairs()!=null && !aggregationSession.getAggregateFunctionPairs().isEmpty()){
					HashMap<String, Object> map = resolveGroupByOperation(aggregationSession, true);
					TDGWTServiceImpl gwtService = new TDGWTServiceImpl();
					
					logger.info("Converting period "+period);
					PeriodType periodType = ConvertForService.periodType(period.getId());
					logger.info("Adding "+GroupByOperationIdentifier.TIME_DIMENSION_AGGR +" = "+periodType);
					map.put(GroupByOperationIdentifier.TIME_DIMENSION_AGGR, periodType.name());
					
					String taskId = gwtService.startTimeAggregation(new TimeAggregationSession(aggregationSession.getTrId(),timeDimensionsColumns.get(0), map), this.getThreadLocalRequest().getSession());
					logger.info("Returning task id: "+taskId +" generated by GroupByOperation");
					return taskId;
				}else
					throw new Exception("Sorry an error occurred when perfoming aggregate by time operation, aggregation function not found");
				
			}else
				throw new Exception("Sorry an error occurred when perfoming aggregate by time operation, group column not found");
			
		}catch (TDGWTSessionExpiredException e){
			logger.error("TDGWTSessionExpiredException, session expired", e);
			throw e;
		} catch (TDGWTServiceException e) {
			logger.error("TDGWTServiceException", e);
			throw e;
		} catch (SecurityException e) {
			logger.error("SecurityException", e);
			throw e;
		}catch(Exception e){
			logger.error("Sorry an error occurred when perfoming group by operation", e);
			throw new Exception("Sorry an error occurred when perfoming group by operation", e);
		}
	}
	
	/**
	 * 
	 * @param aggregationSession
	 * @return
	 * @throws Exception
	 */
	public HashMap<String,Object> resolveGroupByOperation(AggregationColumnSession aggregationSession, boolean isTime) throws Exception{
		logger.trace("Resolving AggregationColumnSession Operation...");
		
		List<ColumnData> groupByColumns = aggregationSession.getGroupColumns();

		if(groupByColumns==null || groupByColumns.isEmpty())
			throw new Exception("Group By Columns parameter is null or empty");
		
		TRId trID = aggregationSession.getTrId();
		logger.trace("Group by column/s is/are: "+groupByColumns.size());
		TdTabularDataService service = columnOperationService();
		Table table;
		List<Map<String, Object>> compositeFunctions;
		try {
			table = ConvertForService.getTable(trID, service);
			logger.info("Converting aggregate functions...");
			compositeFunctions = ConvertForService.buildCompositesByAggregatePairs(table, aggregationSession.getAggregateFunctionPairs());
			logger.info("Converting aggregate functions generate "+compositeFunctions.size() +" result/s");
			
			HashMap<String,Object> grouByParam = new HashMap<String, Object>();
			
			logger.info("Converting group by columns...");
			List<ColumnReference> refs = new ArrayList<ColumnReference>(groupByColumns.size());
			
			for (ColumnData columnData : groupByColumns) {
				ColumnReference ref = ConvertForService.getColumnReference(table, columnData.getName());
				refs.add(ref);
			}
			logger.info("Converted group by column/s returning "+refs.size()+" reference/s");
			
			if(isTime)
				grouByParam.put(GroupByOperationIdentifier.AGGREGATE_BY_TIME, refs);
			else
				grouByParam.put(GroupByOperationIdentifier.GROUPBY_COLUMNS, refs);
			
			grouByParam.put(GroupByOperationIdentifier.AGGREGATE_FUNCTION_TO_APPLY, compositeFunctions);
			
			logger.info("Returning: "+grouByParam);
			
			return grouByParam;
		
		} catch (Exception e) {
			logger.error("Sorry an error occurred when resolving group by operation", e);
			throw new Exception("Sorry an error occurred when resolving operation");
		}
	}
	
	protected TdTabularDataService columnOperationService(){

		HttpSession session = this.getThreadLocalRequest().getSession();
		ASLSession aslSession;
		try {
			aslSession = SessionUtil.getAslSession(session);
			return new TdTabularDataService(aslSession.getScope(), aslSession.getUsername());
		} catch (Exception e) {
			logger.error("Error occurred on instancing the service: ",e);
			return null;
		}
		
	}
	
	@Override
	public List<TdPeriodType> getSuperiorPeriodType(String periodType) throws OperationNotAvailable, Exception{
		
		try {
			PeriodType type = ConvertForService.periodType(periodType);
		
			List<PeriodType> types = ConvertForGwtModule.getSuperiorTimePeriod(type);
			
			if(types==null || types.isEmpty())
				throw new OperationNotAvailable("Time Period too high to aggregate for this column");
			
			List<TdPeriodType> periods = new ArrayList<TdPeriodType>(types.size());
			for (PeriodType pt : types) {
				periods.add(ConvertForGwtModule.getTimeType(pt));
			}
			
			return periods;
		
		} catch (Exception e) {
			logger.error("Error occurred on converting periodType: ",e);
			throw new Exception("Sorry an error occurred on server, operation not available");
		}
	}
	
	
//	public static final String TEST_SCOPE = "/gcube/devsec/devVRE";
//	
//	//TEST PARAMETERS
////	public static final String TEST_USER = "giancarlo.panichi";
////	public static final String TEST_USER = "pasquale.pagano";
////	public static final String TEST_USER = "test.user";
////	public static final String TEST_USER_FULL_NAME = "Test User";	
//	public static final String TEST_USER = "francesco.mangiacrapa";
//	public static final String TEST_USER_FULL_NAME = "Francesco Mangiacrapa";
//	
//	public static ASLSession getAslSession(HttpSession httpSession)
//	{
//		String sessionID = httpSession.getId();
//		String user = (String) httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
//
//		if (user == null) {
//
//			//for test only
//			user = TEST_USER;
////			user = "lucio.lelii";
////			user = "pasquale.pagano";
////			user = "francesco.mangiacrapa";
////			user = "giancarlo.panichi";
//			String scope = TEST_SCOPE;
////			String scope = "/d4science.research-infrastructures.eu/gCubeApps/BiodiversityResearchEnvironment"; //Production
//			
//			httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, user);
//			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, user);
//			session.setScope(scope);
//
//			logger.warn("SessionUtil STARTING IN TEST MODE - NO USER FOUND");
//			logger.warn("Created fake Asl session for user "+user + " with scope "+scope);
//			
//			return session;
//		}
//
//		return SessionManager.getInstance().getASLSession(sessionID, user);
//	}

	public static void main(String[] args) {
		
		TRId fakeTrId = new TRId("3");
		fakeTrId.setTableTypeName("Dataset");
		ColumnData cl = new ColumnData();
		cl.setColumnId("1");
		cl.setTrId(fakeTrId);
		
		SplitAndMergeColumnSession splitSession = new SplitAndMergeColumnSession();
		splitSession.setFirstColumnData(cl);
		splitSession.setValue("[a-z]");
		splitSession.setLabelColumn1("1");
		splitSession.setLabelColumn2("2");
		
		splitSession.setOperatorID(OperationID.SPLIT);
		splitSession.setOperator(new TdOperatorComboOperator("", TdOperatorEnum.REGEX, ""));
		
		
		TdColumnOperationServiceImpl serv = new TdColumnOperationServiceImpl();
		
		try {
			serv.startSplitAndMergeOperation(splitSession);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
  
}
  
	
