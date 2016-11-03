package gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge;

public class SortScanDispatcher extends ScanDispatcher {

	public SortScanDispatcher(ReaderScan[] scan) {
		super(scan);
	}
	
	public void dispatch() {
		for(int i = 0; i < scan.length; i++)
			scan[i].start();
		
		Exception ex = null;
		for(int i = 0; i < scan.length; i++) {
			do {
				try {
					scan[i].join();
					ex = null;
				}catch(InterruptedException e) { ex = e; }
			}while(ex != null);
		}
	}

}
