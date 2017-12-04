/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.client.operation;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.tdtemplateoperation.client.DeletableContainer;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdAggregateFunction;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Command;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 10, 2014
 *
 */
public class GroupByAggregateManager {
	
	
	private List<AggregateDataFunctionPanel> aggregatePanels = new ArrayList<AggregateDataFunctionPanel>();
	private DeletableContainer deletableCnt;
	
	/**
	 * @param groupByColumnPanel 
	 * 
	 */
	public GroupByAggregateManager(DeletableContainer deletable) {
		this.deletableCnt = deletable;
	}
	
	/**
	 * 
	 * @param columns
	 * @param functions
	 * @return VerticalLayoutContainer
	 */
	public VerticalLayoutContainer appendAggregate(List<TdColumnData> columns, List<TdAggregateFunction> functions){
		
		final int index = aggregatePanels.size()>0?aggregatePanels.size():0;
		GWT.log("Adding aggregate index "+index);
		
		Command deleteCommand = new Command() {
			@Override
			public void execute() {

				VerticalLayoutContainer panel = getPanelForIndex(index);
				deletableCnt.deleteFired(panel);
				boolean removed = removeAggregateForIndex(index);
				GWT.log("Removed aggregate index "+index +"? "+removed);
			}
		};
		
		AggregateDataFunctionPanel function = new AggregateDataFunctionPanel(index, columns, functions, true, deleteCommand);
		aggregatePanels.add(function);
		return function.getPanel();
	}

	/**
	 * 
	 * @return true if removed, false otherwise
	 */
	public boolean removeLastAggregate(){
		if((aggregatePanels.size()-1)>0){
			aggregatePanels.remove(aggregatePanels.size()-1);
			resetAggregatesIfEmpty();
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @return true if set index as null, false otherwise
	 */
	public boolean removeAggregateForIndex(int index){
		if(aggregatePanels.size()>index){
			aggregatePanels.set(index, null);
			resetAggregatesIfEmpty();
			return true;
		}
		return false;
	}
	
	public AggregateDataFunctionPanel getLastColumnDataFunctionPanel(){
		
		if(aggregatePanels.size()>0)
			return aggregatePanels.get(aggregatePanels.size()-1);
		
		return null;
	}
	
	public VerticalLayoutContainer getPanelForIndex(int index){
		
		if(aggregatePanels.size()>index)
			return aggregatePanels.get(index).getPanel();
		
		return null;
	}
	
	private void resetAggregatesIfEmpty(){
		
		boolean isEmpty = true;
		
		for (AggregateDataFunctionPanel dfp : aggregatePanels) {
			if(dfp!=null){
				isEmpty = false;
				break;
			}
		}
		
		if(isEmpty)
			aggregatePanels.clear();
	}
	
	public boolean validate(List<TdColumnData> selectedColumns){
		
		boolean isValid = true;
		for (AggregateDataFunctionPanel dfp : aggregatePanels) {
			if(dfp!=null){
				isValid = dfp.validate(selectedColumns);
				if(!isValid){
					return false;
				}
			}
		}
		
		return isValid;
	}

	public List<AggregateDataFunctionPanel> getAggregatePanels() {
		return aggregatePanels;
	}
}
