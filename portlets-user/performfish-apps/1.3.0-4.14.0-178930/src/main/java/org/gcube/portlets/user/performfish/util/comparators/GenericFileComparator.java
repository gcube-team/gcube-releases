package org.gcube.portlets.user.performfish.util.comparators;

import java.util.Comparator;

import org.gcube.common.storagehub.model.items.GenericFileItem;

public class GenericFileComparator implements Comparator<GenericFileItem> {
	@Override
	public int compare(GenericFileItem o1, GenericFileItem o2) {
		return o1.getName().compareTo(o2.getName());
	}
}
