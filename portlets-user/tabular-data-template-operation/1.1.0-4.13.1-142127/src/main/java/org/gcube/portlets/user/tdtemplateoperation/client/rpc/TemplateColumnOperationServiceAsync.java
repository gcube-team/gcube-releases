package org.gcube.portlets.user.tdtemplateoperation.client.rpc;


import java.util.List;

import org.gcube.portlets.user.tdtemplateoperation.shared.TdAggregateFunction;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdPeriodType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TemplateColumnOperationServiceAsync
{

 
    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static TemplateColumnOperationServiceAsync instance;

        public static final TemplateColumnOperationServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (TemplateColumnOperationServiceAsync) GWT.create(TemplateColumnOperationService.class );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instanciated
        }
    }


	void getListAggregationFunctionIds(
			AsyncCallback<List<TdAggregateFunction>> callback);

	void getListTimeTypes(AsyncCallback<List<TdPeriodType>> callback);

	void getSuperiorPeriodType(String periodType,
			AsyncCallback<List<TdPeriodType>> callback);

	void getTimeDimensionGroupPeriodType(AsyncCallback<List<TdPeriodType>> callback);
	
	void getYearTimeDimension(AsyncCallback<TdPeriodType> callback);
}
