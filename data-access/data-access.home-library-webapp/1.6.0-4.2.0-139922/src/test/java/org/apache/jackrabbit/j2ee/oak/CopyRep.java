package org.apache.jackrabbit.j2ee.oak;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import org.apache.jackrabbit.oak.api.AuthInfo;
import org.apache.jackrabbit.oak.api.ContentSession;
import org.apache.jackrabbit.oak.api.QueryEngine;
import org.apache.jackrabbit.oak.api.Root;
import org.apache.jackrabbit.oak.api.Tree;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
import javax.jcr.ImportUUIDBehavior;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.Privilege;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.JackrabbitWorkspace;
import org.apache.jackrabbit.api.security.authorization.PrivilegeManager;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.accounting.AccoutingNodeWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.storage.GCUBEStorage;
import org.apache.jackrabbit.j2ee.workspacemanager.util.MetaInfo;
import org.apache.jackrabbit.j2ee.workspacemanager.util.Util;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentMK;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.apache.jackrabbit.oak.spi.security.privilege.PrivilegeConfiguration;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.type.ContentType;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibary.model.items.type.PrimaryNodeType;
import org.gcube.common.homelibary.model.util.WorkspaceItemAction;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.thoughtworks.xstream.XStream;


public class CopyRep {
	private static final String nameResource 				= "HomeLibraryRepository";
	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {



		String rootScope = "/gcube";


		ScopeProvider.instance.set(rootScope);

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

		try {
			ServiceEndpoint resource = resources.get(0);

			for (AccessPoint ap:resource.profile().accessPoints()) {

				if (ap.name().equals("JCR")) {

					//					String url = ap.address();

					String url = "http://ws-repo-test.d4science.org/home-library-webapp";
					//							url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-patched-2.4.3";
					String user = ap.username();						
					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());

					//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-2.8.0/";
					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					Session session = repository.login( 
							new SimpleCredentials(user, pass.toCharArray()));



					Node root = session.getRootNode();
					//					session.getRootNode().getNode("Share/21cc3f41-af12-446a-90a2-ee66b83fef48").remove();
					//					session.save();

					NodeIterator firstLevel = root.getNode("Home").getNodes();

					boolean flag = false;
					while(firstLevel.hasNext()){


						Node node = firstLevel.nextNode();
						System.out.println("* " + node.getPath());
						//						if (node.getPath().equals("/Share/fd0f27d8-1579-4290-9f65-5b4aa1a351cc"))
						flag = true;
						if (flag && !node.getName().contains(":") && (!node.getName().equals("deny"))){
							try{
								oak(session, node);
							}catch (Exception e) {
								e.printStackTrace();
							}
						}
						NodeIterator folders = node.getNodes();

						while(folders.hasNext()){
							Node folder = folders.nextNode();
							System.out.println("** " + folder.getPath());

							if (!folder.getName().contains(":") && (!folder.getName().equals("deny"))){
								try{
									oak(session, folder);
								}catch (Exception e) {
									e.printStackTrace();
								}
							}else
								continue;
						}
					}



					System.out.println("Done!");

				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}


	}





	public static void oak(Session session, Node node) throws Exception {
		DB db = new MongoClient("ws-repo-mongo-d.d4science.org", 27017).getDB("oak-test");

		DocumentNodeStore ns = new DocumentMK.Builder().
				setMongoDB(db).getNodeStore();

		Repository repo = new Jcr(new org.apache.jackrabbit.oak.Oak(ns)).createRepository();

		//		String user = "workspacerep.imarine";						
		//		String pass = "gcube2010*onan";

		String user = "admin";
		String pass = "admin";
		Session oakSession = repo.login(
				new SimpleCredentials(user, pass.toCharArray()));
		//		System.out.println("**");
		Node parent = oakSession.getNode(node.getParent().getPath());
		//		System.out.println(root.getPath());
		if (parent.hasNode(node.getName())) {
			Node hello = parent.getNode(node.getName());
			System.out.println("found the node " + hello.getPath());
		} else {

			System.out.println("adding " + node.getName() + " type " + node.getPrimaryNodeType().getName() + " to " + parent.getPath());
			String filename = UUID.randomUUID()+".xml";
			doExport(session, filename, node.getPath());
			doImport(oakSession, filename, node.getParent().getPath());

			//			oakSession.getWorkspace().copy(session.getWorkspace()., node.getPath(), node.getPath());
			//			parent.addNode(node.getName(),node.getPrimaryNodeType().getName());
		}
		//		oakSession.save();
		oakSession.save();
		oakSession.logout();
		// close DocumentNodeStore
		ns.dispose();
	}

	public static void doExport(Session session, String filepath, String repositorybasexpath) throws Exception {
		System.out.println("Export " + repositorybasexpath);
		File f = new File(filepath);
		if (f.exists()) {
			throw new IllegalArgumentException("Export file "+filepath+" is existing, can not export");
		}
		try {
			FileOutputStream os = new FileOutputStream(f);
			//export all including binary, recursive
			session.exportSystemView(repositorybasexpath, os, false, false);
			os.close();
		} catch(Throwable t) {
			throw new Exception("Failed to export repository at "+ repositorybasexpath +" to file "+filepath+"\n"+t.toString(), t);
		}
		System.out.println("Exported the repository to "+f);
	}


	public static void doImport(Session session, String filepath,String path) throws Exception {
		System.out.println("Import " + path);
		File f = new File(filepath);
		if (! f.exists()) {
			throw new IllegalArgumentException("File "+filepath+" not existing, can not import");
		}
		//		String path = config.getProperty("repository-base-xpath","/");
		try {
			//Clear repository first
			//			Node rootNode = session.getNode(path);
			//			NodeIterator nodeList = rootNode.getNodes(config.getProperty("purge-path", "*"));
			//			while (nodeList.hasNext()) {
			//				Node node = nodeList.nextNode();
			//				if (! (node.getName().equals("jcr:system")
			//						|| node.getName().equals("rep:policy") )
			//						) {
			//					node.remove();
			//				}
			//			}
			//			PropertyIterator propertyList = rootNode.getProperties();
			//			while (propertyList.hasNext()) {
			//				Property property = propertyList.nextProperty();
			//				if (! (property.getName().startsWith("jcr:")
			//						|| property.getName().startsWith("rep:")
			//						|| property.getName().startsWith("sling:"))
			//						) {
			//					property.remove();
			//				}
			//			}
			//			session.save();
			FileInputStream data = new FileInputStream(f);
			session.importXML(path, data, ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING);

		} catch(Throwable t) {
			//			System.out.println("Failed to import repository to "+ path +
			//					" from file "+filepath);
			throw new Exception("Failed to import repository to "+ path +
					" from file "+filepath+"\n"+t.toString(), t);
		}
		try {

			session.save();
		} catch(Throwable t) {
			//			System.out.println("Failed to save the imported repository: "+t.toString());
			throw new Exception("Failed to save the imported repository: "+t.toString(), t);
		}
		System.out.println("Imported the repository from "+f);
	}
}
