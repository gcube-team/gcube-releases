package org.gcube.data.analysis.tabulardata.commons.webservice;

import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.gcube.data.analysis.tabulardata.commons.utils.Constants;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.HistoryData;
import org.gcube.data.analysis.tabulardata.model.table.Table;


@WebService(targetNamespace=Constants.HISTORY_TNS)
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL)
public interface HistoryManager {

	public static final String SERVICE_NAME = "historymanager";
	
	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	Table getLastTable(long tabularResourceId) throws NoSuchTabularResourceException, NoSuchTableException, InternalSecurityException;

	@SOAPBinding(parameterStyle=ParameterStyle.WRAPPED)
	List<HistoryData> getHistory(long tabularResourceId)
			throws NoSuchTabularResourceException, InternalSecurityException;

}
