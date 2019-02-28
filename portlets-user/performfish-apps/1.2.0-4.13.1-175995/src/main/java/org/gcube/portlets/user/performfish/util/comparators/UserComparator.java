package org.gcube.portlets.user.performfish.util.comparators;

import java.util.Comparator;

import org.gcube.vomanagement.usermanagement.model.GCubeUser;

	public class UserComparator implements Comparator<GCubeUser> {
		@Override
		public int compare(GCubeUser o1, GCubeUser o2) {
			return o1.getFullname().compareTo(o2.getFullname());
		}
	}


