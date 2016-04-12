package org.gcube.portlets.user.results.client.panels;

import java.util.List;

import org.gcube.portlets.user.results.client.components.ResultItem;
import org.gcube.portlets.user.results.client.control.FlexTableRowDragController;
import org.gcube.portlets.user.results.client.draggables.DraggableRow;
import org.gcube.portlets.user.results.client.model.BasketModelItemType;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author massi
 *
 */
public class RecordSubpanel extends Composite {

	private FlexTable flextable = new FlexTable();
	private VerticalPanel mainLayout = new VerticalPanel();

	public RecordSubpanel(List<DraggableRow> draggables, FlexTableRowDragController tableRowDragController, boolean useScroller) {		

		mainLayout.add(flextable);
		//flextable.setStyleName("frame");
		mainLayout.setSpacing(1);
		int i = 0;
		for (final DraggableRow dRow : draggables) {
			
			Image handle = dRow.getDragHandle();
			flextable.setWidget(i, 0, handle);				
			flextable.setWidget(i, 1, dRow);		
		//	if (! (dRow.getType() == BasketModelItemType.METADATA || dRow.getType() == BasketModelItemType.ANNOTATION))
			//if (! (dRow.getType() == BasketModelItemType.ANNOTATION))
			tableRowDragController.makeDraggable(dRow, handle);
		
			//flextable.getRowFormatter().setStylePrimaryName(i, "newresultset-resultItem");
			i++;
		}
		if (draggables.size() > 10) {
		
			ScrollPanel scroller = new ScrollPanel();
			scroller.setPixelSize(ResultItem.WIDTH - 120, 250);
			scroller.add(mainLayout);
			initWidget(scroller);
		}
		else
			initWidget(mainLayout);
	}

	public FlexTable getFlexTable() {
		return flextable;
	}

	public void setTable(FlexTable table) {
		this.flextable = table;
	}


}
