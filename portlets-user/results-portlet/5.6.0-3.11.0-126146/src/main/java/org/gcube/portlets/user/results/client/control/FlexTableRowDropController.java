package org.gcube.portlets.user.results.client.control;

import org.gcube.portlets.user.results.client.draggables.DraggableRow;
import org.gcube.portlets.user.results.client.model.BasketModelItemType;
import org.gcube.portlets.user.results.client.util.MimeTypeImagecreator;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractPositioningDropController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * Allows one or more table rows to be dropped into an existing table.
 */
public final class FlexTableRowDropController extends AbstractPositioningDropController {

	private FlexTable myTable;
	private Controller myController;

	public FlexTableRowDropController(Controller controller, FlexTable flexTable) {
		super(controller.getNewresultset().getLeftPanel().getMainLayout());
		this.myTable = flexTable;
		myController = controller;
	}

	@Override
	public void onDrop(DragContext context) {
		DraggableRow row = (DraggableRow) context.draggable;

		
		if (myController.basketItemExistYet(row.getOid()))
			Window.alert("Sorry, you already added this item in the basket!");
		else {
			/*
			 * check if is dragged a google result, is handled particulary since unfortunately i got to know
			 * the mimetype in a later call after i get the resultset, so I intercept this when an object is dragged into the basket
			 */
			//if (row.getResObject().getMimetype() == MimeTypeImagecreator.GOOGLE)
			//	row.setType(BasketModelItemType.GOOGLE);
			
			super.onDrop(context);
			myController.addBasketItem(myTable, row);
			
		}
	}

	@Override
	public void onEnter(DragContext context) {

		//Window.alert("Entrato");
		super.onEnter(context);
		myController.highlightBasket(true);


	}

	@Override
	public void onLeave(DragContext context) {
		super.onLeave(context);
		myController.highlightBasket(false);
	}

}

