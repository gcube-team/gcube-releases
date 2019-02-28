package org.gcube.data.access.fs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.dsl.ContainerType;
import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.client.dsl.FolderContainer;
import org.gcube.common.storagehub.client.dsl.ItemContainer;
import org.gcube.common.storagehub.client.dsl.ListResolver;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.acls.AccessType;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.junit.BeforeClass;
import org.junit.Test;



public class Items {
	
	@BeforeClass
	public static void setUp(){
		SecurityTokenProvider.instance.set("a5b623b6-6577-4271-aba6-7ada687d29cf-98187548");
		//SecurityTokenProvider.instance.set("a5b623b6-6577-4271-aba6-7ada687d29cf-98187548");
		
		ScopeProvider.instance.set("/gcube");
	}
	
	
	
	
	@Test
	public void renameFile() throws Exception{
						
		StorageHubClient shc = new StorageHubClient();
				
		FolderContainer myRoot = shc.getWSRoot().newFolder("AnimalsMatcher", "");
		
		FolderContainer children = myRoot.newFolder("build", "build folder");
				
		FileContainer file = null;
		try(InputStream is = new FileInputStream(new File("/home/lucio/Downloads/mg.jpg"))){
			file = myRoot.uploadFile(is, "mg.jpg", "file");
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
		FileContainer copiedFile = file.copy(children, "mg.jpg");
				
		try(InputStream is = new FileInputStream(new File("/home/lucio/Downloads/bck.jpg"))){
			file = myRoot.uploadFile(is, "mg.jpg", "file");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		FileContainer secodncopiedFile = file.copy(children, "mg.jpg");
	}

	
	
	
	@Test
	public void uploadAndcopyFile() throws Exception {
		StorageHubClient shc = new StorageHubClient();
		
	
		
		FileContainer file = null;
		try(InputStream is = new FileInputStream(new File("/home/lucio/Downloads/TatyanaSondre.jpg"))){
			file = shc.open("0bae39d3-89be-4dd6-ad36-319813cd815a").asFolder().uploadFile(is, "TatyanaSondre5.jpg", "descr");
				
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		file.copy(shc.getWSRoot(), "firstCopy.jpg");

	}
	
	@Test
	public void download() throws Exception {
		StorageHubClient shc = new StorageHubClient();

		FolderContainer openResolver = shc.open("894d23bf-e2e9-42b6-a781-b99bb18119c8").asFolder();

		StreamDescriptor streamDescr = openResolver.download();
			
		
		File output = Files.createTempFile("down", streamDescr.getFileName()).toFile();
		try (BufferedInputStream bi = new BufferedInputStream(streamDescr.getStream()); FileOutputStream fo = new FileOutputStream(output)){
			byte[] buf = new byte[2048];			
			int read = -1;
			while ((read=bi.read(buf))!=-1) {
				fo.write(buf, 0, read);
			}
		}

		System.out.println("file written "+output.getAbsolutePath());
		
	}

	@Test
	public void emptyTrash() throws Exception {
		StorageHubClient shc = new StorageHubClient();
		
		shc.emptyTrash();

	}

	
	
	@Test
	public void uploadArchive() throws Exception {
		StorageHubClient shc = new StorageHubClient();

		String afi = null;
		
		try(InputStream is = new FileInputStream(new File("/tmp/down724121986692880606my new folder.zip"))){
			shc.getWSRoot().uploadArchive(is, "testUpload2");

			System.out.println(afi);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



	@Test
	public void getPublicLink() {
		StorageHubClient shc = new StorageHubClient();
		System.out.println(shc.open("c2573eec-3942-47ec-94a7-04869e97bb69").asFile().getPublicLink());
	}

	@Test
	public void findByName() throws Exception{
		StorageHubClient shc = new StorageHubClient();
		
		FolderContainer root = shc.open("2fd081dc-3b57-4e5f-aadf-38439aea4c1f").asFolder();
			
		FolderContainer fc = root.newFolder("Computations", "description");
		
		fc.share(new HashSet<>(Arrays.asList("giancarlo.panichi")), AccessType.READ_ONLY);
		
		System.out.println(fc.get().getPath()+"   "+fc.getId());
		
		
	}

	@Test
	public void delete() throws Exception{
		try {
			StorageHubClient shc = new StorageHubClient();
			shc.open("328d0097-e024-43fd-afc8-3926a34a5953").asFile().delete();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void downloadFile() {
		StorageHubClient shc = new StorageHubClient();

		StreamDescriptor descr = shc.open("6c5fb143-3711-4097-9bdc-952e41355440").asFile().download();
		
		File targetFile = new File("/tmp/"+descr.getFileName());

		try {
			java.nio.file.Files.copy(
					descr.getStream(), 
					targetFile.toPath(), 
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("file created "+targetFile.getAbsolutePath());
		/*
		
		List<ItemContainer<? extends Item>> containers = shc.getWSRoot().list().getContainers();

		for (ItemContainer<? extends Item> container :  containers) {
			if (container.getType()==ContainerType.FILE) {
				FileContainer file = (FileContainer) container;
				StreamDescriptor descr = file.download();
				File targetFile = new File("/tmp/"+descr.getFileName());

				try {
					java.nio.file.Files.copy(
							descr.getStream(), 
							targetFile.toPath(), 
							StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("file created "+targetFile.getAbsolutePath());
				break;
			}
		}*/

	}

	
	
	@Test
	public void downloadFolderWithExcludes() throws Exception{
		StorageHubClient shc = new StorageHubClient();

		StreamDescriptor streamDescr = shc.open("6eb20db1-2921-41ec-ab79-909edd9b58fd").asItem().download("05098be5-61a2-423a-b382-9399a04df11e");
		
		File tmpFile = Files.createTempFile(streamDescr.getFileName(),"").toFile();
		System.out.println(tmpFile.getAbsolutePath());
		
		try(FileOutputStream fos = new FileOutputStream(tmpFile)){
			InputStream is = streamDescr.getStream();
			byte[] buf = new byte[2048];
			int read= -1;
			while ((read=is.read(buf))!=-1) {
				fos.write(buf, 0, read);
			}
			
		}

	}
	

	
	
	/*

	static String baseUrl="http://workspace-repository1-d.d4science.org/storagehub";



	public static List<? extends Item> list(OrderBy order, Path path, ItemFilter<?> ... filters){
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(baseUrl+"/list/byPath?gcube-token=595ca591-9921-423c-bfca-f8be19f05882-98187548");
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		List<? extends Item> r =  invocationBuilder.get(ItemList.class).getItemlist();
		return r;

	}

	public static void createFolder(){
		//Client client = ClientBuilder.newClient();
		Client client = ClientBuilder.newBuilder()
				.register(MultiPartFeature.class).build();
		WebTarget webTarget = client.target(baseUrl+"/item/create?gcube-token=595ca591-9921-423c-bfca-f8be19f05882-98187548");

		FolderItem folder= new FolderItem();
		folder.setName("My third folder");
		folder.setTitle("My third title");
		final MultiPart multiPart = new FormDataMultiPart()
		.field("item", new ItemWrapper<FolderItem>(folder), MediaType.APPLICATION_JSON_TYPE)
		/*        .field("characterProfile", jsonToSend, MediaType.APPLICATION_JSON_TYPE)
        .field("filename", fileToUpload.getName(), MediaType.TEXT_PLAIN_TYPE)
        .bodyPart(fileDataBodyPart)*/;
        /*
		Response res = webTarget.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
		System.out.println("status is "+res.getStatus());

	}

	public static void create() throws Exception{


        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property("DEFAULT_CHUNK_SIZE", 2048);

        Clie

		//Client client = ClientBuilder.newClient();
		Client client = ClientBuilder.newClient(clientConfig)
				.register(MultiPartFeature.class);

		WebTarget webTarget = client.target(baseUrl+"/item/create?gcube-token=595ca591-9921-423c-bfca-f8be19f05882-98187548");

		GenericFileItem folder= new GenericFileItem();
		folder.setName("testUpload.tar.gz");
		folder.setTitle("testUpload.tar.gz");

		FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file", new File("/home/lucio/Downloads/testUpload.tar.gz"));
		final MultiPart multiPart = new FormDataMultiPart().field("item", new ItemWrapper<GenericFileItem>(folder), MediaType.APPLICATION_JSON_TYPE)
			.bodyPart(fileDataBodyPart, MediaType.APPLICATION_OCTET_STREAM_TYPE);
		multiPart.close();

		Response res = webTarget.request().post(Entity.entity(multiPart, multiPart.getMediaType()));
		System.out.println("status is "+res.getStatus());

		}

		public static void get() throws Exception{
			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target(baseUrl+"/item/6e9b8350-4854-4c22-8aa1-ba2d8135ad6d/download?gcube-token=950a0702-6ada-40e9-92dc-d243d1b45206-98187548");
			Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_OCTET_STREAM);
			Response res = invocationBuilder.get();


			byte[] buf = new byte[1024];
			/*while (is.read(buf)!=-1)
			System.out.println("reading the buffer");
         */

        /*
		}


		public static <T extends Item> T copy(T item, Path path){
			return null;		
		}

		public static <T extends Item> T move(T item, Path path){
			return null;		
		}

		public static <T extends Item> T unshareAll(T item){
			return null;
		}
         */

}
