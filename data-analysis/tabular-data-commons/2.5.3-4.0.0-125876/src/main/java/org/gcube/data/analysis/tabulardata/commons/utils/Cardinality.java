package org.gcube.data.analysis.tabulardata.commons.utils;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Cardinality implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5552954397483256558L;

	public static final Cardinality ONE= new Cardinality(1, 1);
	
	public static final Cardinality ZERO_OR_ONE= new Cardinality(0, 1);
	
	public static final Cardinality ZERO_OR_MORE= new Cardinality(0, Integer.MAX_VALUE);
	
	public static final Cardinality AT_LEAST_ONE= new Cardinality(1, Integer.MAX_VALUE);
	
	private int min;
	private int max;
	
	@SuppressWarnings("unused")
	private Cardinality(){}
	
	public Cardinality(int min, int max){
		this.min=min;
		this.max=max;
	}

	/**
	 * @return the min
	 */
	public int getMin() {
		return min;
	}

	/**
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + max;
		result = prime * result + min;
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
		Cardinality other = (Cardinality) obj;
		if (max != other.max)
			return false;
		if (min != other.min)
			return false;
		return true;
	}

	
}
