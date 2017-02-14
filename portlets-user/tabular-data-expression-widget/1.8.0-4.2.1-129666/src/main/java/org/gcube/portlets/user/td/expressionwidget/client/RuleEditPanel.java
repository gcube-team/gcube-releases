package org.gcube.portlets.user.td.expressionwidget.client;

import org.gcube.portlets.user.td.expressionwidget.client.resources.ExpressionResources;
import org.gcube.portlets.user.td.expressionwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class RuleEditPanel extends FramedPanel {
	private static final String RULE_DESCRIPTION_HEIGHT = "44px";
	private static final String WIDTH = "648px";
	private static final String HEIGHT = "104px";

	private TextButton btnUpdate;
	private TextButton btnClose;

	private RuleEditDialog parentRuleEditDialog;

	private RuleDescriptionData initialRuleDescriptionData;

	private TextField ruleName;
	private TextArea ruleDescription;

	public RuleEditPanel(RuleEditDialog ruleEditDialog,
			RuleDescriptionData ruleDescriptionData, EventBus eventBus) {
		super();
		setWidth(WIDTH);
		setHeight(HEIGHT);
		this.parentRuleEditDialog = ruleEditDialog;
		this.initialRuleDescriptionData = ruleDescriptionData;

		create();
	}

	protected void create() {
		forceLayoutOnResize = true;

		setBodyBorder(false);
		setHeaderVisible(false);

		VerticalLayoutContainer basicLayout = new VerticalLayoutContainer();
		basicLayout.setAdjustForScroll(true);

		FieldSet properties = null;
		VerticalLayoutContainer propertiesLayout;

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		properties = new FieldSet();
		properties.setHeadingText("Properties");
		properties.setCollapsible(false);

		propertiesLayout = new VerticalLayoutContainer();
		properties.add(propertiesLayout);

		createColumnMockUp(propertiesLayout);

		btnUpdate = new TextButton("Save");
		btnUpdate.setIcon(ExpressionResources.INSTANCE.ruleEdit());
		btnUpdate.setIconAlign(IconAlign.RIGHT);
		btnUpdate.setToolTip("Save rule decription");
		btnUpdate.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Update");
				updateRuleDescription();

			}
		});

		btnClose = new TextButton("Close");
		btnClose.setIcon(ExpressionResources.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setToolTip("Cancel rule");
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();
			}
		});

		flowButton.add(btnUpdate, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		flowButton.add(btnClose, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		basicLayout.add(properties, new VerticalLayoutData(1, -1,
				new Margins(1)));
		basicLayout.add(flowButton, new VerticalLayoutData(1, 36, new Margins(
				5, 2, 5, 2)));
		add(basicLayout);

	}

	private void updateRuleDescription() {
		String name=ruleName.getCurrentValue();
		if(name==null|| name.isEmpty()){
			Log.debug("Attention, enter a valid name for the rule!");
			UtilsGXT3.alert("Attention",
					"Enter a valid name for the rule!");
			
		} else {
			String description=ruleDescription.getCurrentValue();
			if(description==null|| description.isEmpty()){
				Log.debug("Attention, enter a valid description for the rule!");
				UtilsGXT3.alert("Attention",
						"Enter a valid description for the rule!");
			} else {
				initialRuleDescriptionData.setName(name);
				initialRuleDescriptionData.setDescription(description);
				parentRuleEditDialog.updateColumnRule(initialRuleDescriptionData);
			}
		}
		
	
	}


	private void createColumnMockUp(VerticalLayoutContainer propertiesLayout) {
		ruleName = new TextField();
		ruleName.setToolTip("Rule Name");
		if (initialRuleDescriptionData != null) {
			ruleName.setValue(initialRuleDescriptionData.getName());
		}
		FieldLabel ruleNameLabel = new FieldLabel(ruleName, "Rule Name");

		ruleDescription = new TextArea();
		ruleDescription.setHeight(RULE_DESCRIPTION_HEIGHT);
		ruleDescription.setToolTip("Rule Description");
		if (initialRuleDescriptionData != null) {
			ruleDescription.setValue(initialRuleDescriptionData
					.getDescription());
		}
		FieldLabel ruleDescriptionLabel = new FieldLabel(ruleDescription,
				"Rule Description");

		propertiesLayout.add(ruleNameLabel, new VerticalLayoutData(1, -1,
				new Margins(0)));
		propertiesLayout.add(ruleDescriptionLabel, new VerticalLayoutData(1,
				-1, new Margins(0)));

	}

	protected void close() {
		if (parentRuleEditDialog != null) {
			parentRuleEditDialog.close();
		}

	}

}
