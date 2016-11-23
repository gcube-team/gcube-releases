/**
 * 
 */
package org.gcube.informationsystem.model.entity.facet;

import org.gcube.informationsystem.impl.entity.facet.ContactFacetImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.annotations.Key;
import org.gcube.informationsystem.model.entity.Facet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Contact_Facet
 */
@Key(fields={ContactFacet.EMAIL_PROPERTY})
@JsonDeserialize(as=ContactFacetImpl.class)
public interface ContactFacet extends Facet {

	public static final String NAME = "ContactFacet"; // ContactFacet.class.getSimpleName();
	public static final String DESCRIPTION = "This facet is expected to "
			+ "capture contact information";
	public static final String VERSION = "1.0.0";

	public static final String EMAIL_PROPERTY = "eMail";

	public static final String EMAIL_PATTERN = "^[a-z0-9._%+-]{1,128}@[a-z0-9.-]{1,128}$";

	@ISProperty
	public String getTitle();

	public void setTitle(String title);

	@ISProperty(mandatory=true, nullable=false)
	public String getName();

	public void setName(String name);

	@ISProperty
	public String getMiddleName();

	public void setMiddleName(String middleName);

	@ISProperty(mandatory=true, nullable=false)
	public String getSurname();

	public void setSurname(String surname);

	@ISProperty(name=EMAIL_PROPERTY, mandatory=true, nullable=false, regexpr=EMAIL_PATTERN)
	public String getEMail();

	public void setEMail(String eMail);

}
