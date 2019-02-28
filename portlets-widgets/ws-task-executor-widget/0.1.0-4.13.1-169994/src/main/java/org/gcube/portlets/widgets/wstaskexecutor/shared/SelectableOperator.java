/**
 *
 */
package org.gcube.portlets.widgets.wstaskexecutor.shared;

import java.io.Serializable;
import java.util.Arrays;

import org.gcube.common.workspacetaskexecutor.shared.FilterOperator;
import org.gcube.common.workspacetaskexecutor.shared.TaskOperator;


/**
 * The Class SelectableOperator.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 12, 2018
 */
public class SelectableOperator implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -736347344123058207L;

	private String[] filterForParameterTypes;
	private FilterOperator filterOperator;


	/**
	 * Instantiates a new selectable operator.
	 */
	public SelectableOperator() {
	}


	/**
	 * Instantiates a new selectable operator.
	 *
	 * @param filterForParameterTypes the filter for parameter types. It returns only {@link TaskOperator} matching the input filters
	 * @param filterOperator the filter operator
	 */
	public SelectableOperator(
		String[] filterForParameterTypes, FilterOperator filterOperator) {

		super();
		this.filterForParameterTypes = filterForParameterTypes;
		this.filterOperator = filterOperator;
	}




	/**
	 * Gets the filter for parameter types.
	 *
	 * @return the filterForParameterTypes
	 */
	public String[] getFilterForParameterTypes() {

		return filterForParameterTypes;
	}



	/**
	 * Gets the filter operator.
	 *
	 * @return the filterOperator
	 */
	public FilterOperator getFilterOperator() {

		return filterOperator;
	}



	/**
	 * Sets the filter for parameter types.
	 *
	 * @param filterForParameterTypes the filterForParameterTypes to set
	 */
	public void setFilterForParameterTypes(String[] filterForParameterTypes) {

		this.filterForParameterTypes = filterForParameterTypes;
	}



	/**
	 * Sets the filter operator.
	 *
	 * @param filterOperator the filterOperator to set
	 */
	public void setFilterOperator(FilterOperator filterOperator) {

		this.filterOperator = filterOperator;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("SelectableOperator [filterForParameterTypes=");
		builder.append(Arrays.toString(filterForParameterTypes));
		builder.append(", filterOperator=");
		builder.append(filterOperator);
		builder.append("]");
		return builder.toString();
	}

}
