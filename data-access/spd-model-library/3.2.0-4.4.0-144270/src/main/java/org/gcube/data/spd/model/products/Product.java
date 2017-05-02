package org.gcube.data.spd.model.products;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Product {

	public enum ProductType {
		Occurrence,
		Taxon,
		Synonym
	}
	
	@XmlElement
	private ProductType type;
	@XmlElement
	private String key;
	@XmlElement
	private int count=1;
	
	protected Product(){}
	
	public Product(ProductType type, String key) {
		super();
		this.type = type;
		this.key = key;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public ProductType getType() {
		return type;
	}
	public String getKey() {
		return key;
	}
	
	
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		
		return "[key: "+key+", name: "+type.name()+", count: "+count+"]";
	}
}
