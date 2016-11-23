package gr.uoa.di.madgik.searchlibrary.operatorlibrary.gmerge;

import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.EventEntry;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.ReaderHolder;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.ReaderScan;
import gr.uoa.di.madgik.searchlibrary.operatorlibrary.merge.RecordBufferEntry;

import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

/**
 * Abstract class that represent the dispatcher.
 * 
 * @author john.gerbesiotis - DI NKUA
 *
 */
public abstract class GradualScanDispatcher extends Thread {

	protected Vector<ReaderScan> scan = null;
	protected Vector<ReaderHolder> readers = null;
	protected BlockingQueue<RecordBufferEntry> des = null;
	protected Queue<EventEntry> events = null;
	protected String uid = null;
	protected Object synchDispatcher = null;
	protected Object synchWriterInit;
	protected SynchFinished synchFinished = null;
	 
	public GradualScanDispatcher(Vector<ReaderScan> scan, Vector<ReaderHolder> readers, BlockingQueue<RecordBufferEntry> des, Queue<EventEntry> events, String uid, Object synchDispatcher, Object synchWriterInit, SynchFinished synchFinished) {
		this.scan = scan;
		this.readers = readers;
		this.des = des;
		this.events = events;
		this.uid = uid;
		this.synchDispatcher = synchDispatcher;
		this.synchWriterInit = synchWriterInit;
		this.synchFinished = synchFinished;
	}

	public abstract void dispatch();

	public void run() {
		dispatch();
	}
}
