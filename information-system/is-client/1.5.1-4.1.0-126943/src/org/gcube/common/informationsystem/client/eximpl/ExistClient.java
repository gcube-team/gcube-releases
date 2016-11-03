package org.gcube.common.informationsystem.client.eximpl;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.axis.message.addressing.EndpointReferenceType;

import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.informationsystem.ISException;
import org.gcube.common.core.informationsystem.client.ISInputStream;
import org.gcube.common.core.informationsystem.client.ISQuery;
import org.gcube.common.core.informationsystem.client.impl.AbstractClient;
import org.gcube.common.core.informationsystem.client.impl.AbstractQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager;
import org.gcube.common.informationsystem.client.eximpl.queries.GCUBEGenericQueryImpl;
import org.gcube.informationsystem.collector.stubs.XQueryAccessPortType;
import org.gcube.informationsystem.collector.stubs.XQueryExecuteRequest;
import org.gcube.informationsystem.collector.stubs.XQueryExecuteResponse;
import org.gcube.informationsystem.collector.stubs.XQueryFaultType;
import org.gcube.informationsystem.collector.stubs.service.XQueryAccessServiceLocator;


/**
 * 
 * 
 * Specialisation of {@link AbstractClient} for an Information System which
 * supports XQuery as a query language.
 * 
 * @author Andrea Manzi (CNR), Fabio Simeoni (University of Strathclyde)
 * 
 */
public class ExistClient extends AbstractClient {

    private <RESULT> List<RESULT> _execute(ISQuery<RESULT> query,GCUBEScope scope) 	throws ISException {	
	
		ExistQuery<RESULT> existQuery = (ExistQuery<RESULT>) query; // cannot fail at this point.
		
		List<RESULT> records = new ArrayList<RESULT>();
		
		this.checkQuery(query);
		
		XQueryAccessPortType port = null;
		try {
			List<EndpointReferenceType> eprs = existQuery.getISICEPRs(scope);
			String portTypeURI = eprs.iterator().next().getAddress().toString();
			logger.trace("Submitting the query to " + portTypeURI);
			//checkHostAndWait(portTypeURI);
			port = new XQueryAccessServiceLocator().getXQueryAccessPortTypePort(new URL(portTypeURI));
			port = GCUBERemotePortTypeContext.getProxy(port, scope);	    
		} catch (Exception e) {
			 logger.error("Error while creating portType for query submission, the query was \n" + query.getExpression(), e);
			 throw new ISException(e);
		}
		    
		XQueryExecuteResponse response = null;
		long queryStart = 0;
		try {
		    queryStart = System.currentTimeMillis();
		    String queryString = existQuery.getExpression();
		    if (scope.getType().compareTo(GCUBEScope.Type.VRE) == 0)
			queryString = ExistClientUtil.queryAddAuthenticationControl(queryString, scope.toString());	    
		    logger.debug("Query to submit: " + queryString);	  
		    XQueryExecuteRequest request = new XQueryExecuteRequest();
		    request.setXQueryExpression(queryString);	    
		    response = port.XQueryExecute(request);	    
		 } catch (XQueryFaultType e) {
		     logger.error("XQuery Fault received", e);	    
		     throw new ISException(e);
		 } catch (Exception e1) {
		    logger.error("Error while executing the XQuery " + query.getExpression());
		    throw new ISException(e1);
		}
		long queryEnd = System.currentTimeMillis();
		logger.trace("Query took " + (queryEnd - queryStart) + " ms");
		
		if (response == null) {
		    logger.error("Invalid response received from the IC instance");
		    throw new ISException("Invalid response received from the IC instance");
		}
		
		logger.debug("Number of returned records: " + response.getSize()) ;
		if (response.getSize() == 0)
		    return records;
		    
		try {
		    Pattern p = Pattern.compile("<Record>(.*?)</Record>", Pattern.DOTALL);
		    Matcher m = p.matcher(response.getDataset());
		    while (m.find())
		    	try{
		    		records.add(existQuery.parseResult(m.group(1)));
		    	}catch(ISMalformedResultException me){logger.warn("malformed profile found");}	
		} catch (Exception e) {
		    logger.error("ISclient: Error while parsing XQuery result\n");
		    throw new ISMalformedResultException(e);
		}
		
		return records;
    }
    
//    /**
//     * Checks if the IC service is locally hosted, eventually it waits until it is ready
//     * @param portTypeURI the URI to check
//     * @throws Exception
//     */
//    private void checkHostAndWait(String portTypeURI) throws Exception {
//		GHNContext context = GHNContext.getContext();
//		if (portTypeURI.contains(context.getBaseURL())) {
//		    //the IC service is co-hosted, 
//		    GCUBEServiceContext IC = null;
//		    //let's wait until it is registered
//		    while (IC == null) {
//			try {
//			    IC = context.getServiceContext("InformationSystem", "IS-Collector");
//			    Thread.sleep(2000);
//			} catch (Exception e) {logger.warn("waiting for IC registration");}
//		    }	    
//		    //let's wait until it is ready and can respond to the queries
//		    while (IC.getStatus() != Status.READIED) {logger.trace("waiting for co-hosted IC is ready, now it is " + IC.getStatus().toString());Thread.sleep(2000);}	    
//		}	
//    }
    

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    protected void loadQueries(Map<Class<? extends ISQuery<?>>, Class<? extends ISQuery<?>>> queryMap) {

	try {
	    Properties query = new Properties();
	    query.load(ExistClient.class.getResourceAsStream("QueryImpl.properties"));
	    for (Object prop : query.keySet()) {
		queryMap.put((Class<? extends ISQuery<?>>) Class.forName((String) prop),
			(Class<? extends ISQuery<?>>) Class.forName((String) query.get(prop)));
	    }
	} catch (Exception e) {
	    logger.error("Could not load query implementation", e);
	}
    }

