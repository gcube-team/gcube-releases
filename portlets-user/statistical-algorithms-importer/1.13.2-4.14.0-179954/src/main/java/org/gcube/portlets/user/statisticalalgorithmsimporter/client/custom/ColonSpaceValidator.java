package org.gcube.portlets.user.statisticalalgorithmsimporter.client.custom;

import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;

/**
 * 
 * @author Giancarlo Panichi 
 *
 *
 */
public class ColonSpaceValidator extends AbstractValidator<String> {

	@Override
	public List<EditorError> validate(Editor<String> editor, String value) {
		if (value != null && !value.isEmpty() && value.contains(": ")) {
			return createError(editor, "Invalid sequence of characters=': '", value);
		}
		return null;
	}

}
