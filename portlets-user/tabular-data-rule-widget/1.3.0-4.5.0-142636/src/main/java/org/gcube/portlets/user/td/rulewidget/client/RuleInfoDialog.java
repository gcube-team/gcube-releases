package org.gcube.portlets.user.td.rulewidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.rulewidget.client.resources.ResourceBundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class RuleInfoDialog extends Dialog {
	private static final DateTimeFormat sdf = DateTimeFormat
			.getFormat("yyyy-MM-dd HH:mm");

	private RuleDescriptionData ruleDescriptionData;

	private RuleInfoMessages msgs;

	public RuleInfoDialog(RuleDescriptionData ruleDescriptionData) {
		this.ruleDescriptionData = ruleDescriptionData;
		initMessages();
		initWindow();
		create();
	}

	protected void initMessages(){
		msgs = GWT.create(RuleInfoMessages.class);
	}
	
	protected void initWindow() {
		setModal(true);
		setHeadingText(msgs.dialogRuleInfoHead());
		getHeader().setIcon(ResourceBundle.INSTANCE.information());
		setPredefinedButtons(PredefinedButton.OK);
		setHideOnButtonClick(true);
		setButtonAlign(BoxLayoutPack.CENTER);
		setWidth(500);

	}

	protected void create() {
		FieldSet configurationFieldSet = new FieldSet();
		configurationFieldSet.setHeadingText(msgs.configurationFieldSetHead());
		configurationFieldSet.setCollapsible(false);
		configurationFieldSet.setBorders(true);

		VerticalLayoutContainer configurationFieldSetLayout = new VerticalLayoutContainer();
		configurationFieldSet.add(configurationFieldSetLayout,
				new MarginData(0));

		TextField ruleName = new TextField();
		ruleName.setValue(ruleDescriptionData.getName());
		ruleName.setReadOnly(true);
		FieldLabel ruleNameLabel = new FieldLabel(ruleName, msgs.nameLabel());
		configurationFieldSetLayout.add(ruleNameLabel, new VerticalLayoutData(
				1, -1, new Margins(0)));

		TextField ruleScope = new TextField();
		ruleScope.setValue(ruleDescriptionData.getScopeLabel());
		ruleScope.setReadOnly(true);
		FieldLabel ruleScopeLabel = new FieldLabel(ruleScope, msgs.scopeLabel());
		configurationFieldSetLayout.add(ruleScopeLabel, new VerticalLayoutData(
				1, -1, new Margins(0)));

		TextArea ruleDescription = new TextArea();
		ruleDescription.setValue(ruleDescriptionData.getDescription());
		ruleDescription.setReadOnly(true);
		FieldLabel ruleDescriptionLabel = new FieldLabel(ruleDescription,
				msgs.descriptionLabel());
		configurationFieldSetLayout.add(ruleDescriptionLabel,
				new VerticalLayoutData(1, -1, new Margins(0)));

		TextField ruleOwner = new TextField();
		ruleOwner.setValue(ruleDescriptionData.getOwnerLogin());
		ruleOwner.setReadOnly(true);
		FieldLabel ruleOwnerLabel = new FieldLabel(ruleOwner, msgs.ownerLabel());
		configurationFieldSetLayout.add(ruleOwnerLabel, new VerticalLayoutData(
				1, -1, new Margins(0)));

		TextField ruleCreationDate = new TextField();
		ruleCreationDate.setValue(sdf.format(ruleDescriptionData
				.getCreationDate()));
		ruleCreationDate.setReadOnly(true);
		FieldLabel ruleCreationDateLabel = new FieldLabel(ruleCreationDate,
				msgs.creationDateLabel());
		configurationFieldSetLayout.add(ruleCreationDateLabel,
				new VerticalLayoutData(1, -1, new Margins(0)));

		TextArea ruleExpression = new TextArea();
		ruleExpression.setValue(ruleDescriptionData.getReadableExpression());
		ruleExpression.setReadOnly(true);
		ruleExpression.setHeight("82px");
		FieldLabel ruleExpressionLabel = new FieldLabel(ruleExpression,
				msgs.expressionLabel());
		configurationFieldSetLayout.add(ruleExpressionLabel,
				new VerticalLayoutData(1, -1, new Margins(0)));

		add(configurationFieldSet, new MarginData(0));

	}

}
