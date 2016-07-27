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
 * Filename: ResourceDescriptor.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.shared.types.datamodel;

import java.io.Serializable;

import org.gcube.resourcemanagement.support.shared.exceptions.InvalidParameterException;
import org.gcube.resourcemanagement.support.shared.util.Assertion;




import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * All the resources that want to access resource management
 * operations must be described through this data type.
 * @author Massimiliano Assante (ISTI-CNR)
 * @author Daniele Strollo 
 */
@SuppressWarnings("serial")
public class ResourceDescriptor extends BaseModelData implements Serializable {

	public ResourceDescriptor() {
		super();
	}

	/**
	 * Creates a ResourceDescriptor
	 * @param type mandatory
	 * @param subtype can be null
	 * @param ID the identifier of the resource (mandatory).
	 * @param name the short name assigned to the resource (mandatory).
	 * @throws InvalidParameterException
	 */
	public ResourceDescriptor(String type, String subtype, String id, String name) throws InvalidParameterException {
		super();
		Assertion<InvalidParameterException> checker = new Assertion<InvalidParameterException>();
		checker.validate(name != null && name.length() > 0, new InvalidParameterException("The ghnName is null or empty"));
		checker.validate(id != null && id.length() > 0, new InvalidParameterException("The ID is null or empty"));
		checker.validate(type != null && type.length() > 0, new InvalidParameterException("The type is null or empty"));

		this.setSubtype(subtype);
		this.setType(type);
		this.setID(id);
		this.setName(name);
	}

	public final String getType() {
		return get("type");
	}

	public final String getSubtype() {
		return get("subtype");
	}

	public final String getID() {
		return get("ID");
	}

	public final String getName() {
		return get("name");
	}

	public final void setType(final String type) {
		if (type != null) {
			set("type", type.trim());
		}
	}

	public final void setSubtype(final String subtype) {
		if (subtype != null) {
			set("subtype", subtype.trim());
		}
	}

	public final void setID(final String id) {
		if (id != null) {
			set("ID", id.trim());
		}
	}

	public final void setName(final String name) {
		if (name != null) {
			set("name", name.trim());
		}
	}

	public final void addProperty(final String property, final Object value) {
		set(property, value);
	}

	public final Object getProperty(final String property) {
		return get(property);
	}

	@Override
	public String toString() {
		return "ResourceDescriptor [getType()=" + getType() + ", getSubtype()="
				+ getSubtype() + ", getID()=" + getID() + ", getName()="
				+ getName() + "]";
	}
	
	
}
