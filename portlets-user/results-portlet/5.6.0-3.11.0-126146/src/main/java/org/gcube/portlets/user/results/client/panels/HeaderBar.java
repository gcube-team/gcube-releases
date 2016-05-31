package org.gcube.portlets.user.results.client.panels;

import java.util.List;

import org.gcube.portlets.user.gcubewidgets.client.GCubePanel;
import org.gcube.portlets.user.results.client.ResultsDisplayer;
import org.gcube.portlets.user.results.client.components.TreeNode;
import org.gcube.portlets.user.results.client.constants.ImageConstants;
import org.gcube.portlets.user.results.client.constants.StringConstants;
import org.gcube.portlets.user.results.client.control.Controller;
import org.gcube.portlets.user.results.client.dialogBox.SaveQueryPopup;
import org.gcube.portlets.user.results.client.util.QueryDescriptor;
import org.gcube.portlets.user.results.client.util.QuerySearchType;
import org.gcube.portlets.widget.collectionsindexedwords.client.IndexVisualisationPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;


/**
 * <code> HeaderBar </code> class is the top bar component of the UI 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * 
 * @version April 2011 (1.0) 
 */
public class HeaderBar extends Composite {


	private Controller controller;

	private static final int HEADER_HEIGHT = 24;

	GCubePanel panel = new GCubePanel("melo");
	/**
	 * mainLayout Panel
	 */
	private CellPanel mainLayout = new HorizontalPanel();
	private CellPanel leftSide = new HorizontalPanel();
	private CellPanel rightSide = new HorizontalPanel();

	private HorizontalPanel buttonsPanel = new HorizontalPanel();
	private HorizontalPanel switchPagePanel = new HorizontalPanel();

	/**
	 * contains the page displayer
	 */
	private HTML pageDisplayer = new HTML(""); 
	private HTML pageBottomDisplayer = new HTML(""); 


	private HTML firstPageButton = new HTML("<img src=\"" + ImageConstants.IMAGE_FIRST_PAGE + "\" />&nbsp;&nbsp;", true);
	private HTML prevButton = new HTML("<img src=\"" + ImageConstants.IMAGE_PREV_PAGE + "\" />&nbsp;&nbsp;", true);
	private HTML prevBottomButton = new HTML("<img src=\"" + ImageConstants.IMAGE_PREV_PAGE + "\" />&nbsp;&nbsp;&nbsp;&nbsp;", true);

	private HTML nextButton = new HTML("&nbsp;&nbsp;&nbsp;<img src=\"" + ImageConstants.IMAGE_NEXT_PAGE + "\" />&nbsp;&nbsp;&nbsp;", true);
	private HTML nextBottomButton = new HTML("&nbsp;&nbsp;&nbsp;<img src=\"" + ImageConstants.IMAGE_NEXT_PAGE + "\" />&nbsp;&nbsp;", true);

	private HTML currentBasket = new HTML("<center>&nbsp;</center>" , true);
	private Button saveButtone = new Button("Save");

	private HTML saveQuerylabel = new HTML("&nbsp;&nbsp;|&nbsp;&nbsp;<font color=\"gray\">Move Query to basket</font>");
	private HTML saveResults = new HTML("&nbsp;&nbsp;|&nbsp;&nbsp;<font color=\"gray\">Move page results to basket</font>");
	private HTML delimeter = new HTML("&nbsp;&nbsp;|&nbsp;&nbsp;");

	private Image tagCloudImage = new Image(GWT.getModuleBaseURL() + "../images/visual.png");

	HandlerRegistration saveQueryLabelRegHandler;

	private CheckBox toggCheckBox = new CheckBox();


	/**
	 * help image
	 */
	public static final String IMAGE_HELP = GWT.getModuleBaseURL() + "gcube_images/help.png";
	/**
	 * help tooltip
	 */
	public static final String IMAGE_TOOLTIP_EN = "Click here to open this portlet User's Guide";

	private ClickHandler saveQuerylabelListener;
	private ClickHandler saveResultsListener;

	/**
	 * Constructor
	 * @param c the controller instance for this UI component
	 */
	public HeaderBar(final Controller control, boolean isFooter) {
		this.controller = control;

		mainLayout.setSize("100%", "" + HEADER_HEIGHT);

		leftSide.setStyleName("gcube_header_background");
		rightSide.setStyleName("gcube_header_background");

		buttonsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		buttonsPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);

