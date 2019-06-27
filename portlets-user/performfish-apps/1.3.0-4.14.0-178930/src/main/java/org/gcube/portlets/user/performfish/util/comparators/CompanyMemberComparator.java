package org.gcube.portlets.user.performfish.util.comparators;

import java.util.Comparator;

import org.gcube.portlets.user.performfish.bean.CompanyMember;

	public class CompanyMemberComparator implements Comparator<CompanyMember> {
		@Override
		public int compare(CompanyMember o1, CompanyMember o2) {
			return o1.getUser().getFullname().compareTo(o2.getUser().getFullname());
		}
	}


