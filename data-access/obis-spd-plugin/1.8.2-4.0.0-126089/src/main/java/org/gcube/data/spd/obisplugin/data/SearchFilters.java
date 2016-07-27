/**
 * 
 */
package org.gcube.data.spd.obisplugin.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.data.spd.model.Condition;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SearchFilters {

	protected List<Condition> conditions;
	
	protected SearchFilters(){}
	
	public SearchFilters(Condition ... conditions){
		this.conditions = new ArrayList<Condition>(Arrays.asList(conditions));
	}
	
	public void addCondition(Condition condition)
	{
		conditions.add(condition);
	}

	/**
	 * @return the conditions
	 */
	public List<Condition> getConditions() {
		return conditions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SearchFilters [conditions=");
		builder.append(conditions);
		builder.append("]");
		return builder.toString();
	}
	
}
