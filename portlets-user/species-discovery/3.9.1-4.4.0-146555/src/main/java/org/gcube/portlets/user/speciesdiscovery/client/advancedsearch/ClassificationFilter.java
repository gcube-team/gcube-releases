package org.gcube.portlets.user.speciesdiscovery.client.advancedsearch;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.shared.DataSourceModel;
import org.gcube.portlets.user.speciesdiscovery.shared.MainTaxonomicRankEnum;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ClassificationFilter extends ColumnContainer implements AdvancedSearchPanelInterface {

	private Button btnResetAllFilters = new Button("Reset Filters");
	private ContentPanel classificationFilterPanel = new ContentPanel();
	private SimpleComboBox<String> scb;
	private String baseClassification = MainTaxonomicRankEnum.CLASS.getLabel(); //is CLASS
	
	public ClassificationFilter() {
		init();
		btnResetAllFilters.setStyleName("button-hyperlink");
	}
	

	private void init() {

		classificationFilterPanel.setHeaderVisible(false);
		classificationFilterPanel.setBodyBorder(false);
		
		initComboRankFilter();
		
		left.add(scb);
		
		scb.setSimpleValue(baseClassification);

		right.add(btnResetAllFilters);

		btnResetAllFilters.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				resetAdvancedFields();
			}
		});
		
		classificationFilterPanel.add(columnContainer);
	}
	
	@Override
	public ContentPanel getPanel() {
		return classificationFilterPanel;
	}

	@Override
	public String getName() {
		return AdvancedSearchPanelEnum.CLASSIFICATION.getLabel();
	}

	@Override
	public void resetAdvancedFields() {
		scb.setSimpleValue(baseClassification);
	}
	
	private SimpleComboBox<String> initComboRankFilter() {

		List<String> ls = new ArrayList<String>();

		for (String rank : MainTaxonomicRankEnum.getListLabels()) ls.add(rank);

		scb = new SimpleComboBox<String>();
		scb.setFieldLabel(ConstantsSpeciesDiscovery.GROUPBYRANK);
		scb.setTypeAhead(true);
		scb.setEditable(false);
		scb.setTriggerAction(TriggerAction.ALL);

		scb.add(ls);

		return scb;
	}
	
	public String getSelectedRank(){
		return this.scb.getSimpleValue();
	}


	@Override
	public ArrayList<DataSourceModel> getAvailablePlugIn() {
		return null;
	}
}
