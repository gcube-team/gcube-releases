package org.gcube.data.spd.model.products;

import static org.gcube.data.trees.data.Nodes.e;
import static org.gcube.data.trees.data.Nodes.n;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.validator.annotations.IsValid;
import org.gcube.common.validator.annotations.NotEmpty;
import org.gcube.common.validator.annotations.NotNull;
import org.gcube.data.spd.model.CommonName;
import org.gcube.data.spd.model.products.Product.ProductType;
import org.gcube.data.spd.model.util.ElementProperty;
import org.gcube.data.spd.model.util.Labels;
import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Leaf;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ResultItem extends Taxon implements ResultElement{
	
	@XmlElement
	private String provider;

	@XmlElement
	private List<Product> products;
	
	@XmlElement
	private List<CommonName> commonNames= new ArrayList<CommonName>();
	
	@NotNull @IsValid
	@XmlElement
	private DataSet dataSet;
	
	@XmlElement
	private String credits;
		
	@XmlElement
	private List<ElementProperty> properties = new ArrayList<ElementProperty>() ;
	
	
	protected ResultItem(){}
		
	private ResultItem(String id){
		super(id);
	}
	
	public ResultItem(String id, String scientificName) {
		super(id, scientificName);
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}
	

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
	public String getCredits() {
		return credits;
	}

	public void setCredits(String credits) {
		this.credits = credits;
	}

	/**
	 * 
	 * @return a Map with key language and value commonName 
	 */
	public List<CommonName> getCommonNames(){
		return this.commonNames;
	}
	
	public void setCommonNames(List<CommonName> commonNames){
		this.commonNames= commonNames;
	}

	public void addProperty(ElementProperty property){
		this.properties.add(property);
	}
	
	public void resetProperties(){
		this.properties = new ArrayList<ElementProperty>();
	}
	
	
	public List<ElementProperty> getProperties() {
		return Collections.unmodifiableList(properties);
	}
	
	
	public Tree toTree(){
		Tree tree =node();
		tree.setAttribute(Labels.PROVIDER_LABEL, this.getProvider());
		tree.add(e(Labels.DATASET_TAG, this.dataSet.node()));
		//adding commonNames tag
		List<Edge> commonNameEdges = new ArrayList<Edge>();
		for (CommonName entry : this.commonNames)
			commonNameEdges.add(e(Labels.COMMONNAME_LABEL,n(e(Labels.NAME_TAG, entry.getName()),e(Labels.LANGUAGE_TAG, entry.getLanguage()))));
		if (commonNameEdges.size()>0)
			tree.add(e(Labels.COMMONNAMES_LABEL,n(commonNameEdges.toArray(new Edge[commonNameEdges.size()]))));
		
		if (products!=null){
			List<Edge> productsEdges = new ArrayList<Edge>();
			for (Product product: products)
				productsEdges.add(e(Labels.PRODUCT_LABEL,n(product.getKey(),e(Labels.TYPE_LABEL, product.getType()),e(Labels.COUNT_LABEL, product.getCount()))));
			if (productsEdges.size()>0)
				tree.add(e(Labels.PRODUCTS_LABEL,n(productsEdges.toArray(new Edge[productsEdges.size()]))));
		}
		return tree;
	}
	 
	public static ResultItem fromTree(Tree tree) throws Exception{
		ResultItem item = new ResultItem(tree.id());
		if (tree.hasAttribute(Labels.PROVIDER_LABEL))item.setProvider(tree.attribute(Labels.PROVIDER_LABEL));
		for (Field field: ResultItem.class.getDeclaredFields())
			if (tree.hasEdge(field.getName()) && (tree.edge(field.getName()).target() instanceof Leaf)){
				field.setAccessible(true);
				Node node = tree.edge(field.getName()).target();
				field.set(item,((Leaf) node ).value());
			}

		for (Field field: Taxon.class.getDeclaredFields())
			if (tree.hasEdge(field.getName()) && (tree.edge(field.getName()).target() instanceof Leaf)){
				field.setAccessible(true);
				Node node = tree.edge(field.getName()).target();
				field.set(item,((Leaf) node ).value());
			}

		//retrieving commonNames
		List<CommonName> commonNames= new ArrayList<CommonName>();
		if (tree.hasEdge(Labels.COMMONNAMES_LABEL)){
			InnerNode commonNamesNode=(InnerNode) tree.edge(Labels.COMMONNAMES_LABEL).target();
			for (Edge edge :commonNamesNode.edges(Labels.COMMONNAME_LABEL)){
				String language = ((Leaf)((InnerNode)edge.target()).edge(Labels.LANGUAGE_TAG).target()).value();
				String commonName = ((Leaf)((InnerNode)edge.target()).edge(Labels.NAME_TAG).target()).value();
				commonNames.add(new CommonName(language, commonName));
			}
			item.setCommonNames(commonNames);
		}
		
		//retrieving products
		List<Product> products= new ArrayList<Product>();
		if (tree.hasEdge(Labels.PRODUCTS_LABEL)){
			InnerNode productsNode=(InnerNode) tree.edge(Labels.PRODUCTS_LABEL).target();
			for (Edge edge :productsNode.edges(Labels.PRODUCT_LABEL)){
				String type = ((Leaf)((InnerNode)edge.target()).edge(Labels.TYPE_LABEL).target()).value();
				String count = ((Leaf)((InnerNode)edge.target()).edge(Labels.COUNT_LABEL).target()).value();
				String key= edge.target().id();
				Product prod = new Product(ProductType.valueOf(type), key );
				prod.setCount(Integer.parseInt(count));
				products.add(prod);
			}
			item.setProducts(products);
		}
		
		if (tree.hasEdge(Labels.PARENT_TAG))
			item.setParent(Taxon.fromNode((InnerNode)tree.child(Labels.PARENT_TAG)));
		if (tree.hasEdge(Labels.DATASET_TAG))
			item.dataSet = DataSet.fromNode((InnerNode)tree.child(Labels.DATASET_TAG));
		
		return item;
	}
	
	
	
	public ResultType getType() {
		return ResultType.SPECIESPRODUCTS;
	}

	@Override
	public String toString() {
		return "ResultItem [provider=" + provider + ", products=" + products
				+ ", commonNames=" + commonNames + ", dataSet=" + dataSet
				+ ", credits=" + credits + "]";
	}

	
}
