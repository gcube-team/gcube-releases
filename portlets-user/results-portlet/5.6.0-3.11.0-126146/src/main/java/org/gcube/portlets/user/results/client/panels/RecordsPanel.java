package org.gcube.portlets.user.results.client.panels;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.gcube.portlets.user.results.client.components.ResultItem;
import org.gcube.portlets.user.results.client.constants.ImageConstants;
import org.gcube.portlets.user.results.client.constants.StringConstants;
import org.gcube.portlets.user.results.client.control.Controller;
import org.gcube.portlets.user.results.client.control.FlexTableRowDragController;
import org.gcube.portlets.user.results.client.dialogBox.LoadingPopup;
import org.gcube.portlets.user.results.client.model.ResultObj;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * <code> RecordsPanel </code> class is the UI Component where the records are visible
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version January 2009 (0.1) 
 */
public class RecordsPanel extends Composite {

	public static final int HEADERPART_HEIGHT = 40;
	public static final int HEADERPART_WIDTH = Window.getClientWidth();
	/**
	 * the controller
	 */
	private Controller controller;

	/**
	 * 
	 */
	public static RecordsPanel singleton = null;

	public List<ResultItem> currentDisplayedItems;

	/**
	 * 
	 * @return .
	 */
	public static RecordsPanel get() {
		return singleton;
	}


	/**
	 * loading gif as transparent popup
	 */
	public DialogBox loading = new LoadingPopup(true);

	/**
	 * the panel for the layout of the working space
	 */
	private CellPanel mainLayout = new VerticalPanel();	

	private CellPanel headerpart = new HorizontalPanel();
	private VerticalPanel resultsPanel = new VerticalPanel();

	private HorizontalPanel rpanel = new HorizontalPanel();
	private HorizontalPanel lpanel = new HorizontalPanel();

	private FlexTable table;

	private ListBox collectionsList = new ListBox();
	private FlexTableRowDragController dragController;
	private int n = 0;
	private HTML queryHTML = new HTML();
	boolean bottomPanelAdded = false;


	/**
	 * 
	 * @param c the controller instance
	 */
	public RecordsPanel(Controller c, FlexTableRowDragController dragController) {
		singleton = this;
		this.dragController = dragController;
		controller = c;
		table = new FlexTable();
		table.setCellPadding(10);

		mainLayout.setStyleName("recordsPanel");
		mainLayout.setSize("90%", "100%");

		headerpart.setWidth("100%");

		lpanel.setPixelSize(700, HEADERPART_HEIGHT);
		lpanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		lpanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		lpanel.add(queryHTML);

		collectionsList.addItem("collection prova");
		//lpanel.add(collectionsList);

		Image imgToAdd = new Image(ImageConstants.YOU_CAN_DRAG);
		rpanel.add(imgToAdd);

		rpanel.add(new HTML("&nbsp;&nbsp;", true));

		//dragController.makeDraggable(imgToAdd);

		headerpart.add(lpanel);
		headerpart.add(rpanel);
		headerpart.setCellHorizontalAlignment(lpanel, HasAlignment.ALIGN_LEFT);
		headerpart.setCellHorizontalAlignment(rpanel, HasAlignment.ALIGN_RIGHT);
		headerpart.setCellVerticalAlignment(lpanel, HasVerticalAlignment.ALIGN_TOP);
		headerpart.setCellVerticalAlignment(rpanel, HasVerticalAlignment.ALIGN_MIDDLE);



		mainLayout.add(headerpart);	
		mainLayout.add(resultsPanel);

		mainLayout.setCellHeight(headerpart, "30");
		resultsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		resultsPanel.add(table);

		initWidget(mainLayout);
	}

	/**
	 * 
	 * @param toDisplay the query to display
	 */
	public void displayQuery(String toDisplay) {
		if (toDisplay.trim().isEmpty())
			queryHTML.setHTML("<div></div>");
		else {
			String query = "\"" + toDisplay + "\"";
			queryHTML.setHTML("<div style=\"color:gray font-size:12px\"><strong>Your Query: </strong> " + query + "</div>");
		}
	}

	public void addRecord(ResultItem toadd) {
		n++;
		table.setWidget(n, 1, toadd);
	}

