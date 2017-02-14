package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.File;

@XmlRootElement(namespace=aquamapsTypesNS)
public class FileArray {

	@XmlElement(namespace=aquamapsTypesNS, name="fileList")
	private List<File> theList=null;
	
	public FileArray() {
		theList=new ArrayList<File>();
	}
	
	public FileArray(Collection<File> initialCollection) {
		if(initialCollection!=null)theList=new ArrayList<File>(initialCollection);
	}

	/**
	 * @return the theList
	 */
	public List<File> theList() {
		return theList;
	}

	/**
	 * @param theList the theList to set
	 */
	public void theList(List<File> theList) {
		this.theList = theList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return theList+"";
	}
	
	
}
