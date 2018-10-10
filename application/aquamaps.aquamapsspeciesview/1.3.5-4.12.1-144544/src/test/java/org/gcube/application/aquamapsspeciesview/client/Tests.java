package org.gcube.application.aquamapsspeciesview.client;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ExportOperation;
import org.gcube.application.aquamaps.aquamapsspeciesview.servlet.db.DBManager;
import org.gcube.common.scope.api.ScopeProvider;

public class Tests {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws RemoteException 
	 */
	public static void main(String[] args) throws RemoteException, Exception {
		String scope="/gcube/devsec/devVRE";
		ScopeProvider.instance.set(scope);
		System.out.println(dataManagement().withTimeout(2, TimeUnit.MINUTES).build().exportTableAsCSV("speciesoccursum",null,null,null,ExportOperation.TRANSFER));
		
		DBManager.getInstance(scope).fetchSpecies();
		
		System.out.println("Done");
	}

}
