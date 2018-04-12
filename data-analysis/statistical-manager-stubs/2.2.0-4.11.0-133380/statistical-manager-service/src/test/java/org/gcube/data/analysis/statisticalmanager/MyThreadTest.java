package org.gcube.data.analysis.statisticalmanager;

import org.gcube.data.analysis.statisticalmanager.util.MyThread;
import org.gcube.data.analysis.statisticalmanager.util.ScopeUtils;
import org.gcube.data.analysis.statisticalmanager.util.ScopeUtils.ScopeBean;

public class MyThreadTest {

	public static void main(String[] args) {
		
		ScopeBean bean=new ScopeBean("/gcube/some/where", null);
		ScopeUtils.setAuthorizationSettings(bean);
		
		System.out.println("CREATING ::::: ");
		MyThread.createAndRun(new Runnable() {
			
			@Override
			public void run() {
				System.err.println("CHILD :"+ScopeUtils.getCurrentScopeBean());
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.err.println("CHILD :"+ScopeUtils.getCurrentScopeBean());
			}
		});
//		System.err.println("PARENT :"+ScopeUtils.getCurrentScopeBean());
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("PARENT :"+ScopeUtils.getCurrentScopeBean());
	}

}
