package org.gcube.datacatalogue.grsf_manage_widget.client;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datacatalogue.grsf_manage_widget.server.manage.GenericResourceReaderExtras;

public class TestClass {

	//@Test
	public void test() {
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		GenericResourceReaderExtras entries = new GenericResourceReaderExtras();
		System.out.println(entries.getLookedUpExtrasKeys());
	}

}
