/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.server.validator;

import org.gcube.portlets.user.tdtemplate.shared.TdTColumnCategory;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 1, 2014
 *
 */
public class ColumnCategoryConstraint {
	
	private TdTColumnCategory tdtColumn;
	private String constraintDescription;
	private ColumnOccurrenceComparator comparator;
	
	/**
	 * 
	 * @param tdtColumn
	 * @param minOccurrence
	 * @param maxOccurrence
	 * @param constraintDescription
	 */
	public ColumnCategoryConstraint(TdTColumnCategory tdtColumn, ColumnOccurrenceComparator comparator, String constraintDescription) {
		this.tdtColumn = tdtColumn;
		this.comparator = comparator;
		this.constraintDescription = constraintDescription;
	}

	public TdTColumnCategory getTdtColumn() {
		return tdtColumn;
	}

	public void setTdtColumn(TdTColumnCategory tdtColumn) {
		this.tdtColumn = tdtColumn;
	}

	public String getConstraintDescription() {
		return constraintDescription;
	}

	public void setConstraintDescription(String constraintDescription) {
		this.constraintDescription = constraintDescription;
	}

	public ColumnOccurrenceComparator getComparator() {
		return comparator;
	}


	public void setComparator(ColumnOccurrenceComparator comparator) {
		this.comparator = comparator;
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ColumnCategoryConstraint [tdtColumn=");
		builder.append(tdtColumn);
		builder.append(", constraintDescription=");
		builder.append(constraintDescription);
		builder.append(", comparator=");
		builder.append(comparator);
		builder.append("]");
		return builder.toString();
	}

	
}
