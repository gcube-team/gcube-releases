package org.apache.jackrabbit.j2ee.workspacemanager.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.j2ee.workspacemanager.servlets.acl.GetDeniedMap;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

	public static final String PATH_SEPARATOR 					= "/";
	public static final String HOME_FOLDER 						= "Home";
	public static final String SHARED_FOLDER					= "Share";	
	public static final String HL_NAMESPACE						= "hl:";
	public static final String JCR_NAMESPACE					= "jcr:";
	public static final String REP_NAMESPACE					= "rep:";
	public static final String NT_WORKSPACE_FOLDER 				= "nthl:workspaceItem";
	public static final String NT_WORKSPACE_SHARED_FOLDER		= "nthl:workspaceSharedItem";

	private static Logger logger = LoggerFactory.getLogger(Utils.class);

	public static String isValidSearchResult(Node node, String login) throws RepositoryException {
		return isValidSearchResult(node, login, true);
	}


	public static String isValidSearchResult(Node node, String login, boolean includeFolder) throws RepositoryException {

		if (!includeFolder)
			if (node.getPrimaryNodeType().getName().equals(Utils.NT_WORKSPACE_FOLDER) || node.getPrimaryNodeType().getName().equals(Utils.NT_WORKSPACE_SHARED_FOLDER))
				return null;

		String sharePath = PATH_SEPARATOR + SHARED_FOLDER;
		String userPath = PATH_SEPARATOR + HOME_FOLDER + PATH_SEPARATOR + login;

		try {
			String nodePath = node.getPath();
			if (nodePath.startsWith(userPath)){
				//			System.out.println("*** userPath " + nodePath );
				return node.getProperty(NodeProperty.TITLE.toString()).getString();
			}

			if (nodePath.startsWith(sharePath)) {
				//		System.out.println("*** sharePath");
				Node sharedNode = (Node) node.getAncestor(2);

				if (node.getPath().equals(sharedNode.getPath())) {
					Node usersNode = sharedNode.getNode(NodeProperty.USERS.toString());
					String prop = (usersNode.getProperty(login)).getValue().getString();
					String[] value = prop.split(PATH_SEPARATOR);
					//								System.out.println(value[1] + " " + node.getPath());
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
		Map<String, List<String>> map = null;

		try{
			map = new HashMap<String, List<String>>();

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
		} catch (Exception e) {
			logger.error("Impossible to get ACL map");

		}
		return map;
	}



}
