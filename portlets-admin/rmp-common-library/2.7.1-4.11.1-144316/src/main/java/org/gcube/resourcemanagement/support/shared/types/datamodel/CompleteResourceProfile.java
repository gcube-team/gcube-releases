/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ResourceProfile.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.shared.types.datamodel;

import java.io.Serializable;

import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The profile of resources is represented by its
 * xml and html representations and its title (the name
 * or the ID) and the type (GHN, RI, ...).
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
@SuppressWarnings("serial")
public class CompleteResourceProfile implements Serializable {
	private String xmlRepresentation = null;
	private String htmlRepresentation = null;
	private ResourceTypeDecorator type = null;
	private String title = null;
	private String ID = null;

	/**
	 * @deprecated for serialization only
	 */
	public CompleteResourceProfile() {
	}


	public CompleteResourceProfile(String ID, ResourceTypeDecorator type, String title, String xmlRepresentation,
			String htmlRepresentation) {
		super();
		this.ID = ID;
		this.type = type;
		this.title = title;
		this.xmlRepresentation = xmlRepresentation;
		this.htmlRepresentation = htmlRepresentation;
	}


	public String getXmlRepresentation() {
		return xmlRepresentation;
	}


	public String getHtmlRepresentation() {
		return htmlRepresentation;
	}


	public ResourceTypeDecorator getType() {
		return type;
	}


	public String getTitle() {
		return title;
	}
	
	public String getID() {
		return ID;
	}

}
