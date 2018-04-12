package org.gcube.common.core.informationsystem.client.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gcube.common.core.informationsystem.ISException;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.ISInputStream;
import org.gcube.common.core.informationsystem.client.ISQuery;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * Partial implementation of the {@link ISClient}.
 * Subclasses implement specific methods to load implementations of standard {link ISQuery ISQueries},
 * and to execute those queries against the associated Information System.
 *  
 * @author Andrea Manzi (CNR), Fabio Simeoni (University of Strathclyde)
 *
 * 
 *  */
public abstract class AbstractClient implements ISClient {

	/**Object logger. */
	protected GCUBELog logger = new GCUBELog(this);

	/**Query implementations indexed by their standard interface.*/
	static protected Map<Class<? extends ISQuery<?>>,Class<? extends ISQuery<?>>> implementationMap;
	
	/**Query implementations indexed by their implementation-defined name.*/
	static protected Map<String,String> genericQueries;
	
	/**
	 * Creates a new instance and loads query implementations if not previously done. 
	 */
	public AbstractClient() {
		
		 if (implementationMap==null) {
			 implementationMap = Collections.synchronizedMap(new HashMap<Class<? extends ISQuery<?>>,Class<? extends ISQuery<?>>>());
			 this.loadQueries(implementationMap);
			 genericQueries = Collections.synchronizedMap(new HashMap<String,String>());
			 this.loadGenericQueries(genericQueries);
		 }
	}
	
	/**
	 * Implement to load query implementations in a map which indexes by their standard interfaces.
	 * @param queryMap the map.
	 */
	protected abstract void loadQueries(Map<Class<? extends ISQuery<?>>,Class<? extends ISQuery<?>>> queryMap);
	
	/**
	 * Implement to load query implementations in a map which indexes by their standard interfaces.
	 * @param queryMap the map.
	 */
	protected abstract void loadGenericQueries(Map<String,String> queryMap);
	
	
	
	/** {@inheritDoc}*/
	@SuppressWarnings("unchecked")
	public <RESULT, QUERY extends ISQuery<RESULT>> QUERY getQuery(Class<QUERY> clazz) throws ISUnsupportedQueryException,InstantiationException,IllegalAccessException {
		Class<QUERY> impl = (Class<QUERY>) implementationMap.get(clazz);
		if (impl==null) throw new ISUnsupportedQueryException();
		return impl.newInstance();
	}
	

	/**
	 * {@inheritDoc}
	 * @throws ISMalformedResultException 
	 */
	public abstract <RESULT> List<RESULT> execute(ISQuery<RESULT> query,	GCUBEScope scope) throws ISUnsupportedQueryException, ISException; 
	/*	this.checkQuery(query);
		
		
		AbstractQuery<RESULT> abstractQuery= (AbstractQuery) query;//cannot fail at this point.
		List<RESULT> results = new ArrayList<RESULT>();
		//submitting given query in given scope
		logger.debug("Submitting query by val "+query.getExpression()+" in scope "+scope);
		for (String result : this._execute(abstractQuery,scope)) 	
				results.add(abstractQuery.parseResult(result));
				
		return results;

	}*/
	
	/**
	 * {@inheritDoc}
	 * @throws ISException 
	 *//*
	public RSReader executeByRef(ISQuery<?> query, GCUBEScope scope)	throws ISMalformedQueryException, ISInvalidQueryException, ISException {
		
		//submitting given query in given scope
		logger.debug("Submitting query by ref "+query.getQueryExpression()+" in scope "+scope);
		AbstractQuery<?> abstractQuery= (AbstractQuery) query;//cannot fail at this point.
		try {
			return RSReader.getRSReader(new RSLocator(this.executeByRef(abstractQuery,scope))).makeLocal(new RSResourceLocalType()); //callback to  implementation
		}
		catch(Exception e ) {
			throw new ISException(e);
		}
	}
*/
	/**
	 * Invoked internally by {@link #execute(ISQuery, GCUBEScope)} to check that a 
	 * can be handled by the implementation and is in fact well-formed.
	 * By default it checks that the query is an {@link AbstractQuery} and can thus be handled by subclasses.
	 * Subclasses may wish to override the method to enforce more specific checks.
	 * @param query the query.
	 * @throws ISMalformedQueryException if the query is not well-formed.
	 * @throws ISInvalidQueryException if the query if of a type which cannot be processed by the implementatio
	 */
	@SuppressWarnings("unchecked")
	protected void checkQuery(ISQuery<?> query) throws ISMalformedQueryException, ISUnsupportedQueryException {

		if (!this.getQueryBaseClass().isAssignableFrom(query.getClass())) throw new ISUnsupportedQueryException();
		AbstractQuery<?> abstractQuery= (AbstractQuery) query;
		if (!abstractQuery.isWellFormed()) throw new ISMalformedQueryException();

	}
	
	/**
	 * Implement to execute the query against the associated Information System in a given scope, and to return a list of unparsed result serialisations.
	 * Note that, if required, the query can be safely cast to the subtype of {@link AbstractQuery} used by the implementation, as runtime checkes
	 * have already been performed. 
	 *  
	 * @param query the query.
	 * @param scope the scope.
	 * @return the list of unparsed result serialisations.
	 * @throws ISException if the query could not be executed
	 */
	//protected abstract List<String> _execute(ISQuery<RESULT> query, GCUBEScope scope) throws ISException;
	
	/**
	 * Implement to execute the query against the associated Information System in a given scope, and to return the locator of a resultset which contains the unparsed result serialisations.
	 * Note that, if required, the query can be safely cast to the subtype of {@link AbstractQuery} used by the implementation, as runtime checks.
	 * have already been performed. 
	 *  
	 * @param query the query.
	 * @param scope the scope.
	 * @return the ISInputStream.
	 * @throws ISException if the query could not be executed
	 */
	public abstract <RESULT> ISInputStream<RESULT> executeByRef(ISQuery<RESULT> query, GCUBEScope scope) throws ISMalformedQueryException,ISUnsupportedQueryException,ISException;
	
	/**
	 * Returns the subtype of {@link AbstractQuery} used as the base class for query implementations.
	 * This is used by the client to check the validity of queries once and where most convenient 
	 * on behalf of its subclasses.
	 * @return the base class.
	 */
	@SuppressWarnings("unchecked")
	protected Class<? extends AbstractQuery> getQueryBaseClass() {return AbstractQuery.class;}
}


