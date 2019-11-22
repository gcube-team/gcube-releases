package org.gcube.data.access.storagehub.fs;

import java.io.IOException;
import java.io.InputStream;

import org.gcube.common.storagehub.client.dsl.FileContainer;
import org.gcube.common.storagehub.model.items.AbstractFileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jnr.ffi.Pointer;
import jnr.ffi.types.off_t;
import jnr.ffi.types.size_t;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.struct.FileStat;

public class FileDownload implements SHFile{

	public static Logger logger = LoggerFactory.getLogger(FileDownload.class);

	InputStream stream;
	AbstractFileItem fileItem;

	Object monitor = new Object();

	long offset = 0;

	public FileDownload(FileContainer fileContainer) throws Exception {
		stream = fileContainer.download().getStream();
		fileItem = fileContainer.get();
		logger.trace("FILE-DOWNLOAD initialized with {} , {}", fileItem.getName(), fileItem.getContent().getSize());
	}

	
	public int read(Pointer buf, long size, long offset) {
		logger.trace("{} read called with size {} and offset {} ", fileItem.getName(), size, offset);
		
		while (this.offset!=offset) {
			logger.trace("going in wait ({},{})",this.offset, offset);
			synchronized (monitor) {
				try {
					monitor.wait();
					logger.trace("waking up!!!");
				} catch (InterruptedException e2) {
					logger.warn("interrupt exception",e2);
				}
			}
		}

		int bytesToRead = (int) (size);

		byte[] mybuf = new byte[bytesToRead];
		int readTotal= 0;	
		try {

			int read =0;
			logger.trace("{} BEFORE: bytes to read {} and read total {} and last read {}", fileItem.getName(), bytesToRead, readTotal, read);
			while ((read= stream.read(mybuf, 0 , bytesToRead-readTotal))!=-1 && bytesToRead>readTotal) {
				buf.put(readTotal, mybuf, 0, read);
				readTotal+= read;
				logger.trace("{} INSIDE: bytes to read {} and read total {} and last read {}", fileItem.getName() , bytesToRead, readTotal, read);
			}
			logger.trace("{} AFTER: bytes to read {} and read total {} and last read {}", fileItem.getName() , bytesToRead, readTotal, read);

		}catch (Exception e) {
			logger.error("error in read",e);
			try {
				stream.close();
			} catch (IOException e1) {}
			return -ErrorCodes.ENOENT();
		} finally {
			this.offset = readTotal+offset;
			logger.trace("setting offset to {}",this.offset);
			synchronized (monitor) {
				monitor.notifyAll();
			}
		}
		logger.trace("{} work finished!!! {}",fileItem.getName() , readTotal);
		return readTotal;
	}




	public synchronized int flush() {
		logger.trace("called flush");
		//logger.trace("file is ready "+mapPathUpload.get(path).toString());
		try {
			stream.close();
			synchronized (monitor) {
				monitor.notifyAll();
			}
		} catch (IOException e1) {
			logger.error("error closing stream",e1);
		}
		return 0;
	}	


	public int getAttr(FileStat stat) {
		logger.trace("is in download");
		stat.st_mode.set(FileStat.S_IFREG | 0555);
		stat.st_size.set(fileItem.getContent().getSize());
		stat.st_mtim.tv_sec.set(fileItem.getLastModificationTime().toInstant().getEpochSecond());
		stat.st_ctim.tv_sec.set(fileItem.getLastModificationTime().toInstant().getEpochSecond());
		stat.st_atim.tv_sec.set(fileItem.getLastModificationTime().toInstant().getEpochSecond());
		//stat.st_birthtime.tv_sec.set(fileItem.getCreationTime().toInstant().getEpochSecond());
		return 0;
	}

}
