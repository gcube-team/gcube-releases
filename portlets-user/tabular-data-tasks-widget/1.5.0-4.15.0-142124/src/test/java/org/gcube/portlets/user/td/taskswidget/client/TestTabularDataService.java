/**
 *
 */
package org.gcube.portlets.user.td.taskswidget.client;

import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Dec 4, 2013
 *
 */
public class TestTabularDataService {



	private static String username = "test.user";
	private static String scope = "/gcube/devsec/devVRE";

	public static void main(String[] args) throws Throwable {

		try{

			ScopeProvider.instance.set(scope);
			System.out.println("Start");
			TabularDataService service = TabularDataServiceFactory.getService();
			for (TabularResource tr : service.getTabularResources())
				System.out.println(tr);

			List<Task> listTask = service.getTasks(new TabularResourceId(42));
			for (Task task : listTask) {
				System.out.println(task);
			}

			System.out.println("End");
		}catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}

	}


}
