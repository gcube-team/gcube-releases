package org.gcube.datatransformation.datatransformationlibrary.utils.queue;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryFileBackedQueue<E extends Serializable> extends AbstractQueue<E> {
	private static Logger log = LoggerFactory.getLogger(MemoryFileBackedQueue.class);
	private static final int DEFAULT_THRESHOLD = 10;
	private Queue<E> m_memoryQueue;
	private FileBackedQueue<E> m_fileQueue;
	private int m_threshold;
	private Object sync = new Object();
	private boolean finished = false;
	
	private long totalCnt = 0 , fbackedCnt = 0;
	
	public MemoryFileBackedQueue() throws IOException {
		this(MemoryFileBackedQueue.DEFAULT_THRESHOLD);
	}

	public MemoryFileBackedQueue(int threshold) throws IOException {
		m_threshold = threshold;
		m_memoryQueue = new ConcurrentLinkedQueue<E>();
		m_fileQueue = new FileBackedQueue<E>();
	}

	public boolean offer(E e) {
		boolean retValue = false;
		synchronized (sync) {
			if (finished)
				return false;
		}
		totalCnt++;
		if (m_threshold > m_memoryQueue.size() && m_fileQueue.size() == 0) {
			retValue = m_memoryQueue.offer(e);
		} else {
			fbackedCnt++;
			retValue = m_fileQueue.offer(e);
		}

		synchronized (sync) {
			sync.notifyAll();
		}

		return retValue;
	}

	public E poll() {
		E retValue = null;

		synchronized (sync) {
			if (size() == 0)
				try {
					sync.wait(60000);
				} catch (InterruptedException e) {
				}
		}

		if (m_memoryQueue.size() > 0 || m_fileQueue.size() == 0) {
			retValue = m_memoryQueue.poll();
		} else {
			retValue = (E) m_fileQueue.poll();
		}

		return retValue;
	}

	public E peek() {
		return null;
	}

	public int size() {
		synchronized (sync) {
			return m_fileQueue.size() + m_memoryQueue.size();
		}
	}

	public Iterator<E> iterator() {
		return null;
	}

	public void destroy() {
		synchronized (sync) {
			finished = true;
		}
		log.info("Total number of records: " + totalCnt + ". Number of records that were stored in file: " + fbackedCnt + ". Percentage: "
				+ (totalCnt > 0 ? (double) fbackedCnt / (double) totalCnt * (double) 100 : 0) + "% of records stored in file.");
		m_fileQueue.destroy();
		m_memoryQueue.clear();
	}
}