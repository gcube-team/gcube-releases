package org.gcube.contentmanagement.blobstorage.test;

import java.util.Iterator;
import java.util.List;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.service.impl.ServiceEngine;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;



public class SimpleTest2 {

//	public static void main(String[] args) throws Exception {
//		 
//		String[] SERVER_IPS = new String[]{"146.48.122.153"};
//		IClient client = new ServiceEngine(SERVER_IPS, "HomeLibrary-ContentManager", "ROOT", "private");
// 
//		File content = new File("/home/rcirillo/FilePerTest/CostaRicanFrog.jpg");
//		long origSize = content.length();
// 
//		String location = "pippo";
//		String contentId = client.put().LFile(new FileInputStream(content)).RFile(location);
//		System.out.println("contentId: "+contentId);
//		System.out.println("location: "+location);
// 
// 
//		InputStream is = client.get().RFileAStream(location);
// 
//		File tmpFile = File.createTempFile("tmpDownload", "tmp");
//		IOUtils.copy(is, new FileOutputStream(tmpFile));
//		long remoteSize = tmpFile.length();
// 
//		System.out.println("localSize: "+origSize+" remoteSize: "+remoteSize);
// 
//		boolean compare = IOUtils.contentEquals(new FileInputStream(content), new FileInputStream(tmpFile));
//		System.out.println("Equals? "+compare);
// 
//	}
	
	
	public static void main(String[] args) throws RemoteBackendException{		
//		String[] server=new String[]{"146.48.122.102","146.48.122.138",  "146.48.122.153" };
//		String[] server=new String[]{"146.48.123.71","146.48.123.72" };
		String[] server=new String[]{"146.48.123.73","146.48.123.74" };

	    String ghnId="/757ea320-1758-11e1-b2c6-e6eb44eceb4f";
//		IClient client=new ServiceEngine(server, "ServiceClassServiceName"+ghnId, "devsec", "private", "rcirillo");
		IClient client=new ServiceEngine(server, "rcirillo", "cnr", "private", "rcirillo");		
		String localFile="/home/rcirillo/FilePerTest/CostaRica.jpg";
		String remoteFile="/img/shared9.jpg";
		String remoteFile2="/img/img2/shared3.jpg";
//		long start=System.currentTimeMillis();
		String id=client.put(true).LFile(localFile).RFile(remoteFile);
//		System.out.println("id for remote file "+remoteFile+" is "+id);
//		String id2=client.put(true).LFile(localFile).RFile(remoteFile2);
//		System.out.println("id for remote file "+remoteFile+" is "+id2);

//		System.out.println("UPLOAD: "+localFile+" in "+(System.currentTimeMillis()-start));
		String newFile="/home/rcirillo/FilePerTest/repl4.jpg";
//		System.out.println("DOWNLOAD: "+remoteFile+" in: "+newFile);
//		start=System.currentTimeMillis();
		client.get().LFile(newFile).RFile(remoteFile);
//		System.out.println("Done. in "+(System.currentTimeMillis()-start));
//		List list=client.showDir("/img/");
//		client.removeDir().RFile("/img/");
//		client.remove().RFileById(id2);
//		client.remove().RFileById("4fa91b73e4b004bdba8543b9");
//		client.removeDir().RDir("/img/img2");
		
//		client.unlock("4fa93c4ae4b02ac1dbb5ce1e1336491082249").RFileById("4fa93c4ae4b02ac1dbb5ce1e");
//		client.get().LFile(newFile).RFile(remoteFile);

//		client.put(true).
//		client.get().LFile(newFile).RFile(remoteFile);
//		client.remove().RFileById(id);
		List<StorageObject> list=client.showDir().RDir("/img/");
		for(StorageObject obj : list){
//			System.out.println("obj found: "+obj.getName());
		}
		
//		String idLock=client.lock().RFile(remoteFile);
//		String idLock=client.lock().RFileById(id);
//		System.out.println("locked "+remoteFile+" with id : "+idLock);
		
		String uri=client.getUrl().RFile(remoteFile);
//		String uri=client.getUrl().RFileById(id);
		System.out.println(" uri file: "+uri);
//		List list=client.showDir().RDir("/");
//		if(list!=null){
//			System.out.println("size: "+list.size());
//			for(Iterator it=list.iterator();it.hasNext();){
//				StorageObject obj=(StorageObject)it.next();
//				System.out.println("obj found: "+obj.getName()+"  file? "+obj.isFile());
//			}
//		}
		
	}
}
