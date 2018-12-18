package org.gcube.data.access.fs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.plugins.AbstractPlugin;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.client.proxies.WorkspaceManagerClient;
import org.gcube.common.storagehub.model.Metadata;
import org.gcube.common.storagehub.model.Paths;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.acls.AccessType;
import org.gcube.common.storagehub.model.expressions.GenericSearchableItem;
import org.gcube.common.storagehub.model.expressions.OrderField;
import org.gcube.common.storagehub.model.expressions.SearchableItem;
import org.gcube.common.storagehub.model.expressions.logical.And;
import org.gcube.common.storagehub.model.expressions.logical.ISDescendant;
import org.gcube.common.storagehub.model.expressions.text.Like;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.FolderItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.query.Queries;
import org.gcube.common.storagehub.model.query.Query;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestCall {

	@BeforeClass
	public static void setUp(){
		
		//SecurityTokenProvider.instance.set("0e2c7963-8d3e-4ea6-a56d-ffda530dd0fa-98187548");
		//token costantino 9ca79556-54b0-4bbf-ab0f-151ae326f4cf-98187548
		SecurityTokenProvider.instance.set("d9431600-9fef-41a7-946d-a5b402de30d6-98187548");
		ScopeProvider.instance.set("/gcube");
	}

	@Test
	public void getListByPath() throws Exception{
		ItemManagerClient itemclient = AbstractPlugin.item().build();
		WorkspaceManagerClient client = AbstractPlugin.workspace().build();
		try {
			Item ws = client.getWorkspace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		/*System.out.println("ws id is "+ws.getId());
		List<? extends Item> items = itemclient.getChildren(ws.getId(),10, 5, "hl:accounting", "jcr:content");
		List<? extends Item> Vreitems = client.getVreFolders("hl:accounting");
		List<? extends Item> VreitemsPaged = client.getVreFolders(5,5, "hl:accounting");

		System.out.println("items are "+items.size());

		System.out.println("vreItems are "+Vreitems.size());

		System.in.read();*/
	}

	
	@Test
	public void createFolderAndShare() throws Exception{
		ItemManagerClient itemclient = AbstractPlugin.item().build();
		
		WorkspaceManagerClient client = AbstractPlugin.workspace().build();
		Item ws = client.getWorkspace();
		String id = itemclient.createFolder(ws.getId(), "ok7SharingTest", "shared folder for test SHM");
		String sharedId = itemclient.shareFolder(id, new HashSet<String>(Arrays.asList("giancarlo.panichi")), AccessType.WRITE_OWNER);
		itemclient.uploadFile(new FileInputStream("/home/lucio/Downloads/upload.pdf"), sharedId, "sharedFile.pdf" , "shared file in a shared folder");
		
	}
	
	@Test
	public void shareAnAlreadySharedFolder() {
		
		ItemManagerClient itemclient = AbstractPlugin.item().build();
		itemclient.shareFolder("86e8472a-6f66-4608-9d70-20102c9172ce", new HashSet<>(Arrays.asList("costantino.perciante")), AccessType.READ_ONLY);
	}
	
	@Test
	public void restore() {
		
		WorkspaceManagerClient client = AbstractPlugin.workspace().build();
		client.restoreFromTrash("82af9e1c-6cc7-4e16-bba5-9bec6545015a");
	}
	
	@Test
	public void unshareFolder() throws Exception{
		ItemManagerClient itemclient = AbstractPlugin.item().build();
		itemclient.unshareFolder("86e8472a-6f66-4608-9d70-20102c9172ce", new HashSet<>(Arrays.asList("giancarlo.panichi")));
		
	}
	
	
	@Test
	public void delete() throws Exception{
		ItemManagerClient itemclient = AbstractPlugin.item().build();
		itemclient.delete("7af3d5cb-5e74-4a80-be81-acb2fec74cd9");
		
	}
	
	@Test
	public void getById() throws Exception{
		final ItemManagerClient client = AbstractPlugin.item().build();
		List<? extends Item> items = client.getAnchestors("29b417e2-dc2f-419a-be0b-7f49e76c9d7c", "hl:accounting", "jcr:content");

		System.out.println("items are "+items.size());

		for (Item item: items)
			System.out.println(item.getName()+ " "+item.getPath());

		System.in.read();
	}

	@Test 
	public void setMetadata() {
		final ItemManagerClient client = AbstractPlugin.item().build();
		Metadata meta = new Metadata();
		HashMap<String, Object> prop = new HashMap<>();
		prop.put("folderProp", "test2");
		prop.put("folderProp2", "test2");
		meta.setValues(prop);
		client.setMetadata("8822478a-4fd3-41d5-87de-9ff161d0935e", meta);
		
		
	}
	
	
	@Test
	public void upload() throws Exception{
		//final ItemManagerClient client = AbstractPlugin.item().at(new URI("http://workspace-repository1-d.d4science.org:8080/storagehub")).build();
		
		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class).property(ClientProperties.CHUNKED_ENCODING_SIZE, 1024).property(ClientProperties.OUTBOUND_CONTENT_LENGTH_BUFFER, -1)
			.property(ClientProperties.REQUEST_ENTITY_PROCESSING, "CHUNKED");
		WebTarget target = client.target("http://workspace-repository1-d.d4science.org:8080/storagehub/workspace/items/bc1c9525-43f7-4565-b5ea-0a0f9d7853a0/create/test-upload?gcube-token=595ca591-9921-423c-bfca-f8be19f05882-98187548");
		
		FormDataMultiPart multipart = new FormDataMultiPart();

		multipart.field("name", "test1Gb2.db");
		multipart.field("description", "description");
		multipart.field("file", new FileInputStream("/home/lucio/Downloads/ar_bigdata_201705.csv"), MediaType.APPLICATION_OCTET_STREAM_TYPE);
		
		target.request().post(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA));
		
		//client.uploadFile(new FileInputStream("/home/lucio/Downloads/test5Gb.zip"), "bc1c9525-43f7-4565-b5ea-0a0f9d7853a0", "5gb.zip", "description");
		
	}
	
	@Test
	public void uploadArchive() throws Exception{
		final ItemManagerClient client = AbstractPlugin.item().at(new URI("http://workspace-repository1-d.d4science.org:8080/storagehub")).build();
					
		client.uploadArchive(new FileInputStream("/home/lucio/Downloads/filezilla.tar"), "bc1c9525-43f7-4565-b5ea-0a0f9d7853a0", "filezillaTar1");
		
	}
	
	
	
	@Test
	public void getACL() throws Exception{
		final ItemManagerClient client = AbstractPlugin.item().build();
		try {
			List<ACL> acls = client.getACL("790e4c52-fbca-48e9-b267-67ea2ce708c0");

			System.out.println("items are "+acls.size());


			for (ACL acl: acls)
				System.out.println(acl.getPricipal()+ " "+acl.getAccessTypes());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void download() throws Exception{
		ItemManagerClient client = AbstractPlugin.item().build();
		StreamDescriptor streamDescr = client.download("6875651d-6510-4b82-a0f3-cc3356c1a143");
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
	public void getCount() throws Exception{
		final ItemManagerClient client = AbstractPlugin.item().build();
		long start = System.currentTimeMillis();
		System.out.println("found "+client.childrenCount("bc1c9525-43f7-4565-b5ea-0a0f9d7853a0")+" children");

		System.out.println("count took: "+(System.currentTimeMillis()-start) );

		System.in.read();
	}

	@Test
	public void getVreFolder() {
		ItemManagerClient itemclient = AbstractPlugin.item().build();
		WorkspaceManagerClient wsclient = AbstractPlugin.workspace().build();
		wsclient.getVreFolders("hl:accounting");

	}
	
	@Test
	public void getRecents() {
		ItemManagerClient itemclient = AbstractPlugin.item().build();
		WorkspaceManagerClient wsclient = AbstractPlugin.workspace().build();
		List<? extends Item> items = wsclient.getRecentModifiedFilePerVre();
		
		System.out.println("items are "+items.size());

		for (Item item: items)
			System.out.println(item.getName()+ " "+item.getPath());
	}

	@Test
	public void createFolder() {
		long start= System.currentTimeMillis();
		ItemManagerClient itemclient = AbstractPlugin.item().build();
		WorkspaceManagerClient wsclient = AbstractPlugin.workspace().build();
		String id = wsclient.getWorkspace("hl:accounting", "jcr:content").getId();
		System.out.println("getting the WS id took "+(System.currentTimeMillis()-start));
				
		
		itemclient.createFolder(id,"sesto tentativo","6 tentativo");
		System.out.println("creating folder took total "+(System.currentTimeMillis()-start));
	}
		
	@Test
	public void share() {
		ItemManagerClient itemclient = AbstractPlugin.item().build();
		itemclient.shareFolder("4fd4a4ca-c615-4076-8eaa-70268e4f6166", new HashSet<>(Arrays.asList("francesco.mangiacrapa","massimiliano.assante","giancarlo.panichi")), AccessType.WRITE_OWNER);
	
	}
	
	@Test
	public void search() {

		Query<SearchableItem<?>> query = Queries.queryFor(AbstractFileItem.class);
		/*
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2015);
		 */

		WorkspaceManagerClient wsclient = AbstractPlugin.workspace().build();
		//String path = wsclient.getVreFolder("hl:accounting").getPath();
		//System.out.println("path is "+path);
		query.setExpression(new And(new ISDescendant(Paths.getPath("/Home/massimiliano.assante/Workspace/MySpecialFolders/gcube-devNext-NextNext/")), 
				new Like(GenericSearchableItem.get().title,"title")));
		query.setLimit(10);
		query.setOrder(new OrderField(GenericSearchableItem.get().title));
		List<? extends Item> items = wsclient.search(query, "hl:accounting", "jcr:content");
		for (Item item: items) {
			System.out.println(item.getName()+" "+item.getLastModificationTime().getTimeInMillis());
		}
		System.out.println("items are "+items.size());

	}

	/*	
	@Test
	public void createFolder() throws Exception{
		Items.createFolder();
	}

	@Test
	public void createFile() throws Exception{
		Items.create();
	}*/

	@Test
	public void isValidName(){
		String name= "Chart focused on Quantities - Overall mean and standard deviation of the quantity_GENERIC_CHARTS_ID_bdba343e-0e33-4fae-8cca-4e4140610a76.png";
		Pattern p = Pattern.compile("[^a-z0-9\\s_\\-\\.]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(name);
		boolean b = m.find();
		System.out.println("result: "+!b);
	}
	

}
