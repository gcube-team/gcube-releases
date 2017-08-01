package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.aquamapsTypesNS;


@XmlRootElement(namespace=aquamapsTypesNS, name="fieldArray")
public class FieldArray {

	@XmlElement(namespace=aquamapsTypesNS, name="fields")
	private List<Field> theList=null;
	
	public FieldArray() {
		theList=new ArrayList<Field>();
	}
	
	public FieldArray(Collection<Field> initialCollection) {
		if(initialCollection!=null)theList=new ArrayList<Field>(initialCollection);
	}

	/**
	 * @return the theList
	 */
	public List<Field> theList() {
		return theList;
	}

	/**
	 * @param theList the theList to set
	 */
	public void theList(List<Field> theList) {
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
