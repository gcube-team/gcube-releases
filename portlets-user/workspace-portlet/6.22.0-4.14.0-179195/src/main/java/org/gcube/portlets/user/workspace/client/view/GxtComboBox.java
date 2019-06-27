package org.gcube.portlets.user.workspace.client.view;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.workspace.client.model.ScopeModel;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class GxtComboBox {

	ListStore<StringNameFilterModel> stringNameFilterModel = null;
	ListStore<ScopeModel> scopeNameModels = null;
	ComboBox<StringNameFilterModel> comboStringFilter = null;
	ComboBox<ScopeModel> comboViewScope = null;

	public GxtComboBox() {

		this.createComboBox();
	}

	private void createComboBox() {

		scopeNameModels = new ListStore<ScopeModel>();
		
		comboViewScope = new ComboBox<ScopeModel>();
		comboViewScope.setEmptyText("Select a Scope...");
		comboViewScope.setDisplayField("name");
		comboViewScope.setStore(scopeNameModels);
		comboViewScope.setTypeAhead(true);
		comboViewScope.setEditable(false);
		comboViewScope.setTriggerAction(TriggerAction.ALL);
		
		comboStringFilter = new ComboBox<StringNameFilterModel>();
//		comboStringFilter.setWidth(150);
		comboStringFilter.setStore(stringNameFilterModel);
		comboStringFilter.setDisplayField("name");
		comboStringFilter.setTypeAhead(true);
		comboStringFilter.setEditable(false);
		comboStringFilter.setTriggerAction(TriggerAction.ALL);
	}
	
	public ComboBox<ScopeModel> getComboViewScope(){
		
		return comboViewScope;
	}
	
	public SimpleComboBox<String> getComboStringFilter() {

		List<String> ls = new ArrayList<String>();
		ls.add("is");
		ls.add("contains");
		ls.add("begin with");
		ls.add("end with");

		SimpleComboBox<String> scb = new SimpleComboBox<String>();
		scb.setEmptyText("Select filter on item name");
		scb.setTypeAhead(true);
		scb.setEditable(false);
		scb.setTriggerAction(TriggerAction.ALL); //Open list items also after the selection

		scb.add(ls);

		return scb;
	}

	public void setListScope(List<ScopeModel> listScope){
		
		this.scopeNameModels.add(listScope);
	}
	
	
//	private List<StringNameFilterModel> getStringFilter() {
//		List<StringNameFilterModel> stringNameFil = new ArrayList<StringNameFilterModel>();
//		stringNameFil.add(new StringNameFilterModel("is"));
//		stringNameFil.add(new StringNameFilterModel("contain"));
//		stringNameFil.add(new StringNameFilterModel("begin"));
//		stringNameFil.add(new StringNameFilterModel("end"));
//		return stringNameFil;
//	}

	public SimpleComboBox<String> getComboNameFilter() {

		List<String> ls = new ArrayList<String>();
		ls.add("Common Name");
		ls.add("Scientific Name");
		ls.add("Common/Scientific Name");

		SimpleComboBox<String> scb = new SimpleComboBox<String>();
		scb.setEmptyText("Select Criteria");
		scb.setTypeAhead(true);
		scb.setEditable(false);
		scb.setTriggerAction(TriggerAction.ALL);

		scb.add(ls);

		return scb;
	}
}