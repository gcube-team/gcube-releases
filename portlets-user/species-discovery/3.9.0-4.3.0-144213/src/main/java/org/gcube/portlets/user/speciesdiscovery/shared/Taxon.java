package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
@Entity
public class Taxon implements Serializable, FetchingElement, TaxonomyInterface, Comparable<Taxon>{

	private static final long serialVersionUID = -3579358036639552802L;

	public final static String ID_FIELD = "id";
	public final static String PARENTFOREIGN_KEY_TAXON = "parent";
	public final static String RANK = "rank";
	public static final String NAME = "name";
	public static final String ACCORDING_TO = "accordingTo";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int internalId;
	
	protected int id;
	
	protected String name;
	protected String accordingTo;
	protected String rank;

	public Taxon() {
	}
	
	public Taxon(int id) {
		this.id = id;
	}

	/**
	 * Creates a new Taxon.
	 * 
	 * @param id
	 *            the Taxon id.
	 * @param name
	 *            the Taxon name.
	 * @param accordingTo
	 *            the Taxon name author.
	 * @param rank
	 *            the Taxon rank.
	 */
	public Taxon(int id, String name, String accordingTo, String rank) {
		this.id = id;
		this.name = name;
		this.accordingTo = accordingTo;
		this.rank = rank;
	}

	
	/**
	 * {@inheritDoc}
	 */

	public String getTaxonId() {
		return id+"";
	}

	/**
	 * {@inheritDoc}
	 */

	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */

	public String getAccordingTo() {
		return accordingTo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRank() {
		return rank;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	/* 
	 * 
	 * Comparable on insertion order
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Taxon o) {
		return id-o.getId();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Taxon [internalId=");
		builder.append(internalId);
		builder.append(", id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", accordingTo=");
		builder.append(accordingTo);
		builder.append(", rank=");
		builder.append(rank);
		builder.append("]");
		return builder.toString();
	}
}
