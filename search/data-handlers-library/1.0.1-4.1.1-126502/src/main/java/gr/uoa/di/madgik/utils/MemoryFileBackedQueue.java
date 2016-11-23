package gr.uoa.di.madgik.utils;

import java.io.IOException;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class MemoryFileBackedQueue<E> extends AbstractQueue<E> {
	private static final int DEFAULT_THRESHOLD = 10;
	private Queue<E> m_memoryQueue;
	private FileBackedQueue<E> m_fileQueue;
	private int m_threshold;
	private Object sync = new Object();

	public MemoryFileBackedQueue() throws IOException {
		this(MemoryFileBackedQueue.DEFAULT_THRESHOLD);
	}

	public MemoryFileBackedQueue(int threshold) throws IOException {
		m_threshold = threshold;
		m_memoryQueue = new LinkedList<E>();
		m_fileQueue = new FileBackedQueue<E>();
	}

	public boolean offer(E e) {
		boolean retValue = false;

		if (m_threshold > m_memoryQueue.size() && m_fileQueue.size() == 0) {
			retValue = m_memoryQueue.offer(e);
		} else {
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
			while (size() == 0)
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
		m_fileQueue.destroy();
		m_memoryQueue.clear();
	}
}