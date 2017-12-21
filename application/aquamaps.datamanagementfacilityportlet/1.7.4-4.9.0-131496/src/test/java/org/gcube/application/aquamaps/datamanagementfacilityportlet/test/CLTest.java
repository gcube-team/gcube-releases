package org.gcube.application.aquamaps.datamanagementfacilityportlet.test;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.rmi.RemoteException;

import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DataManagement;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.MetaSourceFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.OrderDirection;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.SystemTable;
import org.gcube.common.scope.api.ScopeProvider;

public class CLTest {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws RemoteException 
	 */
	public static void main(String[] args) throws RemoteException, Exception {
		ScopeProvider.instance.set("/gcube/devsec/devVRE");
		DataManagement dm=dataManagement().build();
		System.out.println(dm.getJSONView(
				new PagedRequestSettings(2, 0, MetaSourceFields.searchid+"", OrderDirection.DESC),
				dm.getSystemTableName(SystemTable.DATASOURCES_METADATA),null));

	}

}
