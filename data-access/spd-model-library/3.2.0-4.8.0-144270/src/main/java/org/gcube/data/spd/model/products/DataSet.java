package org.gcube.data.spd.model.products;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.validator.annotations.IsValid;
import org.gcube.common.validator.annotations.NotNull;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class DataSet {
	
	protected DataSet(){}
	
	@NotNull
	@XmlAttribute
	private String id;
	@XmlElement
	private String citation;
	@NotNull
	@XmlElement
	private String name;
	@NotNull 
	@IsValid
	@XmlElement
	private DataProvider dataProvider;
	
	public DataSet(String id) {
		super();
		this.id = id;
	}
	
	public String getCitation() {
		return citation;
	}
	public void setCitation(String citation) {
		this.citation = citation;
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

	public DataProvider getDataProvider() {
		return dataProvider;
	}

	public void setDataProvider(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}
	/*
	protected Node node(){
		return n(id,e(Labels.DATAPROVIDER_TAG, this.dataProvider.node()), e(Labels.CITATION_LABEL, this.citation), e(Labels.NAME_TAG, this.name));
	}
	
	protected static DataSet fromNode(InnerNode node) throws Exception{
		DataSet dataSet= new DataSet(node.id());
		for (Field field: DataSet.class.getDeclaredFields())
			if (node.hasEdge(field.getName()) && (node.edge(field.getName()).target() instanceof Leaf)){
				field.setAccessible(true);
				field.set(dataSet, ((Leaf) node.edge(field.getName()).target()).value());
			}
		if (node.hasEdge(Labels.DATAPROVIDER_TAG))
			dataSet.setDataProvider(DataProvider.fromNode((InnerNode)node.child(Labels.DATAPROVIDER_TAG)));
		return dataSet;
	}
*/
	@Override
	public String toString() {
		return "DataSet [id=" + id + ", citation=" + citation + ", name="
				+ name + ", dataProvider=" + dataProvider + "]";
	}
	
	
	
}
