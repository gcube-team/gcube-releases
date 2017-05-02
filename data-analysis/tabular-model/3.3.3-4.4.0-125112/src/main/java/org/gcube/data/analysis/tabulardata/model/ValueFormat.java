package org.gcube.data.analysis.tabulardata.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ValueFormat implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2429597096980701189L;
			
	public static ValueFormat format(String identifier, String regExpr, String example, Converter ... converters){
		if (converters!=null && converters.length>0)
			return new ValueFormat(identifier, regExpr, example, converters[0]);
		else return new ValueFormat(identifier, regExpr, example);
	}
	
	private String id;
	private String regExpr;
	private String example;
	private Converter converter = null;
	
	private ValueFormat(String identifier, String regExpr, String example) {
		super();
		this.regExpr = regExpr;
		this.example = example;
		this.id = identifier;
	}
	
	private ValueFormat(String identifier, String regExpr, String example, Converter converter) {
		this(identifier, regExpr, example);
		this.converter = converter;
	}
	
	public String getRegExpr() {
		return regExpr;
	}
	public String getExample() {
		return example;
	}
	
	public Converter getConverter() {
		return converter;
	}

	public String getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((regExpr == null) ? 0 : regExpr.hashCode());
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
		ValueFormat other = (ValueFormat) obj;
		if (regExpr == null) {
			if (other.regExpr != null)
				return false;
		} else if (!regExpr.equals(other.regExpr))
			return false;
		return true;
	}
	
}
