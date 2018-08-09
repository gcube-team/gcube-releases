package org.gcube.usecases.ws.thredds.engine.impl.threads;

public class ProcessIdProvider {

	public static ProcessIdProvider instance=new ProcessIdProvider();
	
	private static final InheritableThreadLocal<String> threadProcessId=
			new InheritableThreadLocal<String>() {
		@Override
		protected String initialValue() {
			return null;
		}
	};
	
	private ProcessIdProvider() {
		
	}
	
	public String get() {
		return threadProcessId.get();
	}
	
	public void set(String processId) {
		threadProcessId.set(processId);
	}
	
	public void reset() {
		threadProcessId.remove();
	}
}
