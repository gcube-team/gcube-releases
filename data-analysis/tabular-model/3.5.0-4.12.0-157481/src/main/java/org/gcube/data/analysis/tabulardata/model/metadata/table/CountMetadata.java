package org.gcube.data.analysis.tabulardata.model.metadata.table;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.metadata.DataDependentMetadata;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CountMetadata implements DataDependentMetadata{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2877448543726343123L;

	private int count;
	
	@SuppressWarnings("unused")
	private CountMetadata(){}
	
	public CountMetadata(int count) {
		super();
		this.count = count;
	}

	@Override
	public boolean isInheritable() {
		return false;
	}

	public int getCount() {
		return count;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CountMetadata other = (CountMetadata) obj;
		if (count != other.count)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CountMetadata [count=" + count + "]";
	}
		
}
