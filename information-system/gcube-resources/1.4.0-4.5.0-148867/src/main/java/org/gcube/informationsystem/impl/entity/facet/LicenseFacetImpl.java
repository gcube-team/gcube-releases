/**
 * 
 */
package org.gcube.informationsystem.impl.entity.facet;

import java.net.URL;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.entity.facet.LicenseFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=LicenseFacet.NAME)
public class LicenseFacetImpl extends FacetImpl implements LicenseFacet {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 415765991191430747L;
	
	protected String name;
	protected URL textURL;
	
	/**
	 * @return the value
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param value the value to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the textURL
	 */
	public URL getTextURL() {
		return textURL;
	}

	/**
	 * @param textURL the textURL to set
	 */
	public void setTextURL(URL textURL) {
		this.textURL = textURL;
	}

}
