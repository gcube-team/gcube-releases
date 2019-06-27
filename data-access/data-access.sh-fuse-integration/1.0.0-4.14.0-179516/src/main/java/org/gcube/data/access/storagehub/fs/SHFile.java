package org.gcube.data.access.storagehub.fs;

import jnr.ffi.Pointer;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.struct.FileStat;

public interface SHFile {

	default int read(Pointer buf, long size, long offset) {
		return -ErrorCodes.ENOSYS();
	}
	
	default int write(Pointer buf, long size, long offset) {
		return -ErrorCodes.ENOSYS();
	}
	
	int flush();
	
	int getAttr(FileStat stat);
}
