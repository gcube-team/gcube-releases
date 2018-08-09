package org.gcube.common.homelibrary.jcr.workspace.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipleOutputStream {

	private Logger logger = LoggerFactory.getLogger(MultipleOutputStream.class);

	MyPipedInputStream s1;
	MyPipedInputStream s2;

	InputStream is;

	MyPipedOututStream os1;
	MyPipedOututStream os2;

	public MultipleOutputStream(InputStream is) throws InternalErrorException, IOException{
		this.is = is;

		os1 = new MyPipedOututStream();
		os2 = new MyPipedOututStream();

		s1 = new MyPipedInputStream(os1);
		s2 = new MyPipedInputStream(os2);
	}

	public void startWriting() throws Exception{
		try(BufferedInputStream bis = new BufferedInputStream(is)){
			byte[] buf = new byte[65536];
			int read=-1;
			int writeTot = 0;
			while ((read =bis.read(buf))!=-1){
				if (!s1.isClosed())
					os1.write(buf, 0, read);
				if (!s2.isClosed())
					os2.write(buf, 0, read);

				writeTot+= read;
				if (os1.isClosed() && os2.isClosed())
					break;
			}

			if (!os1.isClosed())
				os1.close();
			if (!os2.isClosed())
				os2.close();

			logger.debug("total written "+writeTot);
		}catch (Exception e) {
			throw new InternalErrorException(e);
		}
	}


	public InputStream getS1() {
		return s1;
	}

	public InputStream getS2() {
		return s2;
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
