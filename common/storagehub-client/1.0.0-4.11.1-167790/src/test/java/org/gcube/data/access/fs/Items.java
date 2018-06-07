package org.gcube.data.access.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehub.client.dsl.StorageHubClient;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.junit.BeforeClass;
import org.junit.Test;



public class Items {

	@BeforeClass
	public static void setUp(){
		SecurityTokenProvider.instance.set("595ca591-9921-423c-bfca-f8be19f05882-98187548");
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
	}
		
	@Test
	public void uploadFile() {
		StorageHubClient shc = new StorageHubClient();
		
		try (InputStream is = new FileInputStream(new File("/home/lucio/Downloads/bonifico ferrara anricipo.pdf"))){
			AbstractFileItem afi = shc.get().open("14d498da-eea1-4fb7-8590-9ce91aaba542").uploadFile(is, "bonificopdf1", "descr");
			System.out.println(afi.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getVreFolder() {
		StorageHubClient shc = new StorageHubClient();
		
		try (InputStream is = new FileInputStream(new File("/home/lucio/Downloads/bonifico ferrara anricipo.pdf"))){
			AbstractFileItem afi = shc.get().uploadFile(is, "bonificopdf", "descr");
			System.out.println(afi.toString());
		} catch (Exception e) {
			e.printStackTrace();
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