	public void displayresults(Vector<ResultObj> results, final int from, final int to, final String currTotal, boolean normalResults) {
		if (StringConstants.DEBUG) {
			for (int i = from; i <= 10; i++) {
				ResultObj obj = new ResultObj();
				obj.setTitle("titolo prova");
				obj.setHtmlText("<table><tr><td>Ethiopia Production Time Series</td></tr>" +
						"<tr><td>Naso</td></tr>" +
				"<tr><td>FAO Fisheries and Aquaculture Department (FAO-FI)</td><tr/></table>");

				ResultItem toadd = new ResultItem(controller, obj, i, new Image(ImageConstants.GIF_ICON), dragController);
				//toadd.viewMetadata();
				addRecord(toadd);
			}	
		}
		else if (! normalResults) {
			displayFieldValues(results, from, to, currTotal);
		}
		else {
			controller.setPageDisplayer("displaying " + from + " - " + to + " of " + currTotal);

			currentDisplayedItems = new LinkedList<ResultItem>();
			for (int i = from; i <= to; i++) {
				if (i > 0 && i <= results.size()) {
					ResultObj obj = results.elementAt(i-1); 
					ResultItem toadd = new ResultItem(controller, obj, i, new Image(ImageConstants.INNER_RECORD_LOADER), dragController);

					currentDisplayedItems.add(toadd);
					addRecord(toadd);
				}
				else if (i >= results.size()) {
					controller.setPageDisplayer("displaying " + from + " - " + results.size());
					break;
				}
			}
			controller.enableMovePageresults(true);
			controller.enableSavQueryButton(true);
		}
		controller.getNewresultset().getLeftPanel().resizeBasket();


		//just the first time
		if (from == 1 && (! bottomPanelAdded) ) {
			addNextPreviousButtons();
			bottomPanelAdded = true;
		}
		
		controller.setCollectionNameVisibility(controller.isCollNameVisible());
	}

	/**
	 * 
	 * @param results
	 * @param from
	 * @param to
	 * @param currTotal
	 */
	public void displayFieldValues(Vector<ResultObj> results, int from, int to, String currTotal) {
		controller.setPageDisplayer("displaying " + from + " - " + to + " of " + currTotal);

		for (int i = from; i <= to; i++) {
			if (i > 0 && i <= results.size()) {
				n++;
				HorizontalPanel sp = new HorizontalPanel();
				sp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				//TODO it was <a>
				final String term = results.get(i-1).getHtmlText();
				HTML result = new HTML("&nbsp;&nbsp;<a>" + term +"</a>");
				result.addStyleName("selectable");
				sp.add(result);
				sp.setPixelSize(650, 20);
				sp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				sp.setStyleName("newresultset-leftpanel-background");
				table.setWidget(n, 1, sp);
				table.setCellPadding(2);

				result.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						AsyncCallback<Void> callback = new AsyncCallback<Void>() {
							public void onFailure(Throwable caught) { controller.hideLoading();	}
							public void onSuccess(Void result) {
								controller.hideLoading();
								Window.Location.reload();
							}							
						};
						controller.showLoading();
						controller.getNewresultset().getModel().getResultService().submitSimpleQuery(term, callback);				
					}

				
				});
			}
			else if (i >= results.size()) {
				controller.setPageDisplayer("displaying " + from + " - " + results.size());
				break;
			}
		}
		controller.enableMovePageresults(false);
		controller.enableSavQueryButton(false);
		controller.getNewresultset().getLeftPanel().resizeBasket();

		//just the first time
		if (from == 1 && (! bottomPanelAdded) ) {
			addNextPreviousButtons();
			bottomPanelAdded = true;
		}
	}

	public void addNextPreviousButtons() {
		controller.addButtonsBottom(mainLayout);
	}

	public void clear() {
		n = 0;
		resultsPanel.clear();
		table.clear();
		resultsPanel.add(table);
	}
	/**
	 * 
	 * @param listbox :  listbox with collection names
	 */
	public void showCollectionListbox(ListBox listbox) {
		HorizontalPanel collectionsPanel = new HorizontalPanel();
		collectionsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		HTML myText = new HTML("Results from:&nbsp;&nbsp; ");
		collectionsPanel.add(myText);
		collectionsPanel.add(listbox);
		lpanel.add(collectionsPanel);
	}



}
