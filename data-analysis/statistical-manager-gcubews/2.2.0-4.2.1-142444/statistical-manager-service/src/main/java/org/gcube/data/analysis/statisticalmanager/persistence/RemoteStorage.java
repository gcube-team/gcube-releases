package org.gcube.data.analysis.statisticalmanager.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.analysis.statisticalmanager.ServiceContext;
import org.gcube.data.analysis.statisticalmanager.experimentspace.computation.ComputationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class RemoteStorage {

	private static Logger logger = LoggerFactory.getLogger(ComputationResource.class);
	
	private static XStream xstream = new XStream();
	
	private IClient storage;
	
	
	public RemoteStorage(){
		this.storage = new StorageClient(ServiceContext.class.getPackage().getName(),
				ServiceContext.SERVICE_NAME, ServiceContext.SERVICE_NAME, AccessType.SHARED).getClient();
	}
	
	
	public String putFile(File f, boolean randomName) {
		return storage.put(true).LFile(f.getAbsolutePath()).RFile(randomName?UUID.randomUUID().toString():f.getName());			
	}

	public String putStream(InputStream is, String path) {
		return storage.put(true).LFile(is).RFile(path);
	}
	
	public String putObject(Object outputs) throws IOException {
	
		File file = File.createTempFile("output", "smf");
		FileOutputStream fos = null;
		try{			
			fos = new FileOutputStream (file);			
			xstream.toXML(outputs, fos);
			return putFile(file,false);
		} finally {
			IOUtils.closeQuietly(fos);
			try{
				FileUtils.forceDelete(file);
			}catch(Throwable t){
				logger.warn("Unable to delete file "+file.getAbsolutePath(),t);
			}
		}
	}
	
	
	public String getUrlById(String id){
		return storage.getUrl().RFile(id);
	}
	
	public File importByUri(String uri) throws MalformedURLException, FileNotFoundException, IOException{
		File f=File.createTempFile("import", "smf");
		InputStream is=null;
		OutputStream os=null;
		try{
			IOUtils.copy(getStreamByUrl(uri), new FileOutputStream(f));
			return f;
		}finally{
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);
		}
	}
	
	
	public  InputStream getStreamByUrl(String url) throws MalformedURLException, IOException{
		URL myUrl=new URL(url);
		URLConnection conn=myUrl.openConnection();
		return conn.getInputStream();
//			return new URL(url).openConnection().getInputStream();		
	}

	
	public File importById(String id) throws IOException{
		File toReturn=File.createTempFile("import", "smf");
		storage.get().LFile(toReturn.getAbsolutePath()).RFile(id);
		return toReturn;
	}

}
