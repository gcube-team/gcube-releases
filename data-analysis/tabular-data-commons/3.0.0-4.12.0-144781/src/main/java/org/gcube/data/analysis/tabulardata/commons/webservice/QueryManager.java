package org.gcube.data.analysis.tabulardata.commons.webservice;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.gcube.data.analysis.tabulardata.commons.utils.Constants;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect;

@WebService(targetNamespace=Constants.QUERY_TNS)
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)
public interface QueryManager {

	public static final String SERVICE_NAME = "querymanager";
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	public Table getTable(long tableId) throws NoSuchTableException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	public String queryAsJson(long tableId, QueryPage page, QuerySelect select, QueryFilter filter, QueryGroup group, QueryOrder order) throws NoSuchTableException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	public int getQueryLenght(long tableId, QueryFilter filter) throws NoSuchTableException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	public Table getTimeTable(PeriodType period);

}
