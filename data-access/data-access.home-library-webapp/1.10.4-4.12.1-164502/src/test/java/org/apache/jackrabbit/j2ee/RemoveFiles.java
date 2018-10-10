package org.apache.jackrabbit.j2ee;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.derby.tools.sysinfo;
import org.apache.jackrabbit.j2ee.accessmanager.AccessControlUtil;
import org.apache.jackrabbit.j2ee.workspacemanager.servlets.acl.JCRAccessControlManager;
import org.apache.jackrabbit.j2ee.workspacemanager.session.SessionManager;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.homelibary.model.acl.AccessRights;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
import org.gcube.common.homelibrary.home.workspace.folder.items.File;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.scope.api.ScopeProvider;


public class RemoveFiles {
	static JCRWorkspace ws = null;

	private static  String portalLogin = "valentina.marioli";
	private static Session session;
	private static String fileInput = "/home/valentina/Downloads/output_share_folder.csv";
	//	private static String fileOutput= "/home/valentina/Downloads/output_share_folder.csv";
	private static final String NAME = "ISExporter";
	private static int found =0;
	private static int i=0;

	private static FileWriter fileWriter;
	private static CSVPrinter csvFilePrinter;


	public static void main(String[] args) throws InternalErrorException, HomeNotFoundException, UserNotFoundException, InsufficientPrivilegesException, WorkspaceFolderNotFoundException, ItemAlreadyExistException, WrongDestinationException, ItemNotFoundException, IOException, PathNotFoundException, InterruptedException {
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");

		try {
			String url = "http://workspace-repository.dev.d4science.org/home-library-webapp";

			URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
			String admin = "workspacerep.imarine";
			String pass = null;

			session = repository.login( 
					new SimpleCredentials(admin, pass.toCharArray()));


			//			Node node = session.getNode("/Home/debhasish.bhakta/Workspace/GISVRE5/stat_algo.project");
			//			System.out.println(node.getPath());
			//			
			//			node.remove();
			//			session.save();


			//System.out.println("home is " + home.getIdentifier());
			Node node = session.getNode("/Home/andrea.rossi/Workspace/mytestsubfolder");
			System.out.println("Node at path " + node.getPath());
			node.remove();
			session.save();

			//			PropertyIterator properties = node.getProperties();
			//
			//			//
			//
			//			while (properties.hasNext()) {
			//				Property props = (Property) properties.next();
			//				if(!props.isMultiple()){
			//					System.out.println("PROP [SINGLE] Name is " + props.getName() + " and value is " + props.getValue());
			//				}
			//				else{
			//					Value[] values = props.getValues();
			//					for (Value value : values) {
			//						System.out.println("PROP Name is " + props.getName() + " and value is " + value);
			//					}
			//				}
			//			}
			//
			//			//
			//			NodeIterator childrenFolder = node.getNodes(); // get folder nodes
			//
			//			while (childrenFolder.hasNext()) {
			//				Node child = (Node) childrenFolder.next();
			//				System.out.println("Node name is " + child.getName());
			//
			//				//				NodeIterator children = child.getNodes();
			//				//
			//				//				while (children.hasNext()) {
			//				//					Node object = (Node) children.next();
			//
			//
			//				PropertyIterator properties1 = child.getProperties();
			//
			//				//
			//				//				if(child.getName().equals("hl:users"))
			//				while (properties1.hasNext()) {
			//					Property props = (Property) properties1.next();
			//					if(!props.isMultiple()){
			//						System.out.println("PROP [SINGLE] Name is " + props.getName() + " and value is " + props.getValue());
			//					}
			//					else{
			//						Value[] values = props.getValues();
			//						for (Value value : values) {
			//							System.out.println("[MUL] PROP Name is " + props.getName() + " and value is " + value);
			//						}
			//					}
			//				}
			//
			//			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (session!=null)
				session.logout();
		}

	}

	/**
	 * Get EACL map
	 * @param absPath
	 * @return
	 * @throws Exception
	 */
	public static Map<String, List<String>> getEACL(String absPath) throws Exception {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		Map<String, AccessRights> acl = AccessControlUtil.getEACL(absPath, session);
		Set<String> keys = acl.keySet();
		for (String principal: keys){
			if (!acl.get(principal).getGranted().isEmpty()){
				map.put(principal, acl.get(principal).getGranted());	
			}
		}
		return map;
	}

	public static Map<String, List<String>> getACL(String absPath) throws Exception {
		//		System.out.println("get acl " + absPath);
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		Map<String, AccessRights> acl = AccessControlUtil.getACL(absPath, session);
		Set<String> keys = acl.keySet();
		for (String principal: keys){
			if (!acl.get(principal).getGranted().isEmpty())
				map.put(principal, acl.get(principal).getGranted());				
		}
		return map;
	}

	private static void check(Session session) throws FileNotFoundException, IOException {




		try {

			Reader in = new FileReader(fileInput);
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader("jcr_id","owner", "created", "lastModified","jcr_path").parse(in);

			for (CSVRecord record : records) {
				String jcr_path = record.get("jcr_path");


				if (jcr_path.equals(""))


					try{
						System.out.println("*** "+ jcr_path);
						String jcr_id = record.get("jcr_id");
						//					System.out.println(jcr_id);
						Node node = session.getNodeByIdentifier(jcr_id).getParent();
						System.out.println(node.getPath());

						//					node.remove();
						//					session.save();

					} catch (Exception e) {

						System.out.println("ID not foud " + jcr_path);
						//					e.printStackTrace();
					}

			}

		} catch (Exception e) {

			e.printStackTrace();
		} finally {

		}
		System.out.println("found " + found + "/" + i);
	}





}




