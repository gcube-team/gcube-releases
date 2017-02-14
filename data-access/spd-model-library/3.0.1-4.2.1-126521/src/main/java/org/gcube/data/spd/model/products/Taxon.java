package org.gcube.data.spd.model.products;

import static org.gcube.data.trees.data.Nodes.e;
import static org.gcube.data.trees.data.Nodes.t;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.validator.annotations.IsValid;
import org.gcube.common.validator.annotations.NotEmpty;
import org.gcube.common.validator.annotations.NotNull;
import org.gcube.data.spd.model.util.Labels;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Leaf;
import org.gcube.data.trees.data.Tree;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Taxon implements TaxonomyInterface {

	@NotNull
	@NotEmpty
	@XmlAttribute
	private String id;
	@XmlAttribute
	private String rank;
	@XmlAttribute
	private String scientificName;
	@XmlAttribute
	private String citation;
	
	@XmlAttribute
	private String scientificNameAuthorship;
	
	@XmlAttribute
	private String credits;
	@XmlAttribute
	private String lsid;
	@IsValid
	@XmlElement
	protected Taxon parent = null;
	
	protected Taxon(){}
	
	public Taxon(String id){
		this.id = id;
	}
	
	public Taxon(String id, String scientificName){
		this.id = id;
		this.scientificName = scientificName;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.data.spd.plugin.fwk.model.TaxonomyInterface#setRank(java.lang.String)
	 */
	public void setRank(String rank) {
		this.rank = rank;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.spd.plugin.fwk.model.TaxonomyInterface#getScientificName()
	 */
	public String getScientificName() {
		return scientificName;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.spd.plugin.fwk.model.TaxonomyInterface#setScientificName(java.lang.String)
	 */
	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.spd.plugin.fwk.model.TaxonomyInterface#getCitation()
	 */
	public String getCitation() {
		return citation;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.spd.plugin.fwk.model.TaxonomyInterface#setCitation(java.lang.String)
	 */
	public void setCitation(String citation) {
		this.citation = citation;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.data.spd.plugin.fwk.model.TaxonomyInterface#setId(java.lang.String)
	 */
	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.spd.plugin.fwk.model.TaxonomyInterface#getId()
	 */
	public String getId() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.data.spd.plugin.fwk.model.TaxonomyInterface#getRank()
	 */
	public String getRank() {
		return rank;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.spd.plugin.fwk.model.TaxonomyInterface#getParent()
	 */
	public Taxon getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.spd.plugin.fwk.model.TaxonomyInterface#setParent(org.gcube.data.spd.plugin.fwk.model.Taxon)
	 */
	public void setParent(Taxon parent) {
		this.parent = parent;
	}
	
	
	/**
	 * @return the scienitificNamAuthorship
	 */
	public String getScientificNameAuthorship() {
		return scientificNameAuthorship;
	}

	/**
	 * @param scienitificNamAuthorship the scienitificNamAuthorship to set
	 */
	public void setScientificNameAuthorship(String scientificNameAuthorship) {
		this.scientificNameAuthorship = scientificNameAuthorship;
	}

	public String getCredits() {
		return credits;
	}

	public void setCredits(String credits) {
		this.credits = credits;
	}

	public String getLsid() {
		return lsid;
	}

	public void setLsid(String lsid) {
		this.lsid = lsid;
	}

	protected Tree node(){
		Tree node = t(this.id);
		if (parent!=null) node.add(e(Labels.PARENT_TAG,this.parent.node()));
		node.add(e(Labels.CITATION_LABEL, this.getCitation()));
		node.add(e(Labels.SCIENTIFICNAME_LABEL, this.getScientificName()));
		node.add(e(Labels.RANK_LABEL, this.getRank()));
		return node;
	}
	
	protected static Taxon fromNode(InnerNode node) throws Exception{
		Taxon taxon = new Taxon(node.id());
		for (Field field: Taxon.class.getDeclaredFields())
			if (node.hasEdge(field.getName()) && (node.edge(field.getName()).target() instanceof Leaf)){
				field.setAccessible(true);
				field.set(taxon, ((Leaf) node.edge(field.getName()).target()).value());
			}
		if (node.hasEdge(Labels.PARENT_TAG))
			taxon.parent = Taxon.fromNode((InnerNode)node.child(Labels.PARENT_TAG));

		return taxon;
	}

	@Override
	public String toString() {
		return "Taxon [id=" + id + ", rank=" + rank + ", scientificName="
				+ scientificName + ", citation=" + citation + "]";
	}
	
	
	
}
