package org.gcube.portlets.user.speciesdiscovery.shared;


public interface TaxonomyInterface {

//	/**
//	 * @return the parent
//	 */
//	public List<? extends FetchingElement> getParent();

	/**
	 * @return the id
	 */
	public abstract String getTaxonId();

	/**
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * @return the accordingTo
	 */
	public abstract String getAccordingTo();

	/**
	 * @return the rank
	 */
	public abstract String getRank();

}