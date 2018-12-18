package org.gcube.portlets.user.performfish.util.comparators;

import java.util.Comparator;

import org.gcube.portlets.user.performfish.bean.Association;

	public class AssociationComparator implements Comparator<Association> {
		@Override
		public int compare(Association o1, Association o2) {
			return o1.getShortName().compareTo(o2.getShortName());
		}
	}


