/**
 * 
 */
package org.gcube.portlets.user.tdw.client.util;

import java.util.Comparator;

import org.gcube.portlets.user.tdw.shared.model.ColumnDefinition;

/**
 * Compares the {@link ColumnDefinition} by position field.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ColumnPositionComparator implements Comparator<ColumnDefinition> {

	protected boolean noPositionLast;
	
	/**
	 * @param noPositionLast
	 */
	public ColumnPositionComparator(boolean noPositionLast) {
		this.noPositionLast = noPositionLast;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compare(ColumnDefinition c1, ColumnDefinition c2) {
		if (c1.getPosition() == c2.getPosition()) return 0;
		if (noPositionLast && c1.getPosition()<0) return 1;
		if (noPositionLast && c2.getPosition()<0) return -1;
		if (c1.getPosition()<c2.getPosition()) return -1;
		if (c1.getPosition()>c2.getPosition()) return 1;
		return 0;
	}

}
