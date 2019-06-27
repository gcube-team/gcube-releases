package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TaxonChildren implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	List<TaxonomyRow> listChildren = new ArrayList<TaxonomyRow>();
	int startIndex = UNDEFINED;
	int offset = UNDEFINED;
	int fullSize = UNDEFINED;
	public static final int UNDEFINED = -1;
	
	public TaxonChildren(int startIndex, int endIndex, int fullSize, List<TaxonomyRow> listChildren) {
		this.startIndex = startIndex;
		this.offset = endIndex;
		this.fullSize = fullSize;
		this.listChildren = listChildren;
	}

	public TaxonChildren() {
	}
	

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<TaxonomyRow> getListChildren() {
		return listChildren;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getOffset() {
		return offset;
	}

	public int getFullSize() {
		return fullSize;
	}

	public static int getUndefined() {
		return UNDEFINED;
	}



	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TaxonChildren [listChildren=");
		builder.append(listChildren);
		builder.append(", startItem=");
		builder.append(startIndex);
		builder.append(", offset=");
		builder.append(offset);
		builder.append(", fullSize=");
		builder.append(fullSize);
		builder.append("]");
		return builder.toString();
	}

}
