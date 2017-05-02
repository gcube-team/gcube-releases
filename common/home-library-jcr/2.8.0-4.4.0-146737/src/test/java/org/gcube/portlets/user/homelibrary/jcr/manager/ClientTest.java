package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.Test;

public class ClientTest {
    
	@Test
	public void contactClient() throws Exception{
 
		InputStream s = new FileInputStream(new File("/home/valentina/Screenshot from 2014-09-09 18_15_20.png"));
 
		final MultipleOutputStream mos = new MultipleOutputStream(s);
 
		Thread t1 = new Thread(){
			public void run(){
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
			}
		};
 
		Thread t2 = new Thread(){
			public void run(){
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				long start = System.currentTimeMillis();
				int readTot =0 ;
				int read =0 ;
				byte[] buf = new byte[8096];
				try(InputStream is = mos.getS2()){
					while ((read = is.read(buf))!=-1){
						System.out.println("reading t2-- "+read);
						readTot+=read;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName()+"- "+(System.currentTimeMillis()-start)+" read t2 "+readTot);
			}
		};
 
		t1.start();
		t2.start();
 
		mos.startWriting();
 
		t1.join();
		t2.join();
	}
 
 
	public class MultipleOutputStream {
 
		InputStream s1;
		InputStream s2;
 
		InputStream is;
 
		PipedOutputStream os1;
		PipedOutputStream os2;
 
		public MultipleOutputStream(InputStream is) throws Exception{
			this.is = is;
 
			os1 = new PipedOutputStream();
			os2 = new PipedOutputStream();
 
			s1 = new PipedInputStream(os1);
			s2 = new PipedInputStream(os2);
		}
 
		public void startWriting() throws Exception{
			try(BufferedInputStream bis = new BufferedInputStream(is)){
				byte[] buf = new byte[8096];
				int read=-1;
				int writeTot = 0;
				while ((read =bis.read(buf))!=-1){
					os1.write(buf, 0, read);
					os2.write(buf, 0, read);
					writeTot+= read;
				}
				os1.close();
				os2.close();
				System.out.println("total written "+writeTot);
			}
		}
 
 
		public InputStream getS1() {
			return s1;
		}
 
		public InputStream getS2() {
			return s2;
		}
	}
}
