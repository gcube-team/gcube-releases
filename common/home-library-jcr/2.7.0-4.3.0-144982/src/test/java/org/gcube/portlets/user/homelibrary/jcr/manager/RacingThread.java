package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.util.Date;

class RacingThread extends Thread {
	private static final long s_maxi = 1000000; // maximum # of steps
	private static final int t_maxi = 5; // maximum # of threads
	private static long[] t_done = new long[t_maxi];
	private static int t_last = -1; // index of the last thread
	private int t_indx; 
	private static int n_prime = 0;


	public static void main(String[] a) {
		System.out.println("Priority: (min, norm, max) = ("+
				Thread.MIN_PRIORITY+", "+
				Thread.NORM_PRIORITY+", "+
				Thread.MAX_PRIORITY+")");
		
		long start_time = new Date().getTime();
		
		
		for (int i=0; i<t_maxi; i++) {
			RacingThread t = new RacingThread();
			if (i==0) t.setPriority(Thread.MIN_PRIORITY);
			else if (i==1) t.setPriority(Thread.NORM_PRIORITY);
			else t.setPriority(Thread.MAX_PRIORITY);
			t.start();
		}

		System.out.print("Threads: ");
		for (int i=0; i<t_maxi; i++) {
			System.out.print(i+" ");
		}

		System.out.print("\n  Steps: ");
		for (int i=0; i<t_maxi; i++) {
			System.out.print(t_done[i]+" ");
		}
		System.out.print((new Date()).getTime()-start_time);

	}
	public RacingThread() {
		t_last++;
		t_indx = t_last;
		t_done[t_indx] = 0;
	}
	public void run() {
		for (long s=0; s<s_maxi; s++) {

			t_done[t_indx] = s;
		}
	}
}

