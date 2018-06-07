package org.gcube.data.access.fs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.storagehub.client.StreamDescriptor;
import org.gcube.common.storagehub.client.plugins.AbstractPlugin;
import org.gcube.common.storagehub.client.proxies.ItemManagerClient;
import org.gcube.common.storagehub.client.proxies.WorkspaceManagerClient;
import org.gcube.common.storagehub.model.Paths;
import org.gcube.common.storagehub.model.acls.ACL;
import org.gcube.common.storagehub.model.expressions.GenericSearchableItem;
import org.gcube.common.storagehub.model.expressions.OrderField;
import org.gcube.common.storagehub.model.expressions.OrderField.MODE;
import org.gcube.common.storagehub.model.expressions.SearchableItem;
import org.gcube.common.storagehub.model.expressions.date.Before;
import org.gcube.common.storagehub.model.expressions.logical.And;
import org.gcube.common.storagehub.model.expressions.logical.ISDescendant;
import org.gcube.common.storagehub.model.expressions.text.Contains;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.gcube.common.storagehub.model.items.Item;
import org.gcube.common.storagehub.model.query.Queries;
import org.gcube.common.storagehub.model.query.Query;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;

public class TestCall {

	@BeforeClass
	public static void setUp(){
		SecurityTokenProvider.instance.set("595ca591-9921-423c-bfca-f8be19f05882-98187548");
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
	}

	@Test
	public void getListByPath() throws Exception{
		ItemManagerClient itemclient = AbstractPlugin.item().build();
		WorkspaceManagerClient client = AbstractPlugin.workspace().build();
		Item ws = client.getWorkspace();
		List<? extends Item> items = itemclient.getChildren(ws.getId(),10, 5, "hl:accounting", "jcr:content");
		List<? extends Item> Vreitems = client.getVreFolders("hl:accounting");
		List<? extends Item> VreitemsPaged = client.getVreFolders(5,5, "hl:accounting");
		
		System.out.println("items are "+items.size());
		
		System.out.println("vreItems are "+Vreitems.size());
		
		System.in.read();
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
	public void createFolder() {
		long start= System.currentTimeMillis();
		ItemManagerClient itemclient = AbstractPlugin.item().build();
		WorkspaceManagerClient wsclient = AbstractPlugin.workspace().build();
		String id = wsclient.getWorkspace("hl:accounting", "jcr:content").getId();
		System.out.println("getting the WS id took "+(System.currentTimeMillis()-start));
			
		itemclient.createFolder(id,"quinto tentativo","5 tentativo");
		System.out.println("creating folder took total "+(System.currentTimeMillis()-start));
	}
	
	@Test
	public void search() {
		
		Query<SearchableItem<?>> query = Queries.queryFor(AbstractFileItem.class);
		/*
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2015);
		*/
		
		WorkspaceManagerClient wsclient = AbstractPlugin.workspace().build();
		String path = wsclient.getVreFolder("hl:accounting").getPath();
		System.out.println("path is "+path);
		query.setExpression(new ISDescendant(Paths.getPath("/Home/lucio.lelii/Workspace/MySpecialFolders/gcube-devNext-NextNext")));
		query.setLimit(10);
		query.setOrder(new OrderField(GenericSearchableItem.get().lastModification,MODE.DESC));
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
