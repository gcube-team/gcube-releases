package org.gcube.opensearch.opensearchlibrary.query;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement;
import org.gcube.opensearch.opensearchlibrary.utils.URLEncoder;

/**
 * Query builder class that implements the QueryBuilder interface, providing standard OpenSearch
 * parameter functionality.
 * 
 * @author gerasimos.farantatos
 *
 */
public class BasicQueryBuilder implements QueryBuilder {

	//private String query;
	protected URLTemplate template;

	protected Map<String, String> values = new HashMap<String, String>();
	
	Integer startIndexDef = null;
	Integer startPageDef = null;
	
//	private String searchTermsValue = null;
//	private String startIndexValue = null;
//	private String startPageValue = null;
//	private String countValue = "100"; 
//	private String languageValue = "";
//	private String inputEncodingValue = "";
//	private String outputEncodingValue = "";
	

	
	/**
	 * Creates a new BasicQueryBuilder operating on a URL template, with default values for startIndex and startPage OpenSearch parameters
	 * (typically provided from the URL element which encloses the URL template)
	 * 
	 * @param template The query template that will be used to construct queries
	 * @param startIndexDef The default value for the startIndex standard OpenSearch parameter
	 * @param startPageDef The default value for the startPage standard OpenSearch parameter
	 * @throws Exception In case of error
	 */
	public BasicQueryBuilder(URLTemplate template, String startIndexDef, String startPageDef) throws Exception {
		this.template = template;
		
		//String[] params = new String[]{"searchTerms", "startIndex", "startPage", "count", "language", "inputEncoding", "outputEncoding"};
		
		this.startIndexDef = Integer.parseInt(startIndexDef);
		this.startPageDef = Integer.parseInt(startPageDef);
		
		List<String> params = template.getRequiredParameters();
		params.addAll(template.getOptionalParameters());
		
		try {
			for(String param: params) {
				if(param.compareTo(OpenSearchConstants.startIndexQName) == 0 && template.isParameterRequired(param))
					values.put(param, startIndexDef);
				else if(param.compareTo(OpenSearchConstants.startPageQName) == 0 && template.isParameterRequired(param))
					values.put(param, startPageDef);
				else if(param.compareTo(OpenSearchConstants.countQName) == 0 && !template.isParameterRequired(param))
					values.put(param, "100"); //an arbitrary default value used when count happens to be optional and no value for it is supplied
				else if(param.compareTo(OpenSearchConstants.languageQName) == 0 && template.isParameterRequired(param))
					values.put(param, "*");
				else if((param.compareTo(OpenSearchConstants.inputEncodingQName) == 0 || param.compareTo(OpenSearchConstants.outputEncodingQName) == 0) && template.isParameterRequired(param))
					values.put(param, "UTF-8");
				else if(!template.isParameterRequired(param))
					values.put(param, "");  //set remaining optional parameters to empty string
				else
					values.put(param, null); //set remaining required parameters to null to indicate absence
			}
		}catch(NonExistentParameterException e) {
			throw new Exception("Unexpected exception", e);
		}
		
	}
	
	/**
	 * Creates a new BasicQueryBuilder. Similar to {@link #BasicQueryBuilder(URLTemplate, String, String)} except that it takes an additional default
	 * value for the count standard OpenSearch parameter
	 * 
	 * @param template The query template that will be used to construct queries
	 * @param countDef The default value for the count standard OpenSearch parameter
	 * @param startIndexDef The default value for the startIndex standard OpenSearch parameter
	 * @param startPageDef The default value for the startPage standard OpenSearch parameter
	 * @throws Exception In case of error
	 */
	public BasicQueryBuilder(URLTemplate template, String countDef, String startIndexDef, String startPageDef) throws Exception {
		this(template, startIndexDef, startPageDef);
		if(template.hasParameter(OpenSearchConstants.countQName))
			values.put(OpenSearchConstants.countQName, countDef);
	}
	
