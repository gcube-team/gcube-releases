package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.IOException;
import java.io.InputStream;

import org.gcube.portlets.user.homelibrary.jcr.manager.ClientTest.MultipleOutputStream;

public class Thread000 implements Runnable {

	MultipleOutputStream mos;
	public Thread000(MultipleOutputStream mos) {
		this.mos = mos;
	}


	public void run() {
		System.out.println("RUN 01");
		long start = System.currentTimeMillis();
		int readTot =0 ;
		int read =0 ;
		byte[] buf = new byte[8096];
		try(InputStream is = mos.getS1()){
			while ((read = is.read(buf))!=-1){
				System.out.println("reading t1-- "+read);
				readTot+=read;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName()+"- "+(System.currentTimeMillis()-start)+" read t1 "+readTot);

		//		long start = System.currentTimeMillis();
		//		int readTot =0 ;
		////		int read =0 ;
		////		byte[] buf = new byte[8096];
		//		try(InputStream is = mos.getS2()){
		//			String url = null;
		//			try {
		//				url = workspace.getStorage().putStream(is, delegate.getPath(), mimeType);
		//				delegate.getContent().put(NodeProperty.REMOTE_STORAGE_PATH, delegate.getPath());
		//				if (is!=null)
		//					is.close();
		//			} catch (IOException e) {
		//				System.out.println(delegate.getPath() + " remote path not present" + e);
		//				throw new RemoteBackendException(e);
		//			}
		//
		//			System.out.println("GCUBEStorage URL : " + url);
		//			System.out.println(url);
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}
		//		System.out.println(Thread.currentThread().getName()+"- "+(System.currentTimeMillis()-start)+" read "+readTot);
	}
}
