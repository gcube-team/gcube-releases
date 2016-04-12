package org.gcube.datatransfer.agent.library.test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.datatransfer.common.objs.LocalSource;
import org.gcube.datatransfer.common.objs.LocalSources;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

public class LocalBrowse {

	private IClient client;
	String rootFolder="/tmp/";
	String path="testNick/";
	//String path="";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBrowse() {
		List<LocalSource> list  = browseFirstLevel(rootFolder+path);
		
		LocalSources sources = new LocalSources();
		sources.setList(list);
		String serialized =sources.toXML(); //testing serialization
		print(deserialize(serialized));		//deserialization	
		
		//print(list);
	}

	public List<LocalSource> browseFirstLevel(String path){
		List<LocalSource> list = new ArrayList<LocalSource>();

		File main = new File(path);
		if(!main.isDirectory())return null;
		String[] children = main.list();
		if(children!=null){
			if(children.length>0){
				for(String tmp:children){
					File child = new File(path+tmp);
					if(child.isDirectory()){
						LocalSource dir = new LocalSource();
						dir.setDirectory(true);
						dir.setPath(child.getAbsolutePath());
						dir.setVfsRoot(rootFolder);
						list.add(dir);
					}
					else{
						LocalSource file = new LocalSource();
						file.setDirectory(false);
						file.setPath(child.getAbsolutePath().replaceFirst(rootFolder, ""));
						file.setVfsRoot(rootFolder);
						file.setSize(child.length());
						list.add(file);
					}
				}
			}
		}
		return list;
	}

	public void print(List<LocalSource> list){
		for(LocalSource path : list){
			if(path.isDirectory()){
				System.out.println("Dir: "+path.getPath() );
			}
			else{
				System.out.println("File: "+path.getPath()+ " - size="+path.getSize());
			}		
		}
	}
	public List<LocalSource> deserialize(String serialized){
		String tmpMsg=serialized;
		tmpMsg.replaceAll("&lt;", "<");
		tmpMsg=tmpMsg.replaceAll("&gt;", ">");

		try{
			XStream xstream = new XStream();
			LocalSources sources= new LocalSources();
			sources=(LocalSources)xstream.fromXML(tmpMsg);
			ArrayList<LocalSource> listToReturn = new ArrayList<LocalSource>();
			for(LocalSource source : sources.getList()){
				listToReturn.add(source);
			}
			return listToReturn;
		}catch(Exception e ){
			e.printStackTrace();
			return null;
		}
	}

}
