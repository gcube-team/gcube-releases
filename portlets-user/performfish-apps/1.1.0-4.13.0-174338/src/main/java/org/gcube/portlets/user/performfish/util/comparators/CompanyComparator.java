package org.gcube.portlets.user.performfish.util.comparators;

import java.util.Comparator;

import org.gcube.portlets.user.performfish.bean.Company;

	public class CompanyComparator implements Comparator<Company> {
		@Override
		public int compare(Company o1, Company o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}


