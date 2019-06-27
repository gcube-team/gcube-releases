/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.rule;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.tdtemplate.client.templatecreator.smart.SmartButtonDescription;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.UserActionInterface;
import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.google.gwt.core.shared.GWT;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 5, 2014
 *
 */
public class RulesDescriptionViewerMng {

	private List<RuleStore> rulesStore = new ArrayList<RuleStore>();
	private int columnIndex;
	private LayoutContainer mainLC = new LayoutContainer();
	private VerticalPanel mainVP = new VerticalPanel();
	
	/**
	 * 
	 */
	public RulesDescriptionViewerMng(int columnIndex) {
		this.columnIndex = columnIndex;
		this.resetLayout();
		initLayout();
	}
	
	
	/**
	 * 
	 */
	private void initLayout() {
		mainLC.add(mainVP);
	}


	/**
	 * 
	 */
	private void resetLayout() {
		mainVP.removeAll();
	}


	public void resetRulesExpressions(){
		this.rulesStore.clear();
		resetLayout();
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 * @throws Exception
	 */
	public boolean deleteRule(int index) {
		try {
			validateRange(index);
		} catch (Exception e) {
			GWT.log("Column index is out of range, skipping delete rule");
			return false;
		}
		
		RuleStore ruleStore = rulesStore.remove(index);
		boolean deleted = ruleStore!=null?true:false;
		
		if(deleted){
			repaintPanels();
		}
		
		return deleted;
	}
	

	/**
	 * 
	 */
	private void repaintPanels() {
		
		resetLayout();
		
		for (int i=0; i<rulesStore.size(); i++) {
			RuleStore rule = rulesStore.get(i);
			addRuleDescription(i, rule.title, rule.expression.getHumanDescription(), rule.caller, rule.isDeletable, rule.isEditable);
		}
		
		mainLC.layout(true);
		
	}


	private void validateRange(int columnIndex) throws Exception{
		if(rulesStore.size()<(columnIndex+1))
			throw new Exception("Column index is out of range");
	}

	
	public void addRule(String caption, TemplateExpression expression, UserActionInterface caller, boolean isEditable, boolean isDeletable){
		this.rulesStore.add(new RuleStore(caption, expression, caller, isDeletable, isEditable));
		addRuleDescription(rulesStore.size()-1, caption, expression.getHumanDescription(), caller, isDeletable, isEditable);
		
	}
	
	public void updateRule(int index, String caption, TemplateExpression expression, UserActionInterface caller, boolean isEditable, boolean isDeletable) throws Exception{
		
		validateRange(index);
		RuleStore rule = new RuleStore(caption, expression, caller, isDeletable, isEditable);
		this.rulesStore.set(index, rule);
		repaintPanels();
	}
	
	private void addRuleDescription(int index, String title, String descr, UserActionInterface caller, boolean isDeletable, boolean isEditable){
		
		SmartButtonDescription button = new SmartButtonDescription(index, title, descr, caller, isDeletable, isEditable);
		button.setStyleAttribute("margin-top", "2px");
		mainVP.add(button);
		mainLC.layout(true);
	}
	
	public LayoutContainer getPanel(){
		return mainLC;
	}
	
	public int size(){
		return rulesStore.size();
	}


	public int getColumnIndex() {
		return columnIndex;
	}


	/**
	 * @return
	 */
	public List<TemplateExpression> getTemplateColumnExpressions() {
		
		List<TemplateExpression> expressions = new ArrayList<TemplateExpression>();
		GWT.log("Get TemplateColumnExpressions, rule store size is: "+rulesStore.size());
		for (RuleStore store : rulesStore)
			expressions.add(store.expression);
		
		return expressions;
	}
	
}
