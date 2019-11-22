package org.gcube.portlets.user.performfish.util.comparators;

import java.util.Comparator;

import org.gcube.vomanagement.usermanagement.model.GCubeTeam;

	public class TeamComparator implements Comparator<GCubeTeam> {
		@Override
		public int compare(GCubeTeam o1, GCubeTeam o2) {
			return o1.getTeamName().compareTo(o2.getTeamName());
		}
	}


