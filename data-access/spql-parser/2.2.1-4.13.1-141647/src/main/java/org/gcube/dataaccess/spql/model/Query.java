/**
 * 
 */
package org.gcube.dataaccess.spql.model;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataaccess.spql.model.error.QueryError;
import org.gcube.dataaccess.spql.model.having.HavingExpression;
import org.gcube.dataaccess.spql.model.ret.ReturnType;
import org.gcube.dataaccess.spql.model.where.WhereExpression;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class Query implements CheckableElement {
	
	protected List<Term> terms;
	protected List<String> datasources;
	protected WhereExpression whereExpression;
	protected ReturnType returnType;
	protected HavingExpression havingExpression;
	
	public Query()
	{
		terms = new ArrayList<Term>();
		datasources = new ArrayList<String>();
	}
	
	/**
	 * @return the terms
	 */
	public List<Term> getTerms() {
		return terms;
	}

	/**
	 * @param terms the terms to set
	 */
	public void setTerms(List<Term> terms) {
		this.terms = terms;
	}

	/**
	 * @return the datasources
	 */
	public List<String> getDatasources() {
		return datasources;
	}

	/**
	 * @param datasources the datasources to set
	 */
	public void setDatasources(List<String> datasources) {
		this.datasources = datasources;
	}


	/**
	 * @return the whereExpression
	 */
	public WhereExpression getWhereExpression() {
		return whereExpression;
	}

	/**
	 * @param whereExpression the whereExpression to set
	 */
	public void setWhereExpression(WhereExpression whereExpression) {
		this.whereExpression = whereExpression;
	}

	/**
	 * @return the returnType
	 */
	public ReturnType getReturnType() {
		return returnType;
	}

	/**
	 * @param returnType the returnType to set
	 */
	public void setReturnType(ReturnType returnType) {
		this.returnType = returnType;
	}

	/**
	 * @return the havingExpression
	 */
	public HavingExpression getHavingExpression() {
		return havingExpression;
	}

	/**
	 * @param havingExpression the havingExpression to set
	 */
	public void setHavingExpression(HavingExpression havingExpression) {
		this.havingExpression = havingExpression;
	}

	@Override
	public List<QueryError> check() {
		List<QueryError> errors = new ArrayList<QueryError>();
		if (whereExpression!=null) errors.addAll(whereExpression.check());
		return errors;
	}

}
