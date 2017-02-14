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
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
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


public class GetChildren {
	private static final String nameResource 				= "HomeLibraryRepository";
	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {

		try {
			oak();
		} catch (UnknownHostException | RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}





	public static void oak() throws UnknownHostException, LoginException, RepositoryException {
		DocumentNodeStore ns = null;
		Session session = null;
		try{
			
			
			DB db = new MongoClient("ws-repo-mongo-d.d4science.org", 27017).getDB("jackrabbit-preprod");
//System.out.println(db.getName());
			ns = new DocumentMK.Builder().
					setMongoDB(db).getNodeStore();

			Repository repo = new Jcr(new org.apache.jackrabbit.oak.Oak(ns)).createRepository();

//					String user = "workspacerep.imarine";						
//					String pass = "gcube2010*onan";

			String user = "valentina.marioli";
			char[] pass = "39c4e6f9fcef359428e15cdbcbfc6df8".toCharArray();
			session = repo.login(
					new SimpleCredentials(user, pass));
//			System.out.println(session.getUserID());
			
//			session.getRootNode().getNode("Share/2a6a4276-7aa5-495f-9d82-064d8edf1111").remove();
//			session.save();
		
//			String absPath = "/Share/332a07c0-664f-4c9f-bb6a-71de64a8b81b/proposal";
//			System.out.println(session.getNode(absPath).getPath());

			getChildren(session.getNode("/Home/valentina.marioli/Workspace"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			if (session!=null)
				session.logout();
			if (ns!=null)
				ns.dispose();
			
			System.out.println("Close");
			System.exit(0);
		}
	}



	private static void getChildren(Node node) throws RepositoryException {
	
		if (!node.getName().contains(":")){
			System.out.println(node.getPath());
			NodeIterator children = node.getNodes();
			while(children.hasNext()){
				Node child = children.nextNode();
				getChildren(child);
			}
		}

	}


}
