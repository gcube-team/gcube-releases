package org.gcube.datatransformation.datatransformationlibrary.dataelements;

import java.io.InputStream;
import java.io.Serializable;

public class DTSExceptionWrapper extends DataElement implements Serializable{
	private static final long serialVersionUID = 1L;

	private Throwable th;
	public DTSExceptionWrapper(Throwable th) {
		this.th = th;
	}
	
	@Override
	public InputStream getContent() {
		return null;
	}

	@Override
	public void destroy() {
	}

	public Throwable getThrowable() {
		return th;
	}
}
