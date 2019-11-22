package org.gcube.portlets.user.tdtemplateoperation.server;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.portlets.user.tdtemplateoperation.client.rpc.TemplateColumnOperationService;
import org.gcube.portlets.user.tdtemplateoperation.shared.OperationNotAvailable;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdAggregateFunction;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdPeriodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TemplateColumnOperationServiceImpl extends RemoteServiceServlet implements TemplateColumnOperationService {

	public static Logger logger = LoggerFactory.getLogger(TemplateColumnOperationServiceImpl.class);
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplateoperation.client.rpc.TemplateColumnOperationService#getListAggregationFunctionIds()
	 */
	@Override
	public List<TdAggregateFunction> getListAggregationFunctionIds() {
		return ConvertOperationForGwtModule.getAggregationFunctionIdsForTemplate();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplateoperation.client.rpc.TemplateColumnOperationService#getListTimeTypes()
	 */
	@Override
	public List<TdPeriodType> getListTimeTypes() {
		return ConvertOperationForGwtModule.getTimeTypes();
	}

	@Override
	public List<TdPeriodType> getSuperiorPeriodType(String periodType) throws OperationNotAvailable, Exception{
		
		try {
			PeriodType type = ConvertOperationForService.periodType(periodType);
		
			List<PeriodType> types = ConvertOperationForGwtModule.getSuperiorTimePeriod(type);
			
			if(types==null || types.isEmpty())
				throw new OperationNotAvailable("Time Period too high to aggregate for this column");
			
			List<TdPeriodType> periods = new ArrayList<TdPeriodType>(types.size());
			for (PeriodType pt : types) {
				periods.add(ConvertOperationForGwtModule.getTimeType(pt));
			}
			
			return periods;
		
		} catch (Exception e) {
			logger.error("Error occurred on converting periodType: ",e);
			throw new Exception("Sorry an error occurred on server, operation not available");
		}
	}
	
	@Override
	public List<TdPeriodType> getTimeDimensionGroupPeriodType() throws OperationNotAvailable, Exception{
		
		try {
			
			List<TdPeriodType> periods = new ArrayList<TdPeriodType>(PeriodType.values().length);
			
			periods.add(ConvertOperationForGwtModule.getTimeType(PeriodType.QUARTER));
			periods.add(ConvertOperationForGwtModule.getTimeType(PeriodType.MONTH));

			return periods;
		
		} catch (Exception e) {
			logger.error("Error occurred on converting periodType: ",e);
			throw new Exception("Sorry an error occurred on server, operation not available");
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.tdtemplateoperation.client.rpc.TemplateColumnOperationService#getYearTimeDimension()
	 */
	@Override
	public TdPeriodType getYearTimeDimension() throws Exception {
		return ConvertOperationForGwtModule.getTimeType(PeriodType.YEAR);
	}

}
