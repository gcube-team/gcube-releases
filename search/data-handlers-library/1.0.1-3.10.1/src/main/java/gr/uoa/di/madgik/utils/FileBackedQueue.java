package gr.uoa.di.madgik.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.AbstractQueue;
import java.util.Iterator;

public class FileBackedQueue<E> extends AbstractQueue<E> {
	private static final String DEFAULT_FILENAME = "queuestorage";
	private File queueStorage;

	private ObjectInput input;
	private ObjectOutput output;

	private volatile int size = 0;
	private boolean initialized = false;

	public FileBackedQueue() throws IOException {
		this(FileBackedQueue.DEFAULT_FILENAME);
	}

	public FileBackedQueue(String filename) throws IOException {
		queueStorage = File.createTempFile(filename, ".tmp");
		queueStorage.deleteOnExit();
		
		OutputStream fos = new FileOutputStream(queueStorage);
		OutputStream bos = new BufferedOutputStream(fos);
		output = new ObjectOutputStream(bos);

	}

	@Override
	public synchronized int size() {
		return size;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized E poll() {

		E e = null;
		try {
			if (size <= 0)
				this.wait(6000);

			if (size > 0) {
				if (!initialized) {
					InputStream fis = new FileInputStream(queueStorage);
					InputStream bis = new BufferedInputStream(fis);
					input = new ObjectInputStream(bis);
					initialized = true;
				}

				e = (E) input.readObject();
				size--;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return e;
	}

	public void destroy() {
		try {
			input.close();
			output.close();
			if (queueStorage.exists())
				queueStorage.delete();
		} catch (Exception e) {
		}
	}

	@Override
	public synchronized boolean offer(E e) {
		try {
			output.writeObject(e);
			output.flush();

			size++;
			this.notifyAll();
		} catch (IOException e1) {
			return false;
		}

		return true;
	}

	@Override
	public E peek() {
		return poll();
	}

	@Override
	public Iterator<E> iterator() {
		return null;
	}
}