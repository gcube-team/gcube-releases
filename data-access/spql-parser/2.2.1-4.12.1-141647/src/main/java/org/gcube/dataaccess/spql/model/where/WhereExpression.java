/**
 * 
 */
package org.gcube.dataaccess.spql.model.where;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataaccess.spql.model.CheckableElement;
import org.gcube.dataaccess.spql.model.error.QueryError;


/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class WhereExpression implements CheckableElement {
	
	protected List<Condition> conditions;

	/**
	 * @param conditions
	 */
	public WhereExpression(List<Condition> conditions) {
		this.conditions = conditions;
	}

	/**
	 * @return the conditions
	 */
	public List<Condition> getConditions() {
		return conditions;
	}

	/**
	 * @param conditions the conditions to set
	 */
	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	@Override
	public List<QueryError> check() {
		List<QueryError> errors = new ArrayList<QueryError>();
		for (Condition condition:conditions) errors.addAll(condition.check());
		return errors;
	}
	
	

}
