package org.gcube.portlets.user.td.expressionwidget.shared.rule;

import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface RuleDescriptionDataPropertiesCombo extends
		PropertyAccess<RuleDescriptionData> {

	@Path("id")
	ModelKeyProvider<RuleDescriptionData> id();

	LabelProvider<RuleDescriptionData> name();

	@Path("name")
	ValueProvider<RuleDescriptionData, String> nameProv();
}
