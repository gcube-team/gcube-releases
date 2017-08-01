package org.gcube.portlets.user.td.mainboxwidget.client.grid;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.mainboxwidget.client.resources.MainboxResources;
import org.gcube.portlets.user.td.widgetcommonevent.shared.GridHeaderOperationId;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * Defines the menu for column on the grid
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class GridHeaderColumnMenu {

	protected final ArrayList<MenuItem> menuItems;

	public GridHeaderColumnMenu() {
		GridHeaderColumnMenuMessages msgs = GWT.create(GridHeaderColumnMenuMessages.class);
		
		menuItems = new ArrayList<MenuItem>();
		
		MenuItem changePositionItem = new MenuItem(msgs.changePositionItem());
		changePositionItem.setId(GridHeaderOperationId.COLUMNPOSITION
				.toString());
		changePositionItem.setIcon(MainboxResources.INSTANCE.columnReorder());
		changePositionItem.setToolTip(msgs.changePositionItemToolTip());
		menuItems.add(changePositionItem);
		
		MenuItem changeLabelItem = new MenuItem(msgs.changeLabelItem());
		changeLabelItem.setId(GridHeaderOperationId.COLUMNLABEL
				.toString());
		changeLabelItem.setIcon(MainboxResources.INSTANCE.columnLabel());
		changeLabelItem.setToolTip(msgs.changeLabelItemToolTip());
		menuItems.add(changeLabelItem);
	
		MenuItem addColumnItem = new MenuItem(msgs.addColumnItem());
		addColumnItem.setId(GridHeaderOperationId.COLUMNADD.toString());
		addColumnItem.setIcon(MainboxResources.INSTANCE.columnAdd());	
		addColumnItem.setToolTip(msgs.addColumnItemToolTip());
		menuItems.add(addColumnItem);

		
		MenuItem deleteColumnItem = new MenuItem(msgs.deleteColumnItem());
		deleteColumnItem.setId(GridHeaderOperationId.COLUMNDELETE.toString());
		deleteColumnItem.setIcon(MainboxResources.INSTANCE.columnDelete());
		deleteColumnItem.setToolTip(msgs.deleteColumnItemToolTip());
		menuItems.add(deleteColumnItem);
		
		MenuItem splitColumnItem = new MenuItem(msgs.splitColumnItem());
		splitColumnItem.setId(GridHeaderOperationId.COLUMNSPLIT.toString());
		splitColumnItem.setIcon(MainboxResources.INSTANCE.columnSplit());
		splitColumnItem.setToolTip(msgs.splitColumnItemToolTip());
		menuItems.add(splitColumnItem);
	
		
		MenuItem mergeColumnItem = new MenuItem(msgs.mergeColumnItem());
		mergeColumnItem.setId(GridHeaderOperationId.COLUMNMERGE.toString());
		mergeColumnItem.setIcon(MainboxResources.INSTANCE.columnMerge());
		mergeColumnItem.setToolTip(msgs.mergeColumnItemToolTip());
		menuItems.add(mergeColumnItem);
		
		
		
		MenuItem changeColumnTypeItem = new MenuItem(msgs.changeColumnTypeItem());
		changeColumnTypeItem.setId(GridHeaderOperationId.COLUMNTYPE.toString());
		changeColumnTypeItem.setIcon(MainboxResources.INSTANCE.columnType());
		changeColumnTypeItem.setToolTip(msgs.changeColumnTypeItemToolTip());
		menuItems.add(changeColumnTypeItem);

		MenuItem filterItem = new MenuItem(msgs.filterItem());
		filterItem.setId(GridHeaderOperationId.COLUMNFILTER.toString());
		filterItem.setIcon(MainboxResources.INSTANCE.columnFilter());
		filterItem.setToolTip(msgs.filterItemToolTip());
		menuItems.add(filterItem);
		
	
		MenuItem replaceBatchItem = new MenuItem(msgs.replaceBatchItem());
		replaceBatchItem.setId(GridHeaderOperationId.COLUMNBATCHREPLACE.toString());
		replaceBatchItem.setIcon(MainboxResources.INSTANCE.columnReplaceBatch());
		replaceBatchItem.setToolTip(msgs.replaceBatchItemToolTip());
		menuItems.add(replaceBatchItem);
		
		MenuItem replaceByExpressionItem = new MenuItem(msgs.replaceByExpressionItem());
		replaceByExpressionItem.setId(GridHeaderOperationId.COLUMNREPLACEBYEXPRESSION.toString());
		replaceByExpressionItem.setIcon(MainboxResources.INSTANCE.columnReplaceByExpression());	
		replaceByExpressionItem.setToolTip(msgs.replaceByExpressionItemToolTip());
		menuItems.add(replaceByExpressionItem);
		
		MenuItem replaceByExternalItem = new MenuItem(msgs.replaceByExternalItem());
		replaceByExternalItem.setId(GridHeaderOperationId.COLUMNREPLACEBYEXTERNAL.toString());
		replaceByExternalItem.setIcon(MainboxResources.INSTANCE.columnReplaceByExternal());	
		replaceByExternalItem.setToolTip(msgs.replaceByExternalItemToolTip());
		menuItems.add(replaceByExternalItem);
				
	}

	/**
	 * 
	 * @return the list of menu items to be added
	 */
	public List<MenuItem> getMenu() {
		return menuItems;
	}
}
