package org.gcube.portlets.user.gisviewer.client.layerspanel;


/*
 * Ext GWT 2.2.3 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */

import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.dnd.Insert;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.VerticalPanel;

/**
 * A <code>DropTarget</code> implementation for Grids. Supports both inserts and
 * appends, specified using
 * {@link #setOperation(com.extjs.gxt.ui.client.dnd.DND.Operation)}.
 * <p />
 * Supported drag data:
 * <ul>
 * <li>A single ModelData instance.</li>
 * <li>A List of ModelData instances.</li>
 * <li>A List of TreeStoreModel instances (children are ignored).
 * </ul>
 */
public class LayersDropTarget extends DropTarget {

	protected LayersPanel layersPanel;
	private int lastPosition = -1;
	static final int spacing = 3;

	/**
	 * Creates a new drop target instance.
	 * 
	 * @param grid the target LayersPanel
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public LayersDropTarget(LayersPanel layersPanel) {
		super(layersPanel);
		this.layersPanel = layersPanel;
	}

	
	@Override
	protected void onDragMove(DNDEvent e) {
		int[] ar = getPosition(e);
		int newPosition = ar[0];
		int newPositionY = ar[1];
		e.setCancelled(false);
		
		if (newPosition!=lastPosition) {
			Insert insert = Insert.get();
			insert.show();
			insert.el().setBounds(layersPanel.getAbsoluteLeft(), newPositionY-3, layersPanel.getWidth(), 6);
			
//			if (newPosition==0)
//				System.out.println("All'inizio, prima di " + newPosition);
//			else if (newPosition==that.getItems().size())
//				System.out.println("Alla fine, dopo di " + (newPosition-1));
//			else
//				System.out.println("Tra " + (newPosition-1) + " e " + (newPosition));
			lastPosition = newPosition;
		}
	}
	
	private int[] getPosition(DNDEvent e) {
		int y = e.getClientY();
		int i=0, position=0, positionY = 0;
		
		for (Component c : layersPanel.getItems()) {
			int cy1 = c.getAbsoluteTop();
			int cy2 = cy1+c.getOffsetHeight()+spacing;
			if (cy1<=y && y<=cy2) {
				// if y<average then the object will drop before the i-object, else after.
				if (y <= (cy1+cy2)/2) {
					position = i;
					positionY = cy1 - spacing/2;
				} else {
					position = i+1;
					positionY = cy2 + spacing/2;
				}
				break;
			}
			i++;
		}
		return new int[]{position,positionY};
	}

	@Override
	protected void onDragDrop(DNDEvent event) {				
		int[] ar = getPosition(event);
		int pos = ar[0];
		VerticalPanel vp = event.getData();
		//that.add(hp);
		
		if (pos>=layersPanel.getItemCount())
			layersPanel.add(vp);
		else
			layersPanel.insert(vp, pos);
		layersPanel.layout(true);

		this.layersPanel.updateLayersOrder();
		
//		String str;
//		if (pos==0)
//			str = "All'inizio, prima di " + pos;
//		else if (pos==layersPanel.getItemCount())
//			str = "Alla fine, dopo di " + (pos-1);
//		else
//			str = "Tra " + (pos-1) + " e " + (pos);
//		Info.display("", str);
	}
}
