package gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge;

public abstract class ScanDispatcher extends Thread {
	
	protected ReaderScan[] scan = null;
	
	public ScanDispatcher(ReaderScan[] scan) {
		this.scan = scan;
	}
	
	public abstract void dispatch();
	
	public void run() {
		dispatch();
	}
}
