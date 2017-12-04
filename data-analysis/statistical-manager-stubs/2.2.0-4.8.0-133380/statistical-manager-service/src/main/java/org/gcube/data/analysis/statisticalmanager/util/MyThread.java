package org.gcube.data.analysis.statisticalmanager.util;

import org.gcube.data.analysis.statisticalmanager.util.ScopeUtils.ScopeBean;

public class MyThread extends Thread {

	public MyThread() {
		// TODO Auto-generated constructor stub
	}

	public MyThread(Runnable target) {
		super(target);
		// TODO Auto-generated constructor stub
	}

	public MyThread(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public MyThread(ThreadGroup group, Runnable target) {
		super(group, target);
		// TODO Auto-generated constructor stub
	}

	public MyThread(ThreadGroup group, String name) {
		super(group, name);
		// TODO Auto-generated constructor stub
	}

	public MyThread(Runnable target, String name) {
		super(target, name);
		// TODO Auto-generated constructor stub
	}

	public MyThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
		// TODO Auto-generated constructor stub
	}

	public MyThread(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
		// TODO Auto-generated constructor stub
	}

	//**************************** ThreadLocal Management
	
	ScopeBean bean=null;
	
	
	@Override
	public void run() {
		ScopeUtils.setAuthorizationSettings(this.bean);
		try{
			super.run();
		}finally{
			ScopeUtils.cleanAuthorizationSettings();
		}
	}
	
	public void gCubeContext(ScopeBean bean){
		this.bean=bean;
	}
	
	public ScopeBean gCubeContext(){
		return ScopeUtils.getCurrentScopeBean();
	}
	
	public static void createAndRun(Runnable toRun){
		ScopeBean bean=ScopeUtils.getCurrentScopeBean();
		MyThread toExecute=new MyThread(toRun);
		toExecute.gCubeContext(bean);
		toExecute.start();
	}
}