	@Override
	public QueryBuilder clone() {
		QueryBuilder qb;
		try {
			qb = new BasicQueryBuilder(template, getStartIndexDef().toString(), getStartPageDef().toString());
		}catch(Exception e) {
			return null;
		}
		return qb;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getRequiredParameters()
	 */
	public List<String> getRequiredParameters() {
		return new ArrayList<String>(template.getRequiredParameters());
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getOptionalParameters()
	 */
	public List<String> getOptionalParameters() {
		return new ArrayList<String>(template.getOptionalParameters());
	}
	

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getParameterValue(String)
	 */
	public String getParameterValue(String name) throws NonExistentParameterException {
		if(!template.hasParameter(name))
			throw new NonExistentParameterException("Parameter not found", name);
		if(values.containsKey(name))
			return values.get(name);
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getUnsetParameters()
	 */
	public List<String> getUnsetParameters() {
		List<String> l = new ArrayList<String>();
		List<String> params = template.getRequiredParameters();
		params.addAll(template.getOptionalParameters());
		for(String param: params) {
			if(values.containsKey(param) && values.get(param) == null)
				l.add(param);
		}
		return l;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getRawTemplate()
	 */
	public String getRawTemplate() {
		return template.getTemplate();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#hasParameter(String)
	 */
	public boolean hasParameter(String name) {
		return template.hasParameter(name);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#setParameter(String, String)
	 */
	public BasicQueryBuilder setParameter(String name, String value) throws NonExistentParameterException, Exception  {
	
		String replacement;
		
		if(!values.containsKey(name))
			throw new NonExistentParameterException("Parameter not found", name);
		
		if(name.compareTo(OpenSearchConstants.searchTermsQName) == 0)
			replacement = URLEncoder.UrlEncode(value, "UTF-8"); //searchTerms must be url-Encoded. Attempt to detect non-urlencoded Strings
		else 
			replacement = value;
		
		values.put(name, replacement);
		
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#setParameter(String, Integer)
	 */
	public BasicQueryBuilder setParameter(String name, Integer value) throws NonExistentParameterException {
		
		if(!values.containsKey(name))
			throw new NonExistentParameterException("Parameter not found", name);
		
		values.put(name, value.toString());
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#setParameters(List, List)
	 */
	public BasicQueryBuilder setParameters(List<String> names, List<Object> values) throws NonExistentParameterException, Exception {
		
		if(names.size() != values.size())
			throw new Exception("List size mismatch");
		
		Iterator<String> nameIt = names.iterator();
		Iterator<Object> valueIt = values.iterator();
		
		while(nameIt.hasNext()) {
			Object obj = valueIt.next();
			if(obj instanceof String)
				setParameter(nameIt.next(), (String)obj);
			else if(obj instanceof Integer)
				setParameter(nameIt.next(), (Integer)obj);
			else
				throw new ClassCastException();
		}
		
		return this;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#isParameterSet(String)
	 */
	public boolean isParameterSet(String name) {
		try {
			if(getParameterValue(name) != null && !getParameterValue(name).equals(""))
				return true;
		}catch(NonExistentParameterException e) { 
			return false;
		}
		return false;
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
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#isQueryComplete()
	 */
	public boolean isQueryComplete() {
		return values.containsValue(null) ? false : true;

	}

	/**
	 * Checks if the parameter values are of consistent form
	 * 
	 * @throws MalformedQueryException If a parameter value is found to be in incorrect format
	 */
	private void validateQuery() throws MalformedQueryException {
		
		for(String param: Arrays.asList(OpenSearchConstants.startIndexQName, OpenSearchConstants.startPageQName, OpenSearchConstants.countQName)) {
			if(values.containsKey(param) && values.get(param) != null && values.get(param).compareTo("") != 0) {
				Integer intVal = null;
				try {
					intVal = Integer.parseInt(values.get(param));
				}catch(Exception e) {
					//System.out.println(param + " = " + values.get(param));
					throw new MalformedQueryException("Incorrect parameter type", param);
				}
				
				if(param.compareTo(OpenSearchConstants.countQName) == 0 && intVal < 0)
					throw new MalformedQueryException("Non-negative value expected", param);
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
		
		if(isQueryComplete() == false)
			throw new IncompleteQueryException();

		String query = template.getTemplate();
		for(Map.Entry<String,String> e : values.entrySet())  {
			if(template.isParameterRequired(e.getKey()))
				query = query.replaceAll("\\{" + e.getKey() + "\\}", e.getValue());
			else
				query = query.replaceAll("\\{" + e.getKey() + "\\?\\}", e.getValue());
		}
		return query;
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

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getStartIndexDef()
	 */
	public Integer getStartIndexDef() {
		return startIndexDef;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.QueryBuilder#getStartPageDef()
	 */
	public Integer getStartPageDef() {
		return startPageDef;
	}
	
	public static void main(String[] args) throws Exception {
//		BasicQueryBuilder q = new BasicQueryBuilder(new BasicURLTemplate("http://www.example.com/search/search.asp?query={searchTerms}&amp;start={startIndex?}"), "10", "1", "1");
//		System.out.println(q.getRequiredParameters());
//		System.out.println(q.getOptionalParameters());
//		System.out.println(q.hasParameter("abc"));
//		System.out.println(q.hasParameter("searchTerms"));
//		System.out.println(q.hasParameter("startIndex"));
//		System.out.println(q.setParameter("searchTerms", "foo").isQueryComplete());
//		System.out.println(q.getQuery());
//		
//		q = new BasicQueryBuilder(new BasicURLTemplate("http://www.nature.com/opensearch/request?interface=opensearch&version=1.1&operation=searchRetrieve&query={searchTerms}&queryType={sru:queryType?}&httpAccept=application/rss%2Bxml&recordPacking=unpacked&startRecord={startIndex?}&maximumRecords={count?}&sortKeys={sru:sortKeys?}&stylesheet={sru:stylesheet?}"), "10", "1", "1");
//		System.out.println(q.getRequiredParameters());
//		System.out.println(q.getOptionalParameters());
//		System.out.println(q.hasParameter("abc"));
//		System.out.println(q.hasParameter("searchTerms"));
//		System.out.println(q.hasParameter("startIndex"));
//		System.out.println(q.setParameter("searchTerms", "foo").isQueryComplete());
//		System.out.println(q.getQuery());
		
		
//	    System.out.println(URLEncoder.encode("This string has spaces"));
//	    System.out.println(URLEncoder.encode("This*string*has*stars"));
//	    System.out.println(URLEncoder.encode("This%string%has%percent%signs"));
//	    System.out.println(URLEncoder.encode("This+string+has+pluses"));
//	    System.out.println(URLEncoder.encode("This/string/has/slashes"));
//	    System.out.println(URLEncoder.encode("This\"string\"has\"quote\"marks"));
//	    System.out.println(URLEncoder.encode("This:string:has:colons"));
//	    System.out.println(URLEncoder.encode("This.string.has.periods"));
//	    System.out.println(URLEncoder.encode("This=string=has=equals=signs"));
//	    System.out.println(URLEncoder.encode("This&string&has&ampersands"));
//	    
//	    System.out.println("");
//	    System.out.println(URLDecoder.decode("This string has spaces"));
//	    System.out.println(URLDecoder.decode("This*string*has*stars"));
//	//    System.out.println(URLDecoder.decode("This%string%has%percent%signs"));
//	    System.out.println(URLDecoder.decode("This%25string%25has%25percent%25signs"));
//	    System.out.println(URLDecoder.decode("This+string+has+pluses"));
//	    System.out.println(URLDecoder.decode("This/string/has/slashes"));
//	    System.out.println(URLDecoder.decode("This\"string\"has\"quote\"marks"));
//	    System.out.println(URLDecoder.decode("This:string:has:colons"));
//	    System.out.println(URLDecoder.decode("This.string.has.periods"));
//	    System.out.println(URLDecoder.decode("This=string=has=equals=signs"));
//	    System.out.println(URLDecoder.decode("This&string&has&ampersands"));
	}

}
