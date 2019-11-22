package org.gcube.portlets.user.tdcolumnoperation.client.rpc;

import java.util.List;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdcolumnoperation.shared.AggregationColumnSession;
import org.gcube.portlets.user.tdcolumnoperation.shared.OperationID;
import org.gcube.portlets.user.tdcolumnoperation.shared.OperationNotAvailable;
import org.gcube.portlets.user.tdcolumnoperation.shared.SplitAndMergeColumnSession;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdAggregateFunction;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdOperatorComboOperator;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdPeriodType;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("TdColumnOperationServlet")
public interface TdColumnOperationService extends RemoteService {

	/**
	 * @param operation
	 * @return
	 */
	List<TdOperatorComboOperator> loadOperatorForOperationId(OperationID operation);

	/**
	 * @param operationColumnSession
	 * @return 
	 * @throws Exception
	 */
	String startSplitAndMergeOperation(
			SplitAndMergeColumnSession operationColumnSession) throws Exception;

	/**
	 * @return
	 */
	List<TdAggregateFunction> getListAggregationFunctionIds();

	/**
	 * @param aggregationSession
	 * @return 
	 * @throws Exception
	 */
	String startGroupByOperation(AggregationColumnSession aggregationSession)
			throws Exception;

	/**
	 * @return
	 */
	List<TdPeriodType> getListTimeTypes();

	/**
	 * @param periodType
	 * @return
	 * @throws OperationNotAvailable
	 * @throws Exception
	 */
	List<TdPeriodType> getSuperiorPeriodType(String periodType) throws OperationNotAvailable, Exception;

	String startAggregateByTimeOperation(
			AggregationColumnSession aggregationSession, TdPeriodType period,
			List<ColumnData> timeDimensionsColumns) throws Exception;



}
