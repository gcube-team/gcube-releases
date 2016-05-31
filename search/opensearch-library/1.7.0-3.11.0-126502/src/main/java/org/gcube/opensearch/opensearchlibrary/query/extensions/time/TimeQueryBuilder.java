package org.gcube.opensearch.opensearchlibrary.query.extensions.time;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.opensearch.opensearchlibrary.TimeConstants;
import org.gcube.opensearch.opensearchlibrary.query.BasicQueryBuilder;
import org.gcube.opensearch.opensearchlibrary.query.BasicURLTemplate;
import org.gcube.opensearch.opensearchlibrary.query.IncompleteQueryException;
import org.gcube.opensearch.opensearchlibrary.query.MalformedQueryException;
import org.gcube.opensearch.opensearchlibrary.query.NonExistentParameterException;
import org.gcube.opensearch.opensearchlibrary.query.QueryBuilder;
import org.gcube.opensearch.opensearchlibrary.query.QueryBuilderDecorator;
import org.gcube.opensearch.opensearchlibrary.query.URLTemplate;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement;

public class TimeQueryBuilder extends QueryBuilderDecorator {

	public TimeQueryBuilder(QueryBuilder qb) {
		super(qb);
	}
	
	@Override
	public QueryBuilder clone() {
		QueryBuilder qb;
		try {
			qb = new TimeQueryBuilder(this.qb);
		}catch(Exception e) {
			return null;
		}
		return qb;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#setParameter(String, String)
	 */
	public TimeQueryBuilder setParameter(String name, String value) throws NonExistentParameterException, Exception  {
		
		if(name.compareTo(TimeConstants.startQname) == 0 || name.compareTo(TimeConstants.endQname) == 0)
			qb.setParameter(name, URLDecoder.decode(value, "UTF-8"));
		else
			qb.setParameter(name, value);
		
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#setParameters(List, List)
	 */
	public TimeQueryBuilder setParameters(List<String> names, List<Object> values) throws NonExistentParameterException, Exception {
		
		if(names.size() != values.size())
			throw new Exception("List size mismatch");
		
		int index;
		if((index = names.indexOf(TimeConstants.startQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(TimeConstants.startQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(TimeConstants.endQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(TimeConstants.endQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
	
		qb.setParameters(names, values);
		
		return this;
	}
	
//	public BasicQueryBuilder setOptionalToEmpty() throws NonExistentParameterException, Exception {
//		Iterator<String> it = template.getOptionalParameters().iterator();
//		
//		while(it.hasNext())
//			replaceParameter(it.next(), "");	
//		
//		return this;
//	}

	/**
	 * Checks if the parameter values are of consistent form
	 * 
	 * @throws MalformedQueryException If a parameter value is found to be in incorrect format
	 */
	private void validateQuery() throws MalformedQueryException {
		
		for(String param: Arrays.asList(TimeConstants.startQname, TimeConstants.endQname)) {
			
			if(!isParameterSet(param))
				continue;
			
			String value = null;
			try {
				value = getParameterValue(param);
			}catch(Exception e) {
				throw new MalformedQueryException(e);
			}
			if(value == null || value.compareTo("") == 0)
				continue;
			
			//TODO Code to check validity of timestamps
		}
		
		if(isParameterSet(TimeConstants.startQname) && isParameterSet(TimeConstants.endQname)) {
			try {
				if(getParameterValue(TimeConstants.startQname).compareTo(getParameterValue(TimeConstants.endQname)) > 0) //Timestamp string comparison amounts to time comparison
					throw new MalformedQueryException("time:start parameter value does not indicate a point in time before time:end");
			}catch(Exception e) {
				throw new MalformedQueryException(e);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getQuery()
	 */
	public String getQuery() throws IncompleteQueryException, MalformedQueryException, Exception {

		validateQuery();
		return qb.getQuery();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#setParameters(QueryElement)
	 */
	public QueryBuilder setParameters(QueryElement queryEl) throws NonExistentParameterException, Exception    {

		Map<String, String> m = queryEl.getQueryParameters();
		for(Map.Entry<String, String> e : m.entrySet())
			setParameter(e.getKey(), e.getValue());

		return this;
	}
	
	public static void main(String[] args) throws Exception {
		Map<String, String> nsPrefixes = new HashMap<String, String>();
		nsPrefixes.put("http://www.w3.org/1999/02/22-rdf-syntax-ns#","rdf");
		nsPrefixes.put("http://www.genesi-dr.eu/spec/opensearch/extensions/eop/1.0/","eop");
		nsPrefixes.put("http://a9.com/-/opensearch/extensions/time/1.0/","time");
		nsPrefixes.put("http://a9.com/-/opensearch/extensions/geo/1.0/","geo"); 
		nsPrefixes.put("http://earth.esa.int/sar","sar"); 
	    nsPrefixes.put("http://purl.org/dc/elements/1.1/","dc");
		nsPrefixes.put("http://purl.org/dc/terms/","dct");
		nsPrefixes.put("http://xmlns.com/2008/dclite4g#","dclite4g"); 
		nsPrefixes.put("http://www.w3.org/2002/12/cal/ical#","ical");
		nsPrefixes.put("http://www.w3.org/2005/Atom","atom");
		nsPrefixes.put("http://www.example.com/schemas/envisat.rdf#","envisat");
		nsPrefixes.put("http://www.w3.org/2002/07/owl#","owl"); 
		nsPrefixes.put("http://downlode.org/Code/RDF/file-properties/","fp"); 
		nsPrefixes.put("http://dclite4g.xmlns.com/ws.rdf#", "ws"); 
		nsPrefixes.put("http://a9.com/-/spec/opensearch/1.1/", "os");
		nsPrefixes.put("http://www.eorc.jaxa.jp/JERS-1/en/", "jers"); 
		nsPrefixes.put("http://a9.com/-/opensearch/extensions/sru/2.0/", "sru"); 
		
		String startTime = "2007-02-12T04:30:02Z";
		String endTime = "2007-03-11T02:28:00Z";
		long start = Calendar.getInstance().getTimeInMillis();
		URLTemplate template = new BasicURLTemplate("http://www.testTimeExt.com/rdf/?count={count?}&startPage={startPage?}&startIndex={startIndex?}&sort={sru:sortKeys?}&ce={ws:ce?}&protocol={ws:protocol?}&resourcetype={ws:type?}&q={searchTerms?}&start={time:start?}&stop={time:end?}&ingested={dct:modified?}&bbox={geo:box?}&geometry={geo:geometry?}&uid={geo:uid?}&processingCenter={eop:processingCenter?}&acquisitionStation={eop:acquisitionStation?}&size={eop:size?}&orbitNumber={eop:orbitNumber?}&trackNumber={eop:trackNumber?}&lat={geo:lat?}&lon={geo:lon?}&radius={geo:radius?}", nsPrefixes);
		for(int i = 0; i < 1000; i++) {
			TimeQueryBuilder qb = new TimeQueryBuilder(new BasicQueryBuilder(template, "1", "1"));
			qb.setParameter(TimeConstants.startQname, java.net.URLEncoder.encode(startTime, "UTF-8"));
			qb.setParameter(TimeConstants.endQname, java.net.URLEncoder.encode(endTime, "UTF-8"));
			String q = qb.getQuery();
			//System.out.println(qb.getQuery());
			if(i % 100 == 0) {
				System.out.println(i*100 + "th record in " + (Calendar.getInstance().getTimeInMillis() - start) + " millis");
				System.out.println(q);
			}
		}
	}
}
