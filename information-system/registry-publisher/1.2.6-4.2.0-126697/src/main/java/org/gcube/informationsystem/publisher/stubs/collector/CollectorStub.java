package org.gcube.informationsystem.publisher.stubs.collector;

import static org.gcube.informationsystem.publisher.stubs.collector.CollectorConstants.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.soap.SOAPFaultException;

/**
 * A local interface to the resource discovery service.
 * 
 * 
 */
@WebService(name=portType,targetNamespace=target_namespace)
public interface CollectorStub {

	/**
	 * Executes a {@link QueryStub}.
	 * @param query the query
	 * @return the query results
	 * @throws MalformedQueryException if the query is malformed
	 * @throws SOAPFaultException if the query cannot be executed
	 */
	@WebMethod(operationName="XQueryExecute")
	@WebResult(name="Dataset")
	String execute(@WebParam(name="XQueryExpression") String query) throws MalformedQueryException;
}
