package org.gcube.opensearch.opensearchlibrary.query.extensions.sru;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants;
import org.gcube.opensearch.opensearchlibrary.SRUConstants;
import org.gcube.opensearch.opensearchlibrary.query.BasicQueryBuilder;
import org.gcube.opensearch.opensearchlibrary.query.BasicURLTemplate;
import org.gcube.opensearch.opensearchlibrary.query.IncompleteQueryException;
import org.gcube.opensearch.opensearchlibrary.query.MalformedQueryException;
import org.gcube.opensearch.opensearchlibrary.query.NonExistentParameterException;
import org.gcube.opensearch.opensearchlibrary.query.QueryBuilder;
import org.gcube.opensearch.opensearchlibrary.query.QueryBuilderDecorator;
import org.gcube.opensearch.opensearchlibrary.query.URLTemplate;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement;

public class SRUQueryBuilder extends QueryBuilderDecorator {

	public SRUQueryBuilder(QueryBuilder qb) throws Exception {
		super(qb);
		for(String param: getRequiredParameters()) {
			if(param.equals(SRUConstants.startRecordQname))
				setParameter(SRUConstants.startRecordQname, getStartIndexDef());
		}
	}
	
	@Override
	public QueryBuilder clone() {
		QueryBuilder qb;
		try {
			qb = new SRUQueryBuilder(this.qb);
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
	public SRUQueryBuilder setParameter(String name, String value) throws NonExistentParameterException, Exception  {
		
		if(name.compareTo(SRUConstants.sortKeysQname) == 0 || name.compareTo(SRUConstants.httpAcceptQname) == 0
				|| name.compareTo(SRUConstants.facetSortQname) == 0)
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
	public SRUQueryBuilder setParameters(List<String> names, List<Object> values) throws NonExistentParameterException, Exception {
		
		if(names.size() != values.size())
			throw new Exception("List size mismatch");
		
		int index;
		if((index = names.indexOf(SRUConstants.queryTypeQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.queryTypeQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.queryQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.queryQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.startRecordQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.startRecordQname, (String)values.get(index));
			else if(values.get(index) instanceof Integer)
				setParameter(SRUConstants.startRecordQname, (Integer)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.maximumRecordsQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.maximumRecordsQname, (String)values.get(index));
			else if(values.get(index) instanceof Integer)
				setParameter(SRUConstants.maximumRecordsQname, (Integer)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.recordPackingQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.recordPackingQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.recordSchemaQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.recordSchemaQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.resultSetTTLQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.resultSetTTLQname, (String)values.get(index));
			else if(values.get(index) instanceof Integer)
				setParameter(SRUConstants.resultSetTTLQname, (Integer)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.sortKeysQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.sortKeysQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.stylesheetQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.stylesheetQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.renderingQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.renderingQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.httpAcceptQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.httpAcceptQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.httpAcceptCharsetQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.httpAcceptCharsetQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.httpAcceptEncodingQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.httpAcceptEncodingQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.httpAcceptRangesQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.httpAcceptRangesQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.facetLimitQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.facetLimitQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.facetStartQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.facetStartQname, (String)values.get(index));
			else if(values.get(index) instanceof Integer)
				setParameter(SRUConstants.facetStartQname, (Integer)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.facetSortQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.facetSortQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.facetRangeFieldQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.facetRangeFieldQname, (String)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.facetLowValueQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.facetLowValueQname, (String)values.get(index));
			else if(values.get(index) instanceof Integer)
				setParameter(SRUConstants.facetLowValueQname, (Integer)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.facetHighValueQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.facetHighValueQname, (String)values.get(index));
			else if(values.get(index) instanceof Integer)
				setParameter(SRUConstants.facetHighValueQname, (Integer)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
		if((index = names.indexOf(SRUConstants.facetCountQname)) != 1) {
			if(values.get(index) instanceof String)
				setParameter(SRUConstants.facetCountQname, (String)values.get(index));
			else if(values.get(index) instanceof Integer)
				setParameter(SRUConstants.facetCountQname, (Integer)values.get(index));
			else
				throw new ClassCastException();
			names.remove(index); values.remove(index);
		}
	
		qb.setParameters(names, values);
		
		return this;
	}

	/**
	 * Checks if the parameter values are of consistent form
	 * @throws IncompleteQueryException
	 * @throws MalformedQueryException
	 * @throws Exception 
	 */
	private void validateQuery() throws IncompleteQueryException, MalformedQueryException, Exception {
		
		for(String param: Arrays.asList(SRUConstants.startRecordQname, SRUConstants.maximumRecordsQname, SRUConstants.resultSetTTLQname)) {
			
			if(!isParameterSet(param))
				continue;
			
			Integer intVal = null;
			try {
				intVal = Integer.parseInt(getParameterValue(param));
			}catch(Exception e) {
				//System.out.println(param + " = " + values.get(param));
				throw new MalformedQueryException("Incorrect parameter type", param);
			}
			
			if((param.equals(SRUConstants.maximumRecordsQname) || param.equals(SRUConstants.resultSetTTLQname)) && intVal < 0)
				throw new MalformedQueryException("Non-negative value expected", param);
		}
		
		if(isParameterSet(SRUConstants.recordPackingQname)) {
			String value = null;
			try {
				value = getParameterValue(SRUConstants.recordPackingQname);
			}catch(Exception e) {
				throw new MalformedQueryException(e);
			}
			if(!value.equals("xml") && !value.equals("string"))
				throw new MalformedQueryException("Invalid SRU recordpacking parameter value");
		}
		
		if(isParameterSet(SRUConstants.sortKeysQname)) {
			String value = null;
			try {
				value = getParameterValue(SRUConstants.sortKeysQname);
			}catch(Exception e) {
				throw new MalformedQueryException(e);
			}
			validateSortKeys(SRUConstants.sortKeysQname, value);
		}
		
		if(isParameterSet(SRUConstants.renderingQname)) {
			String value = null;
			try {
				value = getParameterValue(SRUConstants.renderingQname);
			}catch(Exception e) {
				throw new MalformedQueryException(e);
			}
			if(!value.equals("server") && !value.equals("client"))
				throw new MalformedQueryException("Invalid SRU rendering parameter value");
		}
		
		if(isParameterSet(SRUConstants.facetLimitQname) || isParameterSet(SRUConstants.facetCountQname) || 
				isParameterSet(SRUConstants.facetRangeFieldQname) || isParameterSet(SRUConstants.facetLowValueQname) || 
				isParameterSet(SRUConstants.facetHighValueQname) || isParameterSet(SRUConstants.facetSortQname) || 
				isParameterSet(SRUConstants.facetStartQname)) {
			throw new Exception("SRU facet parameters currently not supported");
		}
	}
	
	private void validateSortKeys(String param, String value) throws MalformedQueryException {
		String[] sortKeys = value.split(" ");
		for(String sortKey : sortKeys) {
			String[] subParams = sortKey.split(",");
			if(subParams.length < 1 || subParams.length > 5)
				throw new MalformedQueryException(sortKey + " contains illegal number of subparameters");
			if(subParams[0].equals("")) //path is mandatory
				throw new MalformedQueryException("Path subparameter of " + sortKey + " is missing", param);
			if(subParams[subParams.length-1].equals(""))
				throw new MalformedQueryException("No value found for last defined subparameter of " + sortKey);
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
		
		String queryType = "TODO";
		String query = "TODO";
		long start = Calendar.getInstance().getTimeInMillis();
		URLTemplate template = new BasicURLTemplate("http://www.testTimeExt.com/rdf/?count={count?}&startPage={startPage?}&startIndex={startIndex?}&sort={sru:sortKeys?}&ce={ws:ce?}&protocol={ws:protocol?}&resourcetype={ws:type?}&q={searchTerms?}&start={time:start?}&stop={time:end?}&ingested={dct:modified?}&bbox={geo:box?}&geometry={geo:geometry?}&uid={geo:uid?}&processingCenter={eop:processingCenter?}&acquisitionStation={eop:acquisitionStation?}&size={eop:size?}&orbitNumber={eop:orbitNumber?}&trackNumber={eop:trackNumber?}&lat={geo:lat?}&lon={geo:lon?}&radius={geo:radius?}", nsPrefixes);
		for(int i = 0; i < 1000; i++) {
			SRUQueryBuilder qb = new SRUQueryBuilder(new BasicQueryBuilder(template, "1", "1"));
			qb.setParameter(SRUConstants.queryTypeQname, java.net.URLEncoder.encode(queryType, "UTF-8"));
			qb.setParameter(SRUConstants.queryQname, java.net.URLEncoder.encode(query, "UTF-8"));
			String q = qb.getQuery();
			//System.out.println(qb.getQuery());
			if(i % 100 == 0) {
				System.out.println(i*100 + "th record in " + (Calendar.getInstance().getTimeInMillis() - start) + " millis");
				System.out.println(q);
			}
		}
	}
}
