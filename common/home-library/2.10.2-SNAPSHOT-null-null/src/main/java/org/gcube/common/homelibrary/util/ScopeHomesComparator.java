/**
 * 
 */
package org.gcube.common.homelibrary.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ScopeHomesComparator extends IndentedVisitor{
	
	protected boolean verbose = true;
	protected boolean failOnWorkspaceDifference = false;
	
	/**
	 * @param verbose
	 */
	public ScopeHomesComparator() {
	}

	/**
	 * @param verbose <code>true</code> to generate verbose message, <code>false</code> otherwise.
	 */
	public ScopeHomesComparator(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * @param scope1 first scope.
	 * @param scope2 second scope.
	 * @param factory home manager factory.
	 * @return <code>true</code> if are equals, <code>false</code> otherwise.
	 * @throws InternalErrorException if an error occurs.
	 * @throws WorkspaceFolderNotFoundException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 */
	public boolean compareScopes(String scope1, String scope2, HomeManagerFactory factory) throws InternalErrorException, WorkspaceFolderNotFoundException, HomeNotFoundException
	{
		HomeManager manager1 = factory.getHomeManager();
		HomeManager manager2 = factory.getHomeManager();
		return compareHomeManager(manager1, manager2);
	}
	
	/**
	 * @param manager1 the first home manager.
	 * @param manager2 the second home manager.
	 * @return <code>true</code> if are equals, <code>false</code> otherwise.
	 * @throws InternalErrorException if an error occurs.
	 * @throws WorkspaceFolderNotFoundException if an error occurs.
	 * @throws HomeNotFoundException if an error occurs.
	 */
	public boolean compareHomeManager(HomeManager manager1, HomeManager manager2) throws InternalErrorException, WorkspaceFolderNotFoundException, HomeNotFoundException
	{
		List<User> users1 = manager1.getUsers();
		List<User> users2 = manager2.getUsers();
		
		boolean sameCardinality = users1.size() == users2.size();
		
		Map<String, Workspace> user_wa1 = new LinkedHashMap<String, Workspace>();
		for (User user:users1){
			String login = user.getPortalLogin();
			Workspace wa = manager1.getHome(user).getWorkspace();
			user_wa1.put(login, wa);
		}
		
		Map<String, Workspace> user_wa2 = new LinkedHashMap<String, Workspace>();
		for (User user:users2){
			String login = user.getPortalLogin();
			Workspace wa = manager2.getHome(user).getWorkspace();
			user_wa2.put(login, wa);
		}
		
		boolean equals = true;
		
		for (Map.Entry<String, Workspace> entry:user_wa1.entrySet()){
			if (user_wa2.containsKey(entry.getKey())){
				if (verbose) println(entry.getKey());
				
				Workspace wa1 = entry.getValue();
				Workspace wa2 = user_wa2.get(entry.getKey());
				
				indent();
				WorkspaceFolder root1 = wa1.getRoot();
				WorkspaceFolder root2 = wa2.getRoot();
				boolean compare = compareWorkspaceItem(root1, root2);
				equals &= compare;
				outdent();
				
				println(entry.getKey()+" >> "+compare);
			}
			else {
				println(entry.getKey()+": not found in scope "+manager2 +" skipping");
				equals = false;
			}
			if (!equals && failOnWorkspaceDifference) return false;
		}
		
		return equals && sameCardinality;
		
	}
	
	/**
	 * @param item1 first workspace item.
	 * @param item2 second workspace item.
	 * @return <code>true</code> if are equals, <code>false</code> otherwise.
	 * @throws InternalErrorException if an error occurs.
	 */
	public boolean compareWorkspaceItem(WorkspaceItem item1, WorkspaceItem item2) throws InternalErrorException
	{
		if (verbose) indent();
		
		boolean equals = true;
		
		equals &= item1.getName().equals(item2.getName());
		equals &= item1.getType().equals(item2.getType());
		
		if (verbose) println(equals+" : "+item1.getName());
		
		Map<String, WorkspaceItem> children1 = new LinkedHashMap<String, WorkspaceItem>();
		for (WorkspaceItem child:item1.getChildren()){
			children1.put(child.getName(), child);
		}
		
		Map<String, WorkspaceItem> children2 = new LinkedHashMap<String, WorkspaceItem>();
		for (WorkspaceItem child:item2.getChildren()){
			children2.put(child.getName(), child);
		}
		
		for (Map.Entry<String, WorkspaceItem> entry:children1.entrySet()){
			if (children2.containsKey(entry.getKey())){
				WorkspaceItem child2 = children2.get(entry.getKey());
				
				equals &= compareWorkspaceItem(entry.getValue(), child2);
			} else {
				equals = false;
				if (verbose) println("not found : "+entry.getKey());
				//for (String key:children2.keySet()) println("key: "+key);
			}
			
			if (!equals && failOnWorkspaceDifference) break;
		}
		

		if (verbose) outdent();
	
		return equals;
	}
	
	
	

}
