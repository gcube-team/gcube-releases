package org.gcube.opensearch.opensearchlibrary.query;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants;

/**
 * Class implementing the URLTemplate interface
 * 
 * @author gerasimos.farantatos
 *
 */
public class BasicURLTemplate implements URLTemplate {

	/**
	 * Class representing a query parameter, containing the qualified name of the parameter
	 * and information about whether the parameter is required or not
	 * 
	 * @author gerasimos.farantatos
	 *
	 */
	protected static class Parameter {
		public String name;
		boolean optional;
	
		/**
		 * Creates a new Parameter object 
		 */
		public Parameter() { }
		
		/**
		 * Creates a new Parameter object with a given qualified name and required-optional information
		 * 
		 * @param name The qualified name of the parameter
		 * @param optional False if the parameter is required, true otherwise
		 */
		public Parameter(String name, boolean optional) {
			this.name = name;
			this.optional = optional;
		}
		
		/**
		 * Determines if two Parameter objects are the same. Two parameters are considered the
		 * same if they have the same qualified name, regardless of their required or optional status
		 * 
		 * @param p The Parameter that will be checked for equality with this instance
		 * @return true if the parameters are equal, false otherwise
		 */
		@Override
		public boolean equals(Object p) {
			if(p == null)
				return false;
			if(!(p instanceof Parameter))
				return false;
			return this.name.compareTo(((Parameter)p).name) == 0;
		}
	}
	
	private static 	Pattern paramPattern = Pattern.compile("\\{[^\\}]*\\}");
	
	protected String template;
	protected List<Parameter> parameters = new ArrayList<Parameter>();

	/**
	 * Creates a new BasicURLTemplate object
	 * 
	 * @param template The query template that will be used
	 * @param nsPrefixes The mapping from namespace URIs to namespace prefixes for all namespaces contained in a description document
	 * @throws Exception If a parameter namespace URI is not present in the description document or in case of other error
	 */
	public BasicURLTemplate(String template, Map<String, String> nsPrefixes) throws Exception {
		Matcher m = paramPattern.matcher(template);
		while(m.find()) {
			Parameter paramEntry = new Parameter();
			paramEntry.optional = false;
			String param = m.group().trim();
			param = param.substring(1, param.length()-1);
			if(param.charAt(param.length()-1) == '?') {
				paramEntry.name = param.substring(0, param.length()-1);
				paramEntry.optional = true;
			}
			else
				paramEntry.name = param;
			int index;
			String encodedURLParam;
			if((index = paramEntry.name.indexOf(":")) == -1)
				encodedURLParam = URLEncoder.encode(OpenSearchConstants.OpenSearchNS, "UTF-8") + ":" + paramEntry.name;
			else {
				String nsPrefix = paramEntry.name.substring(0, index);
				String nsUrl = null;
				for(Map.Entry<String, String> e : nsPrefixes.entrySet()) {
					if(e.getValue().equals(nsPrefix))
						nsUrl = e.getKey();
				}
				if(nsUrl == null)
					throw new Exception("Namespace " + nsPrefix + " is not specified in the description document");
				encodedURLParam = URLEncoder.encode(nsUrl, "UTF-8") + ":" + paramEntry.name.substring(index+1);
				
			}
			template = template.replace("{"+paramEntry.name+(paramEntry.optional ? "?" : "") + "}", "{"+encodedURLParam + (paramEntry.optional ? "?" : "") + "}");
			paramEntry.name = encodedURLParam;
			parameters.add(paramEntry);
			this.template = template;
		}
	}
	
	private List<String> getParameters(boolean opt) {
		List<String> rp = new ArrayList<String>();
		Iterator<Parameter> it = parameters.iterator();
		while(it.hasNext()) {
			Parameter p = it.next();
			if(p.optional == opt)
				rp.add(new String(p.name));
		}
		return rp;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.URLTemplate#getRequiredParameters()
	 */
	public List<String> getRequiredParameters() {
		return getParameters(false);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.URLTemplate#getOptionalParameters()
	 */
	public List<String> getOptionalParameters() {
		return getParameters(true);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.URLTemplate#isParameterRequired(String)
	 */
	public boolean isParameterRequired(String name) throws NonExistentParameterException {
		Iterator<Parameter> it = parameters.iterator();
		while(it.hasNext()) {
			Parameter p = it.next();
			if(p.name.compareTo(name) == 0)
				return p.optional == true ? false : true;
		}
		throw new NonExistentParameterException("Parameter not found: " + name);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.URLTemplate#hasParameter(String)
	 */
	public boolean hasParameter(String name) {
		Parameter p = new Parameter(name, false);
		if(parameters.contains(p))
			return true;
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.query.URLTemplate#getTemplate()
	 */
	public String getTemplate() {
		return template;
	}
	
//	public static void main(String[] args) {
//		GenericURLTemplate t = new GenericURLTemplate("http://www.example.com/search/search.asp?query={searchTerms}&amp;start={startIndex?}");
//		System.out.println(t.getRequiredParameters());
//		System.out.println(t.getOptionalParameters());
//		System.out.println(t.hasParameter("abc"));
//		System.out.println(t.hasParameter("searchTerms"));
//		System.out.println(t.hasParameter("startIndex"));
//	}
}