		buttonsPanel.add(new HTML("&nbsp;&nbsp"));
		buttonsPanel.add(toggCheckBox);		
		buttonsPanel.add(new HTML("&nbsp;Show collection name"));

		buttonsPanel.add(saveResults);
		buttonsPanel.add(saveQuerylabel);

		buttonsPanel.add(delimeter);

		buttonsPanel.add(tagCloudImage);
		tagCloudImage.setTitle("Click here for a tag cloud visualization of the current query");
		tagCloudImage.getElement().getStyle().setCursor(Cursor.POINTER); 
		tagCloudImage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				//TagCloudPopup tagCloudPopUp = new TagCloudPopup(controller, 600, 450);
				final IndexVisualisationPanel ivp = new IndexVisualisationPanel(Document.get().createUniqueId(), 400, 300);
				AsyncCallback<Integer> getCurrentQueryIndexCallback = new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable arg0) {
						ResultsDisplayer.unmask();
					}

					@Override
					public void onSuccess(Integer arg0) {
						if (arg0 != null) {
							ivp.visualiseQueryStats(arg0, 50);
							ResultsDisplayer.unmask();
						}
						else
							ResultsDisplayer.unmask();
					}
				};control.getNewresultset().getModel().getResultService().getCurrentQueryIndexNumber(getCurrentQueryIndexCallback);
				ResultsDisplayer.mask();

			}
		});

		buttonsPanel.setHeight("" + HEADER_HEIGHT);

		rightSide.add(buttonsPanel);
		rightSide.setSize("100%", "" + HEADER_HEIGHT);
		rightSide.setCellVerticalAlignment(buttonsPanel, HasVerticalAlignment.ALIGN_MIDDLE);

		//rightSide.add(prevButton);

		if (!isFooter)
			mainLayout.add(leftSide);
		mainLayout.add(rightSide);
		//TODO changed at 22 of october
		//Image openTree = new Image(ImageConstants.OPEN_TREE);
		//	openTree.setStyleName("selectable");

		//	SimplePanel lpanel = new SimplePanel();

		switchPagePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		switchPagePanel.add(firstPageButton);
		switchPagePanel.add(prevButton);
		switchPagePanel.add(pageDisplayer);
		switchPagePanel.add(nextButton);

		if (!isFooter) {			
			Image help = new Image(IMAGE_HELP);
			help.setStyleName("button_help");
			help.setTitle(IMAGE_TOOLTIP_EN);

			help.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					String url = "https://gcube.wiki.gcube-system.org/gcube/index.php/User%27s_Guide";
					int width = Window.getClientWidth();
					int height = Window.getClientHeight();
					int winWidth = (int) (Window.getClientWidth() * 0.8);
					int winHeight = (int) (Window.getClientHeight() * 0.7);
					int left = (width - winWidth) / 2;
					int top = (height - winHeight) / 2;
					Window.open(url, null,"left=" + left + "top" + top + ", width=" + winWidth + ", height=" + winHeight + ", resizable=yes, scrollbars=yes, status=yes");			
				}
			});

			switchPagePanel.add(help);
		}

		rightSide.add(switchPagePanel);
		rightSide.setCellHorizontalAlignment(switchPagePanel, HasAlignment.ALIGN_RIGHT);

		prevButton.setTitle(StringConstants.PREV_TOOLTIP_EN);
		nextButton.setTitle(StringConstants.NEXT_TOOLTIP_EN);
		firstPageButton.setTitle(StringConstants.FIRST_TOOLTIP_EN);

		//initial set the switch page buttons not visible

		hideNextButton();
		hidePrevButton();
		hideFirstPageButton();

		if (!isFooter) {
			SimplePanel rpanel = new SimplePanel();
			//lpanel.add(openTree);
			rpanel.add(saveButtone);



			//	leftSide.add(lpanel);
			leftSide.add(currentBasket);
			leftSide.add(rpanel);

			//	leftSide.setCellWidth(lpanel, "50");
			leftSide.setCellWidth(currentBasket, "100%");
			leftSide.setCellWidth(rpanel, "70");


			//	leftSide.setCellVerticalAlignment(lpanel, HasVerticalAlignment.ALIGN_MIDDLE);
			leftSide.setCellVerticalAlignment(currentBasket, HasVerticalAlignment.ALIGN_MIDDLE);
			leftSide.setCellVerticalAlignment(rpanel, HasVerticalAlignment.ALIGN_MIDDLE);		
			int width = LeftPanel.LEFTPANEL_WIDTH + 8;
			leftSide.setPixelSize(width, 28);
			//rightSide.setPixelSize(width, HEADER_HEIGHT);
			mainLayout.setCellWidth(leftSide, ""+width);
		}
		mainLayout.setCellWidth(rightSide, "100%");
		initWidget(mainLayout);		



		saveButtone.addClickHandler(new ClickHandler()  {
			public void onClick(ClickEvent event) {
				controller.saveBasket();
				enableSaveButton(false);
			}
		});

		nextButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) { controller.nextPageButtonClicked(); }
		});		

		prevButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) { controller.prevPageButtonClicked(); }		
		});

		firstPageButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) { controller.firstPageButtonClicked(); }		
		});


		toggCheckBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				controller.setCollectionNameVisibility(toggCheckBox.getValue());
			}
		});

		enableSaveButton(false);

		AsyncCallback<String> bcallback = new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {	}

			public void onSuccess(final String default_basketID) {
				AsyncCallback<TreeNode> callback = new AsyncCallback<TreeNode>() {
					public void onFailure(Throwable caught) {	}

					public void onSuccess(TreeNode result) {			
						control.openFolder(result.getId());
					}
				};	
				//-1 means that a basket already exists in the session
				if (! default_basketID.equals("-1"))
					control.getNewresultset().getModel().getResultService().getWorkspaceTree(callback);

			}
		};		
		control.getNewresultset().getModel().getResultService().getDefaultBasket(bcallback);

		saveQuerylabelListener = new ClickHandler() {
			public void onClick(final ClickEvent event) {

				AsyncCallback<QueryDescriptor> callback = new AsyncCallback<QueryDescriptor>() {
					public void onFailure(Throwable caught) {	}					
					public void onSuccess(final QueryDescriptor qDesc) {
						//String suggestion = description.indexOf("is:")
						//if (qDesc.getType() == QuerySearchType.BROWSE)
						//	Window.alert("There is no query to save, did you perform a browse?");
						if ((qDesc.getType() == QuerySearchType.CQL_QUERY)) {
							Window.alert("This type of query cannot be saved to the workspace");
						}
						else {	
							String suggestedQuery = qDesc.getSimpleTerm();
							if (qDesc.getSimpleTerm() != null) {
								suggestedQuery += " ON ";
								if (qDesc.getType() == QuerySearchType.GENERIC) {
									suggestedQuery += " ALL collections";
								}
								else {
									List<String> selCols = qDesc.getSelectedCollections();

									for (int i = 0; i < selCols.size(); i++) {
										suggestedQuery += selCols.get(i);
										if (i != selCols.size()-1)
											suggestedQuery += ", ";
									}		
								}
							} else if (qDesc.getDescription() != null) { //then is a Quick Search
								suggestedQuery = qDesc.getDescription();
							}
							else {
								suggestedQuery = "";
							}				
							SaveQueryPopup popup = new SaveQueryPopup(controller, false, suggestedQuery, qDesc.getDescription(), qDesc.getType());
							popup.setStyleName("gwt-DialogBox");

							popup.setPopupPosition(saveQuerylabel.getAbsoluteLeft() + 10, saveQuerylabel.getAbsoluteTop() + 15);
							popup.show();
							//popup.setFocus();
						}
					}
				};		
				GWT.log("calling getQueryDescFromSession");
				control.getNewresultset().getModel().getResultService().getQueryDescFromSession(callback);		
			}


		};

		saveResultsListener = new ClickHandler() {
			public void onClick( ClickEvent event) {
				control.addPageResultsToBasket();
			}
		};
	}

	/**
	 * changes the pages label in the UI : e.g. Page x of y
	 * @param currentPage . 
	 * @param totalPages .
	 */
	public void setPageDisplayer(String html) {
		pageDisplayer.setHTML(html);	
		pageBottomDisplayer.setHTML(html);
	}

	/**
	 * Shows the previous botton in the UI
	 */
	public void showPrevButton() {
		prevButton.removeStyleName("setVisibilityOff");
		prevButton.addStyleName("setVisibilityOn");
		prevBottomButton.removeStyleName("setVisibilityOff");
		prevBottomButton.addStyleName("setVisibilityOn");
	}

	/**
	 * Shows the previous botton in the UI
	 */
	public void showFirstPageButton() {
		firstPageButton.removeStyleName("setVisibilityOff");
		firstPageButton.addStyleName("setVisibilityOn");

	}
	/**
	 * Shows the next botton in the UI
	 */
	public void showNextButton() {
		nextButton.setStyleName("setVisibilityOn");
		nextBottomButton.setStyleName("setVisibilityOn");
	}

	/**
	 * Hide the previous botton in the UI
	 */
	public void hidePrevButton() {
		prevButton.removeStyleName("setVisibilityOn");
		prevButton.addStyleName("setVisibilityOff");
		prevBottomButton.removeStyleName("setVisibilityOn");
		prevBottomButton.addStyleName("setVisibilityOff");
	}
	/**
	 * Hide the next botton in the UI
	 */
	public void hideNextButton() {
		nextButton.setStyleName("setVisibilityOff");
		nextBottomButton.setStyleName("setVisibilityOff");
	}
	/**
	 * Hide the next botton in the UI
	 */
	public void hideFirstPageButton() {
		firstPageButton.removeStyleName("setVisibilityOn");
		firstPageButton.addStyleName("setVisibilityOff");
	}
	/**
	 * temporary command 
	 * @return the command instance
	 */
	public Command getNullCommand() {
		Command openNothing = new Command() {	

			public void execute() {
				Window.alert("Feature not supported yet");

			}
		};

		return openNothing;
	}

	public void enableSaveButton(boolean enabled) {
		saveButtone.setEnabled(enabled);
	}

	public String getCurrentBasket() {
		return currentBasket.getText();
	}

	public void setCurrentBasket(String html) {
		this.currentBasket.setHTML("<center>" + html + "</center>");
	}

	/**
	 * clone the page switchpanel to place it also at the bottom of the page
	 * @return a new switchpanel to be inserted in the flex table
	 */
	public HorizontalPanel cloneSwitchPagePanel() {
		HorizontalPanel rowPanel = new HorizontalPanel();
		//rowPanel.setPixelSize(rightSide.getOffsetWidth() - 150, 30);
		rowPanel.setWidth("100%");
		Image topOfThePage = new Image(ImageConstants.ARROW_UP);
		topOfThePage.setTitle("top of the page");

		HorizontalPanel switchPagePanel = new HorizontalPanel();
		switchPagePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		switchPagePanel.add(topOfThePage);
		topOfThePage.setStyleName("selectable");
		switchPagePanel.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
		switchPagePanel.add(prevBottomButton);
		switchPagePanel.add(pageBottomDisplayer);
		switchPagePanel.add(nextBottomButton);

		rowPanel.add(switchPagePanel);
		rowPanel.setCellHorizontalAlignment(switchPagePanel, HasAlignment.ALIGN_RIGHT);

		nextBottomButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent source) {
				controller.nextPageButtonClicked();
				DOM.scrollIntoView(RootPanel.get("site-slogan").getElement()); 
			}
		});

		topOfThePage.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent source) { 
				DOM.scrollIntoView(RootPanel.get("site-slogan").getElement()); 
			}
		});

		prevBottomButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent source) { 
				controller.prevPageButtonClicked();
				DOM.scrollIntoView(RootPanel.get("site-slogan").getElement()); 
			}
		});

		return rowPanel;
	}


	public void enableMovePageresults(boolean enable) {
		if (enable ) {
			saveResults.setHTML("&nbsp;&nbsp;|&nbsp;&nbsp;Move page results to basket");
			saveResults.addStyleName("selectable");
			saveResults.addClickHandler(saveResultsListener);
		} else {
			saveResults.setHTML("&nbsp;&nbsp;|&nbsp;&nbsp;<font color=\"gray\">Move page results to basket</font>");
			saveResults.removeStyleName("selectable");
			saveResults = null;
		}
	}

	/**
	 * 
	 * @param control the controller instance
	 */
	public void enableSaveQuery(final Controller control, boolean enable) {
		if (enable) {
			saveQuerylabel.setHTML("&nbsp;&nbsp;|&nbsp;&nbsp;Move Query to basket");
			saveQuerylabel.addStyleName("selectable");
			saveQueryLabelRegHandler = saveQuerylabel.addClickHandler(saveQuerylabelListener);
		} else {
			saveQuerylabel.setHTML("&nbsp;&nbsp;|&nbsp;&nbsp;<font color=\"gray\">Move Query to basket</font>");
			saveQuerylabel.removeStyleName("selectable");
			saveQueryLabelRegHandler.removeHandler();
		}
	}

}
