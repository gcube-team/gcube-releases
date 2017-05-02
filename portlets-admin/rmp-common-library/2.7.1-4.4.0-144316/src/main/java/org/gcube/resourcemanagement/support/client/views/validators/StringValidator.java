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

package org.gcube.resourcemanagement.support.client.views.validators;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class StringValidator implements Validator {
	private int maxLenght = 256;
	private boolean emptyAllowed = false;

	public StringValidator(final int maxLenght, final boolean emptyAllowed) {
		this.maxLenght = maxLenght;
		this.emptyAllowed = emptyAllowed;
	}

	public final String validate(final Field<?> field, final String value) {
		if (!emptyAllowed && (value == null || value.trim().length() == 0)) {
			return "The field value is invalid. Empty or null value not allowed.";
		}
		if (value != null && value.length() > this.maxLenght) {
			return "The value is too long. Limit of " + this.maxLenght + " chars exceeded.";
		}
		return null;
	}
}


