package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.accounting.AccoutingNodeWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.storage.GCUBEStorage;
import org.apache.jackrabbit.j2ee.workspacemanager.util.MetaInfo;
import org.apache.jackrabbit.j2ee.workspacemanager.util.Util;
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
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import com.thoughtworks.xstream.XStream;


public class SaveImg {
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
InputStream is = null;

		try {
			ServiceEndpoint resource = resources.get(0);

			for (AccessPoint ap:resource.profile().accessPoints()) {

				if (ap.name().equals("JCR")) {

					String url = ap.address();
					//							url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-patched-2.4.3";
					String user = ap.username();						
					String pass = StringEncrypter.getEncrypter().decrypt(ap.password());

					//		String url = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-webapp-2.8.0/";
					URLRemoteRepository repository = new URLRemoteRepository(url + "/rmi");
					Session session = repository.login( 
							new SimpleCredentials(user, pass.toCharArray()));

//				List<AccountingDelegate> list = getAccouting(session, "3d4787ba-8802-445e-a02e-cb4a15a0142b");
//				for(AccountingDelegate item: list){
//					System.out.println(item.getEntryType());
//				}
		

					ItemDelegate delegate = new ItemDelegate();
					delegate.setId(null);
					delegate.setName("Test.jpg");
					delegate.setTitle("Test.jpg");
					delegate.setDescription("desc");
					delegate.setLastModifiedBy(null);
					delegate.setParentId("cf6caaeb-09ba-48e3-9945-f22bbde9abe0");
					delegate.setParentPath(null);
					delegate.setLastModificationTime(null);
					delegate.setCreationTime(null);
					delegate.setProperties(null);
					//		delegate.setPath("/Home/valentina.marioli/Workspace/");
					delegate.setOwner("valentina.marioli");
					delegate.setPrimaryType(PrimaryNodeType.NT_WORKSPACE_IMAGE);
					delegate.setLastAction(WorkspaceItemAction.CREATED);

					Map<NodeProperty, String> content = new HashMap<NodeProperty, String>();
					content.put(NodeProperty.CONTENT, ContentType.IMAGE.toString());
					content.put(NodeProperty.FOLDER_ITEM_TYPE, FolderItemType.EXTERNAL_IMAGE.toString());
					content.put(NodeProperty.PORTAL_LOGIN, "valentina.marioli");

					//			content.put(NodeProperty.MIME_TYPE, info.getMimeType());
					//			Long l = Long.valueOf(String.valueOf(info.getSize()));
					//			content.put(NodeProperty.SIZE, new XStream().toXML(l));
					//			content.put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);

					delegate.setContent(content);
					
					
					is = new FileInputStream("/home/valentina/Downloads/i_3486313092015112116778.jpg");
					System.out.println(delegate.toString());
					
					
					String remotePath = session.getNodeByIdentifier(delegate.getParentId()).getPath() + "/" + "i_3486313092015112116778.jpg";

					
					
					MetaInfo metadata = null;
					if (is!=null){
						GCUBEStorage storage = new GCUBEStorage("valentina.marioli");					
						
						metadata = Util.getMetadataInfo(is, storage, remotePath, "i_3486313092015112116778.jpg");
						if (metadata.getStorageId()==null)
							throw new Exception("Inpustream not saved in storage.");
					}


					//set metadata
//					Map<NodeProperty, String> content = delegate.getContent();
					content.put(NodeProperty.MIME_TYPE, metadata.getMimeType());
					Long l = Long.valueOf(String.valueOf(metadata.getSize()));
					content.put(NodeProperty.SIZE, new XStream().toXML(l));
					content.put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);		
					delegate.setContent(content);
					

					ItemDelegateWrapper wrapper = new ItemDelegateWrapper(delegate, delegate.getOwner());
					wrapper.save(session);

			
//					AccountingDelegate delegate = new AccountingDelegate();
//					delegate.setId("45e15eed-2779-42f0-9056-f5571db43b93");
//					delegate.setUser("valentina.marioli");
//					delegate.setDate(Calendar.getInstance());
//					delegate.setEntryType(AccountingEntryType.REMOVAL);
//					Map<AccountingProperty, String> accountingProperties = new HashMap<AccountingProperty, String>();
//					accountingProperties.put(AccountingProperty.FOLDER_ITEM_TYPE, new XStream().toXML(FolderItemType.EXTERNAL_IMAGE));
//					accountingProperties.put(AccountingProperty.ITEM_NAME, new XStream().toXML("2893036344_f51fb1c5a3_z.jpg"));
//					accountingProperties.put(AccountingProperty.ITEM_TYPE, new XStream().toXML(WorkspaceItemType.FOLDER_ITEM));
//					accountingProperties.put(AccountingProperty.MIME_TYPE, new XStream().toXML("image/jpeg"));
//
//					delegate.setAccountingProperties(accountingProperties);
//
//
//					System.out.println("item delegate: " + delegate);
//					AccountingDelegateWrapper wrapper = new AccountingDelegateWrapper(delegate, "");
//					wrapper.save(session);
					//					System.out.println("NEW ITEM: " + delegate.getPath());
					//					String absPath = "/Home/valentina.marioli/Workspace/testGcubeItem";
					//					NodeWrapper wrapNode = new NodeWrapper(session.getNode(absPath));
					//					System.out.println("----> " + wrapNode.getItemDelegate().toString());



				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}


	}
	


	private static List<AccountingDelegate> getAccouting(Session session, String identifier) throws Exception {

		List<AccountingDelegate> children = new ArrayList<AccountingDelegate>();
		
		Node node = session.getNodeByIdentifier(identifier);
		Node accountingNode = node.getNode("hl:accounting");
		for(NodeIterator iterator = accountingNode.getNodes();iterator.hasNext();) {
			Node entryNode = (Node)iterator.next();
			
			AccountingDelegate item = null;
			AccoutingNodeWrapper wrap = new AccoutingNodeWrapper(entryNode);
			try {
				item = wrap.getAccountingDelegate();
//				wrap.setProperties(item);
				children.add(item);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
		return children;
	}



}
