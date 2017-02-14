package org.gcube.datatransformation.datatransformationlibrary.dataelements.impl;

import java.util.ArrayList;
import java.util.List;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.CompoundDataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * The implementation of compound data element which keeps the parts in an <tt>ArrayList</tt>.
 * </p>
 */
public class CompoundDataElementImpl extends CompoundDataElement {

	private ArrayList<DataElement> parts = new ArrayList<DataElement>();
	
	/**
	 * Adds a part to the compound data element.
	 * @param part The part to be added.
	 */
	public void addPart(DataElement part){
		parts.add(part);
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.dataelements.CompoundDataElement#getParts()
	 * @return The parts of the compound object.
	 */
	@Override
	public List<DataElement> getParts() {
		return parts;
	}

	@Override
	public void destroy() {
		try {
			for(DataElement part: parts){
				part.destroy();
			}
		} catch (Exception e) {
		}
	}

}
