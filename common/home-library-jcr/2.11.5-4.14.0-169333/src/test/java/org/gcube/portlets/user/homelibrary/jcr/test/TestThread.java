package org.gcube.portlets.user.homelibrary.jcr.test;

public class TestThread {

	public static void main(String args[]) throws InterruptedException {
//		MultiThreadUpload R1 = new MultiThreadUpload( "Thread-1");
//		R1.start();
//
//		MultiThreadUpload R2 = new MultiThreadUpload( "Thread-2");
//		R2.start();
//		
		for (int n = 5; n > 0; n--) {
			MultiThreadUpload R1 = new MultiThreadUpload( "Thread-"+n);
			R1.start();
			Thread.sleep(1000);
		}
	}   
}
