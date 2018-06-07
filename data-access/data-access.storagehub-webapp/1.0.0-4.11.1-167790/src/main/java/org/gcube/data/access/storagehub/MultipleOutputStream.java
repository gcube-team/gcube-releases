package org.gcube.data.access.storagehub;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipleOutputStream {

	private Logger logger = LoggerFactory.getLogger(MultipleOutputStream.class);

	private MyPipedInputStream[] pipedInStreams;

	private InputStream is;

	private MyPipedOututStream[] pipedOutStreams;

	private int index=0;
	
	public MultipleOutputStream(InputStream is, int number) throws IOException{
		this.is = is;
		
		
		logger.debug("requested {} piped streams ",number);
		
		pipedInStreams = new MyPipedInputStream[number];
		pipedOutStreams = new MyPipedOututStream[number];
		
		for (int i =0; i<number; i++) {		
			pipedOutStreams[i] = new MyPipedOututStream();
			pipedInStreams[i] = new MyPipedInputStream(pipedOutStreams[i]);
		}

	}

	public void startWriting() throws Exception{
		try(BufferedInputStream bis = new BufferedInputStream(is)){
			byte[] buf = new byte[65536];
			int read=-1;
			int writeTot = 0;
			while ((read =bis.read(buf))!=-1){
				for (int i=0; i< pipedInStreams.length; i++) {
					if (!pipedInStreams[i].isClosed()) {
						logger.debug("writing into piped stream {}  ",i);
						pipedOutStreams[i].write(buf, 0, read);
					}
				}
				
						
				writeTot+= read;
				if (allOutStreamClosed()) {
					logger.debug("all streams created are closed");
					break;
				} else logger.debug("NOT all streams created are closed");
			}
			
			for (int i=0; i< pipedOutStreams.length; i++) {
				if (!pipedOutStreams[i].isClosed()) {
					logger.debug("closing outputstream {}",i);
					pipedOutStreams[i].close();
				}
			}
			
			logger.debug("total written "+writeTot);
		}
	}


	private boolean allOutStreamClosed() {
		for (int i=0; i<pipedOutStreams.length; i++) {
			if (!pipedOutStreams[i].isClosed())
				return false;
		}
		return true;
	}

	public synchronized InputStream get() {
		logger.debug("requesting piped streams {}",index);
		if (index>=pipedInStreams.length) return null;
		return pipedInStreams[index++];
	}
	

	public class MyPipedOututStream extends PipedOutputStream{

		boolean close = false;

		@Override
		public void close() throws IOException {
			this.close = true;
			super.close();
		}

		/**
		 * @return the close
		 */
		public boolean isClosed() {
			return close;
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			try{
				super.write(b, off, len);
			}catch(IOException io){
				this.close = true;
			}
		}




	}

	public class MyPipedInputStream extends PipedInputStream{

		boolean close = false;

		public MyPipedInputStream(PipedOutputStream src) throws IOException {
			super(src);
		}

		@Override
		public void close() throws IOException {
			this.close = true;
			logger.debug(Thread.currentThread().getName()+" close MyPipedInputStream");
			super.close();
		}

		/**
		 * @return the close
		 */
		public boolean isClosed() {
			return close;
		}

	}
}
