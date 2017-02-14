package gr.uoa.di.madgik.searchlibrary.operatorlibrary.gmerge;

import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.EventEntry;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.ReaderHolder;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.ReaderScan;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.RecordBufferEntry;

import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

/**
 * Dispatcher that scans {@link Record}s in first available order.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class FirstAvailableGradualScanDispatcher extends GradualScanDispatcher {

	public FirstAvailableGradualScanDispatcher(Vector<ReaderScan> scan, Vector<ReaderHolder> readers, BlockingQueue<RecordBufferEntry> des,
			Queue<EventEntry> events, String uid, Object synchDispatcher, Object synchWriterInit, SynchFinished synchFinished) {
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

			int readersSize = readers.size();
			for (; cntLocators < readersSize; cntLocators++) {
				scan.add(cntLocators, new ReaderScan(readers, cntLocators, des, events, GradualMergeOp.TimeoutDef, GradualMergeOp.TimeUnitDef, uid,
						GradualMergeOp.OperationModeDef));
				scan.get(cntLocators).start();
				if (cntLocators == 0) {
					synchronized (synchWriterInit) {
						synchWriterInit.notify();
					}
				}
			}
		}
	}
}
