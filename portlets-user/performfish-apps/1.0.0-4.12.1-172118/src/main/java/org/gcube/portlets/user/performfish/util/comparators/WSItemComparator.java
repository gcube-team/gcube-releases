package org.gcube.portlets.user.performfish.util.comparators;

import java.util.Comparator;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;

	public class WSItemComparator implements Comparator<WorkspaceItem> {
		@Override
		public int compare(WorkspaceItem o1, WorkspaceItem o2) {
			try {
				return o1.getName().compareTo(o2.getName());
			} catch (InternalErrorException e) {
				e.printStackTrace();
			}
			return 0;
		}
	}


