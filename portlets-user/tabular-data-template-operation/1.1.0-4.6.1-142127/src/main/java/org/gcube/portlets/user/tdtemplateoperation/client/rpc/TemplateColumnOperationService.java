package org.gcube.portlets.user.tdtemplateoperation.client.rpc;

import java.util.List;

import org.gcube.portlets.user.tdtemplateoperation.shared.OperationNotAvailable;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdAggregateFunction;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdPeriodType;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("templateColumnOperationService")
public interface TemplateColumnOperationService extends RemoteService {

	/**
	 * Gets the list aggregation function ids.
	 *
	 * @return the list aggregation function ids
	 */
	List<TdAggregateFunction> getListAggregationFunctionIds();


	/**
	 * Gets the list time types.
	 *
	 * @return the list time types
	 */
	List<TdPeriodType> getListTimeTypes();

	/**
	 * Gets the superior period type.
	 *
	 * @param periodType the period type
	 * @return the superior period type
	 * @throws OperationNotAvailable the operation not available
	 * @throws Exception the exception
	 */
	List<TdPeriodType> getSuperiorPeriodType(String periodType) throws OperationNotAvailable, Exception;


	/**
	 * @return
	 * @throws OperationNotAvailable
	 * @throws Exception
	 */
	List<TdPeriodType> getTimeDimensionGroupPeriodType() throws OperationNotAvailable, Exception;
	
	/**
	 * 
	 */
	TdPeriodType getYearTimeDimension() throws Exception;
}
