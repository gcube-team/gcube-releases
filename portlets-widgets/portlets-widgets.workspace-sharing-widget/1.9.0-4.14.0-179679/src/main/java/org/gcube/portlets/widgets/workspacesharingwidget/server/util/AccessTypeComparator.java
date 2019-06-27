/**
 * 
 */
package org.gcube.portlets.widgets.workspacesharingwidget.server.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.storagehub.model.acls.AccessType;

/**
 * The Class AclTypeComparator.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it Mar 5, 2015
 */
public class AccessTypeComparator implements Comparator<AccessType> {

	public static final Map<AccessType, Integer> aclTypeOrder;
	static {
		aclTypeOrder = new LinkedHashMap<AccessType, Integer>();
		aclTypeOrder.put(AccessType.READ_ONLY, 0);
		aclTypeOrder.put(AccessType.WRITE_OWNER, 1);
		aclTypeOrder.put(AccessType.WRITE_ALL, 2);
		// aclTypeOrder.put(ACLType.ADMINISTRATOR, 3);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(AccessType o1, AccessType o2) {
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;

		Integer order1 = aclTypeOrder.get(o1);
		Integer order2 = aclTypeOrder.get(o2);

		if (order1 == null)
			return -1;

		if (order2 == null)
			return 1;

		if (order1 == order2)
			return 0;

		return order1 < order2 ? -1 : 1;
	}

	/**
	 * Gets the allowed.
	 *
	 * @param accessType
	 *            the acl type to compare
	 * @param includeEqual
	 *            if true include equal ACLType, no otherwise
	 * @return the allowed
	 */
	public List<AccessType> getAllowed(AccessType accessType, boolean includeEqual) {

		List<AccessType> allowed = new ArrayList<>();

		if (accessType == null)
			return allowed;

		// IF MAP DOES NOT CONTAINS ACT TYPE IT IS NOT COMPARABLE
		if (aclTypeOrder.get(accessType) == null)
			return allowed;

		for (AccessType aMap : aclTypeOrder.keySet()) {
			int comparator = compare(aMap, accessType);
			if (comparator == 1)
				allowed.add(aMap);
			else if (includeEqual && comparator == 0)
				allowed.add(aMap);
		}

		return allowed;
	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {

		AccessTypeComparator comparator = new AccessTypeComparator();

		List<AccessType> allowed = comparator.getAllowed(AccessType.WRITE_ALL, false);

		System.out.println(allowed);

	}

}
