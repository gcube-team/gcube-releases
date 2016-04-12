package org.gcube.rest.commons.filter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.gcube.rest.commons.helpers.JSHelper;
import org.gcube.rest.commons.helpers.JSONConverter;
import org.gcube.rest.commons.helpers.ObjectHelper;
import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceFilter<T extends StatefulResource> implements IResourceFilter<T> {

	private static final Logger logger = LoggerFactory.getLogger(ResourceFilter.class);

	public List<T> apply(List<T> objs, String filterString) {
		
		logger.info("Applying filter " + filterString + " to : " + objs);
		
		List<T> filteredObjs = new ArrayList<T>();

		for (T obj : objs) {
			if (apply(obj, filterString))
				filteredObjs.add(obj);
		}

		return filteredObjs;
	}
	
	
	public  List<String> applyIDs(List<T> objs, String filterString) {
		
		logger.info("Applying filter " + filterString + " to : " + objs);
		
		List<String> filteredIDs = new ArrayList<String>();

		for (T obj : objs) {
			if (apply(obj, filterString))
				filteredIDs.add(obj.getResourceID());
		}

		return filteredIDs;
	}

	public  Boolean apply(T obj, String filterString) {
		String filterExpression = null;
		
		try {
			filterExpression = evaluatedFilter(obj, filterString);
		} catch (Exception e) {
			logger.error("Error while getting the expression of : " + filterString + " for object " + obj, e);
			
			return Boolean.FALSE;
		}
		
		try {
			logger.info("obj : " + obj);
			logger.info("filterString : " + filterString);
			logger.info("filter expression : " + filterExpression);
			
			Boolean result = JSHelper.evaluate(filterExpression);
			
			logger.info("result : " + result);
			
			return result;
		} catch (Exception e) {
			logger.error("Error while evaluating expression : " + filterExpression, e);
			
			return Boolean.FALSE;
		}
	}

	public static String evaluatedFilter(Object obj, String filterString)
			throws JsonParseException, JsonMappingException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, IOException {
		List<String> variables = FilterParser.getVariables(filterString);

		logger.info("In filter : " + filterString + " found variables are : " + variables);
		
		String json = JSONConverter.convertToJSON(obj);
		
		logger.info("json of obj  : " + json);

		Map<String, String> varValues = new HashMap<String, String>();
		for (String var : variables) {
			String value = ObjectHelper.getProperty(json, var).toString();
			logger.info("value of variable " + var + " : " + value);
			varValues.put(var, value);
		}

		String newQuery = FilterParser.replaceVariables(varValues, filterString);

		return newQuery;
	}
	
	
	static class FilterParser {
		private static final String VAR_DELIM = "$";
		private static final String VAR_DELIM_EXP = "\\" + VAR_DELIM;
		private static final String VALUE_QUOTES = "\"";

		private static final String variablesRegex = VAR_DELIM_EXP + "(\\S+)" + VAR_DELIM_EXP;
		private static final Pattern patternVariables = Pattern.compile(variablesRegex);
		

		public static List<String> getVariables(String filterString) {
			List<String> variables = new ArrayList<String>();

			Matcher m = patternVariables.matcher(filterString);

			while (m.find())
				variables.add(m.group(1).trim());

			return variables;
		}
		
		public static String replaceVariables(Map<String, String> variables,
				String filterString) {
			String replacedFilter = filterString;

			for (Map.Entry<String, String> var : variables.entrySet()) {
				String varName = VAR_DELIM_EXP + var.getKey() + VAR_DELIM_EXP;
				String varValue = VALUE_QUOTES + var.getValue() + VALUE_QUOTES;

				replacedFilter = replacedFilter.replaceAll(varName, varValue);
			}

			return replacedFilter;
		}
	}
}