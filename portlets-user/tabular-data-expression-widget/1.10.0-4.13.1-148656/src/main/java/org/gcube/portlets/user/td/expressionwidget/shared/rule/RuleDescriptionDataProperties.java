package org.gcube.portlets.user.td.expressionwidget.shared.rule;

import java.util.Date;

import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface RuleDescriptionDataProperties extends
		PropertyAccess<RuleDescriptionData> {

	@Path("id")
	ModelKeyProvider<RuleDescriptionData> id();

	ValueProvider<RuleDescriptionData, String> name();

	ValueProvider<RuleDescriptionData, String> scopeLabel();
	
	ValueProvider<RuleDescriptionData, Date> creationDate();

	ValueProvider<RuleDescriptionData, String> description();

	ValueProvider<RuleDescriptionData, String> ownerLogin();

	ValueProvider<RuleDescriptionData, String> readableExpression();

}
