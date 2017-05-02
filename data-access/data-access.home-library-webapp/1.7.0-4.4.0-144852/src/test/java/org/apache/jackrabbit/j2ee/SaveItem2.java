package org.apache.jackrabbit.j2ee;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.j2ee.workspacemanager.ItemDelegateWrapper;
import org.apache.jackrabbit.j2ee.workspacemanager.storage.GCUBEStorage;
import org.apache.jackrabbit.j2ee.workspacemanager.util.MetaInfo;
import org.apache.jackrabbit.j2ee.workspacemanager.util.Util;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.ItemDelegate;
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


public class SaveItem2 {
	private static final String nameResource 				= "HomeLibraryRepository";
	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {



		String rootScope = "/gcube/preprod/preVRE";


		ScopeProvider.instance.set(rootScope);

		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'Database' and $resource/Profile/Name eq '"+ nameResource + "' ");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);



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

					System.out.println("**CREATE ITEM SERVLET**");
					long start = System.currentTimeMillis();

					
					String parentPath = "/Home/valentina.marioli/Workspace/PreProd";
					String name = "test.pdf";
					String filenameWithExtension ="test.pdf";
					String size = "";
					String description = "";
					String remotePath = parentPath + "/" + name;
					Node parent = session.getNode(parentPath);
					String login = "valentina.marioli";
					String mimetype = "";
					long start00 = System.currentTimeMillis();
					
					
					// opens input stream of the request for reading data
					InputStream inputStream = new FileInputStream("/home/valentina/Downloads/managingsharing.pdf");	
					System.out.println("fileData: " + inputStream.available());

					System.out.println("**** " + filenameWithExtension + " get Inpustream in "+(System.currentTimeMillis()-start00)+ " millis");

					MetaInfo metadata = null;
					if (inputStream!=null){
						GCUBEStorage storage = new GCUBEStorage("valentina.marioli");

						long mysize = 0;

						if(size!=null)
							try {
								mysize = Long.parseLong(size);
							} catch (Exception e) {
								System.out.println("size cannot be cast to long  " + e.getMessage());
							}

						
						metadata = Util.getMetadataInfo(inputStream, storage, remotePath, filenameWithExtension, mimetype, mysize);
						if (metadata.getStorageId()==null)
							throw new Exception("Inpustream not saved in storage.");
					}

					long start01 = System.currentTimeMillis();
					ItemDelegate delegate = new ItemDelegate();
					delegate.setName(name);
					delegate.setTitle(name);

					delegate.setDescription(description);
					delegate.setParentId(parent.getIdentifier());
					delegate.setOwner(login);
					delegate.setPrimaryType(PrimaryNodeType.NT_WORKSPACE_IMAGE);
					delegate.setLastAction(WorkspaceItemAction.CREATED);

					Map<NodeProperty, String> content = new HashMap<NodeProperty, String>();
					content.put(NodeProperty.CONTENT, ContentType.IMAGE.toString());
					content.put(NodeProperty.FOLDER_ITEM_TYPE, FolderItemType.EXTERNAL_IMAGE.toString());

					content.put(NodeProperty.PORTAL_LOGIN, login);

					//set metadata
					content.put(NodeProperty.MIME_TYPE, metadata.getMimeType());
					content.put(NodeProperty.SIZE, new XStream().toXML(Long.valueOf(String.valueOf(metadata.getSize()))));
					content.put(NodeProperty.REMOTE_STORAGE_PATH, remotePath);		
					delegate.setContent(content);

					ItemDelegateWrapper wrapper = new ItemDelegateWrapper(delegate, login);
					ItemDelegate new_item = wrapper.save(session, false);


					System.out.println("**** " + filenameWithExtension + " create obj in Jackrabbit in "+(System.currentTimeMillis()-start01)+ " millis");
				}

			}
		}catch (Exception e) {
			e.printStackTrace();
		}

	}









}
