package gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge;

public class FIFOScanDispatcher extends ScanDispatcher {
	
	public FIFOScanDispatcher(ReaderScan[] scan) {
		super(scan);
	}

	public void dispatch() {
		for(int i = 0; i < scan.length; i++) {
			scan[i].start();
			InterruptedException ex = null;
			do {
				try {
					scan[i].join();
					ex = null;
				}catch(InterruptedException e) { ex = e; }
			}while(ex != null);
		}
	}
}
