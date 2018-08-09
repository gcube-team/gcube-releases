package org.gcube.datatransfer.common.objs;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.XStream;

public class LocalSources implements Serializable{
	private static final long serialVersionUID = 1L;
	//------------------------
	protected static XStream xstream = new XStream();

	
	List<LocalSource> list;
	
	public LocalSources(){
		list=null;
	}

	public List<LocalSource> getList() {
		return list;
	}

	public void setList(List<LocalSource> list) {
		this.list = list;
	}
	public String toXML(){
		return xstream.toXML(this);
	}
}
