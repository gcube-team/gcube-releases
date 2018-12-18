package org.gcube.portlets.user.performfish.util.comparators;

import java.util.Comparator;

import org.gcube.portlets.user.performfish.bean.Farm;

	public class FarmComparator implements Comparator<Farm> {
		@Override
		public int compare(Farm o1, Farm o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}


