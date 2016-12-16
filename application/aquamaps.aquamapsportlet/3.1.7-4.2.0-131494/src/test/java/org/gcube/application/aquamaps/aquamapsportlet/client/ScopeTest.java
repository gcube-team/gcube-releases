package org.gcube.application.aquamaps.aquamapsportlet.client;

import org.gcube.application.aquamaps.aquamapsportlet.servlet.utils.Utils;

public class ScopeTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(Utils.removeVRE("/gcube"));
		System.out.println(Utils.removeVRE("/gcube/devsec"));
		System.out.println(Utils.removeVRE("/gcube/devsec/devVRE"));
		

	}

}
