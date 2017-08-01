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
 * Filename: XMLValidator.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.sepeditor.client.forms.validators;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class XMLValidator implements Validator {
	private String rootNode = "body";

	public XMLValidator(final String rootNode) {
		this.rootNode = rootNode;
	}

	public final String validate(final Field<?> field, final String value) {
		try {
			XMLParser.parse("<" + this.rootNode + ">" + value + "</" + this.rootNode + ">");
		} catch (DOMParseException e) {
			return "Not a valid XML document";
		}
		return null;
	}
}


