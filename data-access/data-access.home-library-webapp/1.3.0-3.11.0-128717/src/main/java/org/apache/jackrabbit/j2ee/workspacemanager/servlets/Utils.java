package org.apache.jackrabbit.j2ee.workspacemanager.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.Privilege;

import org.gcube.common.homelibary.model.items.type.NodeProperty;

public class Utils {

	public static final String PATH_SEPARATOR 					= "/";
	public static final String HOME_FOLDER 						= "Home";
	public static final String SHARED_FOLDER					= "Share";	
	public static final String HL_NAMESPACE						= "hl:";
	public static final String JCR_NAMESPACE					= "jcr:";
	public static final String REP_NAMESPACE					= "rep:";
	public static final String NT_WORKSPACE_FOLDER 				= "nthl:workspaceItem";
	public static final String NT_WORKSPACE_SHARED_FOLDER		= "nthl:workspaceSharedItem";
	
	public static String isValidSearchResult(Node node, String login) {

		String sharePath = PATH_SEPARATOR + SHARED_FOLDER;
		String userPath = PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR + login;

		try {
			String nodePath = node.getPath();
			if (nodePath.startsWith(userPath)){
				//				System.out.println("*** userPath");
				return node.getProperty(NodeProperty.TITLE.toString()).getString();
			}

			if (nodePath.startsWith(sharePath)) {
				//				System.out.println("*** sharePath");
				Node sharedNode = (Node) node.getAncestor(2);

				if (node.getPath().equals(sharedNode.getPath())) {
					Node usersNode = sharedNode.getNode(NodeProperty.USERS.toString());
					String prop = (usersNode.getProperty(login)).getValue().getString();
					String[] value = prop.split(PATH_SEPARATOR);
					//					System.out.println("prop " + value[1]);
					return value[1];
				}				
				else 
					return node.getName();

			}	
			return null;
		} catch (RepositoryException e) {
			return null;
		}
	}
	
	public static  Map<String, List<String>> getMap(List<AccessControlEntry> allEntries) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();

		//		System.out.println("entry size " + allEntries.size());
		for (AccessControlEntry entry: allEntries){	

			List<String> privilegesList = null;

			String key = entry.getPrincipal().getName();

			if (!key.equals("everyone")){
				try{			
					privilegesList = map.get(key);
					//					System.out.println("privilegesList size " + privilegesList.size() );
				}catch (Exception e) {
					//					System.out.println("key: "+ key + ", does not exist yet");
				}

				Privilege[] privileges = entry.getPrivileges();
				for (int i=0; i< privileges.length; i++){
					//					System.out.println("* "+ i + ")" + privileges[i].getName());
					if (privilegesList==null)
						privilegesList = new ArrayList<String>();
					privilegesList.add(privileges[i].getName());

				}
				map.put(entry.getPrincipal().getName(), privilegesList);	
			}
		}    			
		return map;
	}
	
}
