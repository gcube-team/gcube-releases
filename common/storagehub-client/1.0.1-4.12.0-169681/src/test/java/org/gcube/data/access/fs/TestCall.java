package org.gcube.data.access.fs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

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
import org.gcube.common.storagehub.model.Paths;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.acls.AccessType;
import org.gcube.common.storagehub.model.expressions.GenericSearchableItem;
import org.gcube.common.storagehub.model.expressions.OrderField;
import org.gcube.common.storagehub.model.expressions.OrderField.MODE;
import org.gcube.common.storagehub.model.expressions.SearchableItem;
import org.gcube.common.storagehub.model.expressions.date.Before;
import org.gcube.common.storagehub.model.expressions.logical.And;
import org.gcube.common.storagehub.model.expressions.logical.ISDescendant;
import org.gcube.common.storagehub.model.expressions.text.Contains;
import org.gcube.common.storagehub.model.expressions.text.Like;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.query.Queries;
import org.gcube.common.storagehub.model.query.Query;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;

public class TestCall {

	@BeforeClass
	public static void setUp(){
		/*
		SecurityTokenProvider.instance.set("8effc529-44ec-4895-b727-ed0dc14ad113-843339462");
		ScopeProvider.instance.set("/d4science.research-infrastructures.eu");
		*/
		SecurityTokenProvider.instance.set("595ca591-9921-423c-bfca-f8be19f05882-98187548");
		ScopeProvider.instance.set("/gcube");
	}

	@Test
	public void getListByPath() throws Exception{
		ItemManagerClient itemclient = AbstractPlugin.item().build();
		WorkspaceManagerClient client = AbstractPlugin.workspace().build();
		Item ws = client.getWorkspace();
		System.out.println("ws id is "+ws.getId());
		List<? extends Item> items = itemclient.getChildren(ws.getId(),10, 5, "hl:accounting", "jcr:content");
		List<? extends Item> Vreitems = client.getVreFolders("hl:accounting");
		List<? extends Item> VreitemsPaged = client.getVreFolders(5,5, "hl:accounting");

		System.out.println("items are "+items.size());

		System.out.println("vreItems are "+Vreitems.size());

		System.in.read();
	}

	
	@Test
	public void delete() throws Exception{
		ItemManagerClient itemclient = AbstractPlugin.item().build();
		itemclient.delete("6d712458-cbba-4141-b7ef-c9bf6b9537c7");
		
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
	public void upload() throws Exception{
		//final ItemManagerClient client = AbstractPlugin.item().at(new URI("http://workspace-repository1-d.d4science.org:8080/storagehub")).build();
		
		Client client = ClientBuilder.newClient();
		client.register(MultiPartFeature.class).property(ClientProperties.CHUNKED_ENCODING_SIZE, 1024).property(ClientProperties.OUTBOUND_CONTENT_LENGTH_BUFFER, -1)
			.property(ClientProperties.REQUEST_ENTITY_PROCESSING, "CHUNKED");
		WebTarget target = client.target("http://workspace-repository1-d.d4science.org:8080/storagehub/workspace/items/bc1c9525-43f7-4565-b5ea-0a0f9d7853a0/create/FILE?gcube-token=595ca591-9921-423c-bfca-f8be19f05882-98187548");
		
		FormDataMultiPart multipart = new FormDataMultiPart();

		multipart.field("name", "5gb.zip");
		multipart.field("description", "description");
		multipart.field("file", new FileInputStream("/home/lucio/Downloads/test5Gb.zip"), MediaType.APPLICATION_OCTET_STREAM_TYPE);
		
		target.request().post(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA));
		
		//client.uploadFile(new FileInputStream("/home/lucio/Downloads/test5Gb.zip"), "bc1c9525-43f7-4565-b5ea-0a0f9d7853a0", "5gb.zip", "description");
		
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
		StreamDescriptor streamDescr = client.download("07cd8d55-a35b-4445-9680-c98f158c55de");
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
		System.out.println("found "+client.childrenCount("07cd8d55-a35b-4445-9680-c98f158c55de")+" children");

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


}
