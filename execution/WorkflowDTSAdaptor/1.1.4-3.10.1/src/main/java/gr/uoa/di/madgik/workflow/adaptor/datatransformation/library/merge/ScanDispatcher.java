package gr.uoa.di.madgik.workflow.adaptor.datatransformation.library.merge;

import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;

/**
 * Abstract class that represent the dispatcher.
 * 
 * @author john.gerbesiotis - DI NKUA
 *
 */
public abstract class ScanDispatcher extends Thread {

	protected Vector<ReaderScan> scan = null;
	protected Vector<ReaderHolder> readers = null;
	protected BlockingQueue<DataElement> des = null;
	protected Queue<EventEntry> events = null;
	protected String uid = null;
	protected Object synchDispatcher = null;
	protected Object synchWriterInit;
	protected SynchFinished synchFinished = null;
	 
	public ScanDispatcher(Vector<ReaderScan> scan, Vector<ReaderHolder> readers, BlockingQueue<DataElement> des, Queue<EventEntry> events, String uid, Object synchDispatcher, Object synchWriterInit, SynchFinished synchFinished) {
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
