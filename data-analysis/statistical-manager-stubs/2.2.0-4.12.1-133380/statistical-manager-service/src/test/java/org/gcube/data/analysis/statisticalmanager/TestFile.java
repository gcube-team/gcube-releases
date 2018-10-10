package org.gcube.data.analysis.statisticalmanager;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.model.protocol.smp.SMPURLConnection;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.analysis.statisticalmanager.persistence.DataBaseManager;
import org.gcube.data.analysis.statisticalmanager.persistence.HibernateManager;
import org.gcube.data.analysis.statisticalmanager.persistence.SMPersistenceManager;
import org.gcube.data.analysis.statisticalmanager.stubs.SMCreateTableRequest;
import org.gcube.data.analysis.statisticalmanager.util.ServiceUtil;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMFile;

public class TestFile {

	/**
	 * @param args
	 * @throws Exception
	 * @throws URISyntaxException
	 */
	public static IClient storage;
	public static void main(String[] args) throws URISyntaxException, Exception {
//		final SMCreateTableRequest request = new SMCreateTableRequest() {
//		};
//
//		SimpleQuery query = queryFor(ServiceEndpoint.class);
//
//		query.addCondition("$resource/Profile/Name/text() eq 'StatisticalManagerDataBase'");
//		ScopeProvider.instance.set("/gcube/devsec");
//
//		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
//		//
//		List<ServiceEndpoint> resources = client.submit(query);
//
//		ServiceEndpoint res = resources.get(0);
//		for (AccessPoint ap : res.profile().accessPoints()) {
//
//			System.out.println("******Access point name " + ap.name());
//
//			if (ap.name().equals("jdbc")) {
//
//				System.out.println("***********URL jdbc  " + ap.address());
//				System.out.println("***********User jdbc " + ap.username());
//				System.out
//						.println("***********Pass jdbc "
//								+ StringEncrypter.getEncrypter().decrypt(
//										ap.password()));
//
//				DataBaseManager.initializeDataSource(ap.address(),
//						ap.username(),
//						StringEncrypter.getEncrypter().decrypt(ap.password()));
//			}
//
//			if (ap.name().equals("hibernate")) {
//				System.out.println("***********URL hibernate  " + ap.address());
//				System.out.println("User hibernate " + ap.username());
//				System.out
//						.println("***********Pass hibernate "
//								+ StringEncrypter.getEncrypter().decrypt(
//										ap.password()));
//
//				HibernateManager.buildSessionFactory(ap.address(),
//						ap.username(),
//						StringEncrypter.getEncrypter().decrypt(ap.password()));
//
//			}
//		}
////		RSWrapper wrapper = new RSWrapper(GCUBEScope.getScope("/gcube/devsec"));
////		wrapper.add(new File("/home/angela/Desktop/test.csv"));
////		System.out.println("Create rsWrapper ");
////		final String locator = wrapper.getLocator().toString();
//
//		request.setUser("angela.italiano");
//		request.setFileName("testfile");
//		request.setDescription("test file");
//		request.setRsLocator("http://goo.gl/q82Equ");
//		System.out.println("***********Request settate ");
//		System.out.println("***********Before retrive importer ");
//		final long importerId = SMPersistenceManager.addImporter(request);
//
//		System.out.println("***********retrieve file ");
//		System.out.println("***********Locator :" + request.getRsLocator());
//		final File file= new File("/home/angela/t.csv");
////		final File file = RSWrapper.getStreamFromLocator(new URI(request
////				.getRsLocator()));
//
//		System.out.println("***********File created " + file);
//
//		try {
//			System.out.println("Before ic client ***********");
//			 storage = new StorageClient(ServiceContext.class
//					.getPackage().getName(), ServiceContext.SERVICE_NAME,
//					request.getUser(), AccessType.SHARED, "/gcube/devsec")
//					.getClient();
//
//			String url = putFile(file, file.getName());
//			System.out.println("Url***********"+url);
//			
//			
//			WorkspaceFolder appfolder = ServiceUtil.getWorkspaceSMFolder(request.getUser());
//			System.out.println("**********************Folder of service"+appfolder.getPath());
//
//			
//			String description = "File imported from the StatisticalManager";
//			InputStream inputStream = getStorageClientInputStream(url);
//			appfolder.createExternalImageItem(file.getName(), description,
//					null, inputStream);
//			System.out.println("**********************ADDED EXTERNAL FILE");
//
//			SMFile smfile = new SMFile("mimeType", file.getName(), url);
//			smfile.setPortalLogin(request.getUser());
//			smfile.setResourceType(SMResourceType.FILE.ordinal());
//			smfile.setResourceId(UUID.randomUUID().toString());
//			smfile.setName(file.getName());
//			smfile.setProvenance(SMOperationType.IMPORTED.ordinal());
//			smfile.setCreationDate(Calendar.getInstance());
//
//			SMPersistenceManager.addCreatedResource(smfile);
//			
//			
//
//		} catch (Exception e) {
//			e.printStackTrace();
////			SMPersistenceManager.setOperationStatus(importerId,"",""
////					SMOperationStatus.FAILED);
//		}

	}

	private static InputStream getStorageClientInputStream(String url)
			throws Exception {

		URL u = new URL(null, url, new URLStreamHandler() {

			@Override
			protected URLConnection openConnection(URL u) throws IOException {

				return new SMPURLConnection(u);
			}
		});
		return u.openConnection().getInputStream();

	}

	public static String putFile(File file, String fileName) {

		String rfileName = File.separator + fileName;
		storage.put(true).LFile(file.getAbsolutePath()).RFile(rfileName);
		return storage.getUrl().RFile(rfileName);
	}

}
