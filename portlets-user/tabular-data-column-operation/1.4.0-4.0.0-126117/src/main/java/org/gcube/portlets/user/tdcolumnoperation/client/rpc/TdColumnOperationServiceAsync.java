package org.gcube.portlets.user.tdcolumnoperation.client.rpc;


import java.util.List;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.tdcolumnoperation.shared.AggregationColumnSession;
import org.gcube.portlets.user.tdcolumnoperation.shared.OperationID;
import org.gcube.portlets.user.tdcolumnoperation.shared.SplitAndMergeColumnSession;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdAggregateFunction;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdOperatorComboOperator;
import org.gcube.portlets.user.tdcolumnoperation.shared.TdPeriodType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TdColumnOperationServiceAsync
{

 
    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static TdColumnOperationServiceAsync instance;

        public static final TdColumnOperationServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (TdColumnOperationServiceAsync) GWT.create( TdColumnOperationService.class );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instanciated
        }
    }

    public void loadOperatorForOperationId(OperationID operation, AsyncCallback<List<TdOperatorComboOperator>> callback);

	void startSplitAndMergeOperation(
			SplitAndMergeColumnSession operationColumnSession,
			AsyncCallback<String> callback);

	void getListAggregationFunctionIds(
			AsyncCallback<List<TdAggregateFunction>> callback);

	void startGroupByOperation(AggregationColumnSession aggregationSession,
			AsyncCallback<String> callback);

	void getListTimeTypes(AsyncCallback<List<TdPeriodType>> callback);

	void getSuperiorPeriodType(String periodType,
			AsyncCallback<List<TdPeriodType>> callback);

	void startAggregateByTimeOperation(
			AggregationColumnSession aggregationSession, TdPeriodType period,
			List<ColumnData> timeDimensionsColumns, AsyncCallback<String> callback);

}
