/**
 * 
 */
package org.gcube.portlets.user.workspace;

import java.util.List;

import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Test;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * May 15, 2014
 * 
 */
public class TestWithTime {

	public static String DEFAULT_SCOPE = "/gcube/devsec"; //DEV
	public static String TEST_USER = "fabio.simeoni";
	
	@Test
	public void accessTest() throws Exception {

		ScopeProvider.instance.set(DEFAULT_SCOPE);
		System.out.println("Start Test Simeoni");
		long time = System.currentTimeMillis();

		Workspace ws = HomeLibrary.getUserWorkspace(TEST_USER);

		for (WorkspaceItem item : ws.getRoot().getChildren())
			item.getProperties().getProperties().keySet();

		long diff = System.currentTimeMillis()-time;
		System.out.println("End Test Simeoni: "+diff);
	}
	
	@Test
	public void accessTest1() throws Exception {
		
		ScopeProvider.instance.set(DEFAULT_SCOPE);
		
		System.out.println("Start Test");
		long time = System.currentTimeMillis();
		long diff;
		
		System.out.println("Get worskpace");
		Workspace ws = HomeLibrary.getUserWorkspace(TEST_USER);
		diff = System.currentTimeMillis()-time;
		System.out.println("Worskapce returned in: "+diff);

		System.out.println("Get Root");
		WorkspaceFolder root = ws.getRoot();
		diff = System.currentTimeMillis()-time;
		System.out.println("Root returned in: "+diff);
		
		System.out.println("Get Children");
		List<WorkspaceItem> children = root.getChildren();
		diff = System.currentTimeMillis()-time;
		System.out.println("Get Children returned in: "+diff);
		
		System.out.println("Children size is: "+children.size());
		for (WorkspaceItem item : children){
			diff = System.currentTimeMillis()-time;
			System.out.println("Item: +"+item.getId()+", keyset: "+item.getProperties().getProperties().keySet() + ", Diff is: "+diff);
		}
		diff = System.currentTimeMillis()-time;
		System.out.println("Children properties returned in: "+diff);

		diff = System.currentTimeMillis()-time;
		System.out.println("End test: "+diff);
	}
}
