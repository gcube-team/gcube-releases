package gr.uoa.di.madgik.searchlibrary.operatorlibrary.join;

public class BooleanHolder {
	private boolean value = false;
	
	public synchronized boolean get() {
		return value;
	}
	
	public synchronized void set(boolean value) {
		this.value = value;
	}
}
