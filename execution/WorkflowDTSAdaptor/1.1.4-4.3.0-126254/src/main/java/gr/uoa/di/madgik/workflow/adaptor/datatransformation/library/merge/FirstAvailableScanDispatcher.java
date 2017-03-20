package gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.merge;

import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;

/**
 * Dispatcher that scans {@link DataElement}s in first available order.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class FirstAvailableScanDispatcher extends ScanDispatcher {

	public FirstAvailableScanDispatcher(Vector<ReaderScan> scan, Vector<ReaderHolder> readers, BlockingQueue<DataElement> des, Queue<EventEntry> events,
			String uid, Object synchDispatcher, Object synchWriterInit, SynchFinished synchFinished) {
		super(scan, readers, des, events, uid, synchDispatcher, synchWriterInit, synchFinished);
	}

	public void dispatch() {
		int cntLocators = 0;

		while (!synchFinished.isFinished()) {
			try {
				synchronized (synchDispatcher) {
					while (readers.size() == cntLocators && !synchFinished.isFinished()) {
						synchDispatcher.wait();
					}
				}
			} catch (InterruptedException e) {
			}

			for (; cntLocators < readers.size(); cntLocators++) {
				scan.add(cntLocators, new ReaderScan(readers, cntLocators, des, events, uid));
				scan.get(cntLocators).start();
				if (cntLocators == 0){
					synchronized (synchWriterInit) {
						synchWriterInit.notify();
					}
				}
			}
		}
	}
}
