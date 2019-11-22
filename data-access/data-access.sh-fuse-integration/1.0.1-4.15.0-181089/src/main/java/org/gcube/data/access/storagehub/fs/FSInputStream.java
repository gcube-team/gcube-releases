package org.gcube.data.access.storagehub.fs;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class FSInputStream extends InputStream{

	private boolean closed = false;
	
	BlockingQueue<Byte> q = new LinkedBlockingQueue<Byte>(20000);
	
	public int byteRead = 0;
	public int bytegiven = 0;
	
	protected synchronized void add(byte[] buf) {
		for (byte b : buf)
			try {
				//System.out.println("adding "+b);
				q.put(b);
				byteRead++;
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		
	}
	
	@Override
	public int read() throws IOException {
		try {
			//System.out.println("q is empty ? "+q.isEmpty());
			Byte retrievedValue;
			do{
				retrievedValue=q.poll(2, TimeUnit.SECONDS);
			} while (retrievedValue==null && !closed);
					
			if (closed && retrievedValue==null) {
				return -1;
			}
						
			int value = retrievedValue &  0xFF;
			//System.out.println("reading byte: ==== "+value);
			bytegiven++;
			return value;
		} catch (InterruptedException e) {
			//System.out.println("interrupt -------------");
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int available() {
		return q.size();
	}

	@Override
	public void close() throws IOException {
		this.closed= true;
		super.close();
	}
	
	public boolean isClosed() {
		return this.closed;
	}
	
}
