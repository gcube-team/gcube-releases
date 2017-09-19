/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.expression.Expression;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 28, 2014
 *
 */
public class CacheServerExpressions implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	Map<String, Expression> cache = new HashMap<String, Expression>();
	
	/**
	 * 
	 */
	public CacheServerExpressions() {
	}
	
	public void addExpression(Expression expression){
		
		cache.put(expression.toString(), expression);
	}
	
	/**
	 * 
	 * @param expressionToString
	 * @return
	 */
	public Expression getExpression(String expressionToString){
		return cache.get(expressionToString);
		
	}
	
	public List<String> keys(){
		return new ArrayList<String>(cache.keySet());
	}
	
	/**
	 * @return the cache
	 */
	public Map<String, Expression> getCache() {
		return cache;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CacheServerExpressions [cache=");
		builder.append(cache);
		builder.append("]");
		return builder.toString();
	}
}