    /**
     * {@inheritDoc}
     */
    protected void loadGenericQueries(Map<String, String> queryMap) {

	try {
	    Properties query = new Properties();
	    query.load(ExistClient.class.getResourceAsStream("GenericQueries.properties"));
	    for (Object prop : query.keySet())
		queryMap.put((String) prop, (String) query.get(prop));
	} catch (Exception e) {
	    logger.error("Could not load query implementation", e);
	}

    }

    /** {@inheritDoc} */
    public GCUBEGenericQuery getQuery(String name) throws ISUnsupportedQueryException {
    	GCUBEGenericQuery query = new GCUBEGenericQueryImpl();
    	String expression = genericQueries.get(name);
    	if (expression == null)
    		throw new ISUnsupportedQueryException();
    	query.setExpression(ExistQuery.NS + expression);
    	return query;
    }

    /**
     * Returns the subtype of {@link ExistQuery} used as the base class for
     * query implementations. This is used by the client to check the validity
     * of queries once and where most conveniente on behalf of its subclasses.
     * 
     * @return the base class.
     */
    @SuppressWarnings("rawtypes")
	@Override
    protected Class<? extends AbstractQuery> getQueryBaseClass() {
    	return ExistQuery.class;
    }

    /** {@inheritDoc} */
    public <RESULT> ISInputStream<RESULT> executeByRef(ISQuery<RESULT> query, GCUBEScope scope) 
    	throws ISMalformedQueryException, ISUnsupportedQueryException, ISException {	
	return new ISInputStreamImpl<RESULT>(this._execute(query, scope));
    }

    /** {@inheritDoc} */    
    public <RESULT> List<RESULT> execute(ISQuery<RESULT> query, GCUBEScope scope)
	    throws ISUnsupportedQueryException, ISException {
	return this._execute(query, scope);
    }
    
    /** {@inheritDoc} */    
    public <RESULT> List<RESULT> execute(ISQuery<RESULT> query)
	    throws ISUnsupportedQueryException, ISException {
	return this._execute(query, GCUBEScopeManager.DEFAULT.getScope());
    }

}
