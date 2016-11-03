package org.gcube.common.informationsystem.client.eximpl;

import java.util.Iterator;
import java.util.List;

import org.gcube.common.core.informationsystem.client.ISInputStream;

public class ISInputStreamImpl<RESULT> implements ISInputStream<RESULT> {

	private List<RESULT> resultList=null;
	
	protected ISInputStreamImpl(List<RESULT> resultList){
		this.resultList=resultList;
	}
	
	public void close() {
		this.resultList=null;
	}

	public boolean isEmpty() {
		return resultList==null?true:resultList.isEmpty();
	}

	public Iterator<RESULT> iterator() {
		return resultList.iterator();
	}
	
}
