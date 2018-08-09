package org.gcube.common.resources.gcore.common;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;


public class FileList {

	@XmlElement(name="File")
	public Set<String> files=new LinkedHashSet<String>();

	
	@Override
	public String toString() {
		return "[files=" + files + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((files == null) ? 0 : files.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileList other = (FileList) obj;
		if (files == null) {
			if (other.files != null)
				return false;
		} else if (!files.equals(other.files))
			return false;
		return true;
	}
	
	
	
}
