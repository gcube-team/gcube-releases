package org.gcube.portlets.user.reportgenerator.client;

import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;
import org.gcube.portlets.widgets.sessionchecker.client.CheckSession;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;



/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * 
 * <code> ReportGeneretor </code> class is the Entry point class, defines the main layout of the UI
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it

 */
public class ReportGenerator implements EntryPoint {

	/**
	 * 
	 */
	public static final int HEADER_HEIGHT = 25;
	/**
	 * 
	 */
	public static final int PAGE_LEFT = 5;
	/**
	 * 
	 */
	public static final int TOOLBOX_LEFT = 15;
	/**
	 * 
	 */
	public static final int TEMPLATE_LEFT = 230;
	/**
	 * 
	 */
	public static final int TEMPLATE_TOP = HEADER_HEIGHT + 60;
	/**
	 * 
	 */	
	public static ReportGenerator singleton = null;
	/**
	 * 
	 * @return .
	 */
	public static ReportGenerator get() {
		return singleton;
	}
	/**
	 * the controller
	 */
	private Presenter presenter;

	private VerticalPanel mainLayout = new VerticalPanel();

	private ToolboxPanel toolBoxPanel;

	private Headerbar header;

	private TitleBar titlebar;

	private HorizontalPanel exportResultsPanel = new HorizontalPanel();
	
	private WorkspacePanel workSpacePanel;

	private VerticalPanel eastPanel = new VerticalPanel();

	private VerticalPanel toolbarPanel = new VerticalPanel();

	private ScrollPanel bottomScrollerPanel = new ScrollPanel();
	
	private HTML divHidden = new HTML(); 
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
				singleton = this;
				presenter = new Presenter();
				mainLayout.setWidth("95%");
				mainLayout.setHeight("100%");
				//
				workSpacePanel = new WorkspacePanel(presenter);
				titlebar = new TitleBar(presenter);
				header = new Headerbar(presenter);
				toolBoxPanel = new ToolboxPanel();
		
				presenter.setHeader(header);
				presenter.setTitleBar(titlebar);
				presenter.setWp(workSpacePanel);
				presenter.setToolBoxPanel(toolBoxPanel);
				presenter.setExportsPanel(exportResultsPanel);
		
				mainLayout.add(titlebar);
				mainLayout.add(header);
				mainLayout.add(toolbarPanel);
		
				toolbarPanel.setWidth("100%");
				toolbarPanel.setHeight("40");
				
				CellPanel cellPanel = new HorizontalPanel();
				cellPanel.setStyleName("cella");
				cellPanel.add(toolBoxPanel);
				cellPanel.add(bottomScrollerPanel);
		
				cellPanel.setCellWidth(toolBoxPanel, "10px");
				
				mainLayout.add(cellPanel);
		
				divHidden.setStyleName("d4sFrame");
				divHidden.setWidth("750px");
				divHidden.setStyleName("d4sRichTextArea");
				divHidden.addStyleName("hasRichTextToolbar");
				divHidden.addStyleName("setVisibilityOff");
		
				eastPanel.add(exportResultsPanel);
				eastPanel.add(workSpacePanel);
				eastPanel.add(divHidden);
				bottomScrollerPanel.add(eastPanel);
				presenter.addTextToolBar(true);
		
				// Add image and button to the RootPanel
				RootPanel.get("ReportGeneratorDIV").add(mainLayout);
				//if you do not need to something when the session expire
				CheckSession.getInstance().startPolling();
				
	}
	

	/**
	 * 
	 * @return .
	 */
	public VerticalPanel getMainLayout() {
		return mainLayout;
	}

	/**
	 * 
	 * @return .
	 */
	public Headerbar getHeader() {
		return header;
	}
	/**
	 * 
	 * @return .
	 */
	public WorkspacePanel getWorkSpacePanel() {
		return workSpacePanel;
	}

	/**
	 * 
	 * @return .
	 */
	public ToolboxPanel getToolBoxPanel() {
		return toolBoxPanel;
	}
	/**
	 * 
	 * @return .
	 */
	public VerticalPanel getToolbarPanel() {
		return toolbarPanel;
	}

	/**
	 * 
	 * @return .
	 */
	public TitleBar getTitleHeader() {
		return titlebar;
	}

	/**
	 * 
	 * @return .
	 */
	public HTML getDivHidden() {
		return divHidden;
	}

	/**
	 * 
	 * @param divHidden .
	 */
	public void setDivHidden(HTML divHidden) {
		this.divHidden = divHidden;
	}

	public ScrollPanel getScrollerPanel() {
		return bottomScrollerPanel;
	}
}
