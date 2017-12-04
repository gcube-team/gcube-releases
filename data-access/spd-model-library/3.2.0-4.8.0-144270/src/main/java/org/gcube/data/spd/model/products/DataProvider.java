package org.gcube.data.spd.model.products;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.validator.annotations.NotNull;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DataProvider {
	
	
	protected DataProvider(){}
	
	@NotNull
	@XmlElement
	private String name;
	@NotNull
	@XmlAttribute
	private String id;
	
	public DataProvider(String id) {
		super();
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	
	/*
	protected Node node(){
		return n(id, e(Labels.NAME_TAG, this.name));
	}

	protected static DataProvider fromNode(InnerNode node) throws Exception{
		DataProvider dataProvider= new DataProvider(node.id());
		for (Field field: DataProvider.class.getDeclaredFields())
			if (node.hasEdge(field.getName()) && (node.edge(field.getName()).target() instanceof Leaf)){
				field.setAccessible(true);
				field.set(dataProvider, ((Leaf) node.edge(field.getName()).target()).value());
			}
		return dataProvider;
	}*/

	@Override
	public String toString() {
		return "DataProvider [name=" + name + ", id=" + id + "]";
	}
	
	
}
