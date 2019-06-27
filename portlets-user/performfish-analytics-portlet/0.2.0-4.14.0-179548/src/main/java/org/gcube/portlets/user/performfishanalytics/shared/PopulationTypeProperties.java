/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.shared;


/**
 * The Interface PopulationTypeProperties.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 17, 2019
 */
public interface PopulationTypeProperties {

	public int getInternalId();

	public String getId();

	public String getName();

	public String getDescription();

	public void setId(String id);

	public void setName(String name);

	public void setDescription(String description);
}
