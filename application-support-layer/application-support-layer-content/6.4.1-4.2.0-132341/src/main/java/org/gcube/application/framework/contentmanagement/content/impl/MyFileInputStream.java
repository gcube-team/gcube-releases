package org.gcube.application.framework.contentmanagement.content.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyFileInputStream extends FileInputStream {

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(MyFileInputStream.class);
	
	long length;
	long read;

	public MyFileInputStream(File file, long length) throws FileNotFoundException {
		super(file);
		this.length = length;
		read = 0;
	}


	@Override
	public int read() throws IOException {
		do
		{
			if(read < length && this.available() > 0)
			{
				read++;
				return super.read();
			}
			else if(read == length)
			{
				return super.read();
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Exception:", e);
			}
		}while(true);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		do
		{
			if(read < length && this.available() > 0)
			{
				int avail =this.available();
				int size = (avail > len)? len : avail;
				int ret = super.read(b, off, size);
				read +=ret;
				return ret;
			}
			else if(read == length)
				return super.read(b, off, len);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				logger.error("Exception:", e);
			}
		}while(true);
	}

	@Override
	public int read(byte[] b) throws IOException {
		return this.read(b, 0, b.length);
	}

	
}
