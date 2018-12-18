package org.gcube.portlets.user.performfish.util.comparators;

import java.util.Comparator;

import org.gcube.common.homelibary.model.versioning.WorkspaceVersion;

	public class FileVersionComparator implements Comparator<WorkspaceVersion> {
		@Override
		public int compare(WorkspaceVersion o1, WorkspaceVersion o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}


