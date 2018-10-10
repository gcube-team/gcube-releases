package org.gcube.resources.discovery.client.impl;

import java.util.ArrayList;
import java.util.List;

import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.api.DiscoveryException;
import org.gcube.resources.discovery.client.api.InvalidResultException;
import org.gcube.resources.discovery.client.api.ResultParser;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link DiscoveryClient} that delegates the execution of queries to another {@link DiscoveryClient} that
 * does not perform result parsing and the parsing itself to a dedicated {@link ResultParser}.
 * 
 * 
 * @author Fabio Simeoni
 *
 * @param <R> the type of query results
 */
public class DelegateClient<R> implements DiscoveryClient<R> {

	private static Logger log = LoggerFactory.getLogger(DelegateClient.class);
	
	private final ResultParser<R> parser;
	private final DiscoveryClient<String> inner;

	/**
	 * Creates an instance with a given {@link ResultParser} and a {@link DiscoveryClient} that produces untyped results
	 * @param parser the parser
	 * @param inner the client
	 */
	public DelegateClient(ResultParser<R> parser,DiscoveryClient<String> inner) {
		this.parser=parser;
		this.inner=inner;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Result parsing errors are only logged as long as some results are successfully parsed. Otherwise, the
	 * client flags the parsing errors as likely due to the parser itself.
	 */
	public List<R> submit(Query query) throws DiscoveryException, InvalidResultException {
		
		List<R> parsed = new ArrayList<R>();
		
		List<String> unparsed = inner.submit(query);
		
		int errors = 0;
		
		for (String result : unparsed)
			try {
				parsed.add(parser.parse(result));
			}
			catch(Exception e) {
				log.warn("discarded invalid result "+result,e);
				errors++;
			}
		
		if (errors>0 && parsed.size()==0)
			throw new InvalidResultException("no success but "+errors+" errors parsing results");
		
		return parsed;
	}
}
