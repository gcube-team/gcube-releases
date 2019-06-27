package org.gcube.data.access.storagehub.fs;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jnr.ffi.Pointer;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.struct.FileStat;

public class FileUpload implements SHFile {

	public static Logger logger = LoggerFactory.getLogger(FileUpload.class);
	
	FSInputStream stream;	
	
	private int bytesRead =0;
	
	public FileUpload(FSInputStream stream) {
		super();
		this.stream = stream;
	}

	public synchronized int write(Pointer buf, long size, long offset) {
		logger.trace(Thread.currentThread().getName()+" ) calling write "+ size+" "+offset);
		if (stream==null) return -ErrorCodes.ENOENT();
		byte[] mybuf = new byte[(int)size];
		try {
			buf.get(0, mybuf, 0, (int)size);
			stream.add(mybuf);
		}catch (Exception e) {
			logger.error("error on download",e);
			try {
				stream.close();
			} catch (IOException e1) {}
			return -ErrorCodes.ENOENT();
		}
		bytesRead+=size;
		return (int)size;
	}


	@Override
	public synchronized int flush() {
		try {
			stream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return 0;
	}

	@Override
	public int getAttr(FileStat stat) {
		stat.st_mode.set(FileStat.S_IFREG | 0555);
		stat.st_size.set(bytesRead);
		long now = System.currentTimeMillis()/1000;
		//stat.st_birthtime.tv_sec.set(now);
		stat.st_mtim.tv_sec.set(now);
		stat.st_ctim.tv_sec.set(now);
		stat.st_atim.tv_sec.set(now);
		return 0;
	}
	
	public boolean uploadFinished() {
		return stream.isClosed() && stream.available()==0;
	}
	
	
}
