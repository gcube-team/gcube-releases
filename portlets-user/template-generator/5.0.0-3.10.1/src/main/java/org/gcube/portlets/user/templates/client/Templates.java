package org.gcube.portlets.user.templates.client;




import org.gcube.portlets.user.templates.client.model.TemplateModel;
import org.gcube.portlets.user.templates.client.presenter.Presenter;
import org.gcube.portlets.user.templates.client.resources.TourResources;
import org.gcube.portlets.widgets.guidedtour.client.GCUBEGuidedTour;
import org.gcube.portlets.widgets.guidedtour.client.GuidedTourResourceProvider;
import org.gcube.portlets.widgets.guidedtour.resources.client.GuidedTourResource;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Templates implements EntryPoint {

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
	public static final int TEMPLATE_LEFT = 25;
	/**
	 * 
	 */
	public static final int TEMPLATE_TOP = HEADER_HEIGHT + 90;
	/**
	 * 
	 */	
	public static Templates singleton = null;
	/**
	 * 
	 * @return .
	 */
	public static Templates get() {
		return singleton;
	}

	/**
	 * the controller
	 */
	private Presenter presenter;

	private VerticalPanel mainLayout = new VerticalPanel();

	private TitleBar titlebar;

	private HeaderBar header;

	private ScrollPanel bottomScrollerPanel = new ScrollPanel();

	private VerticalPanel bottomPanel = new VerticalPanel();

	private VerticalPanel toolbarPanel = new VerticalPanel();

	private HTML divHidden = new HTML(); 

	private TemplateModel model;


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		singleton = this;
		model = new TemplateModel();


		mainLayout.setWidth("90%");
		mainLayout.setHeight("100%");

		presenter = new Presenter(model);

		titlebar = new TitleBar(presenter);
		header = new HeaderBar(presenter);

		mainLayout.add(titlebar);
		mainLayout.add(header);
		mainLayout.add(toolbarPanel);


		toolbarPanel.setWidth("90%");
		toolbarPanel.setHeight("25px");
		//		bottomScrollerPanel.setStyleName("border");
		//		bottomPanel.setStyleName("border");

		bottomScrollerPanel.add(bottomPanel);

		mainLayout.add(bottomScrollerPanel);


		presenter.addTextToolBar();


		bottomPanel.setWidth("90%");
		bottomPanel.setHeight("100%");



		bottomPanel.add(presenter.getWorkSpacePanel());
		divHidden.setStyleName("border");
		divHidden.setWidth("500px");
		divHidden.setStyleName("d4sRichTextArea");
		divHidden.addStyleName("setVisibilityOff");
		bottomPanel.add(divHidden);

		presenter.setHeader(header);
		presenter.setTitleBar(titlebar);

		// Add image and button to the RootPanel
		RootPanel.get("TemplateGeneratorDIV").add(mainLayout);


//		int scrollerHeight = Window.getClientHeight() - bottomScrollerPanel.getAbsoluteTop();
//
//		bottomScrollerPanel.setPixelSize(Window.getClientWidth()- 150, scrollerHeight);
//
//		Window.addResizeHandler(new ResizeHandler() {
//			@Override
//			public void onResize(ResizeEvent event) {
//				int scrollerHeight = event.getHeight() - bottomScrollerPanel.getAbsoluteTop();
//				bottomScrollerPanel.setPixelSize(Window.getClientWidth()- 150, scrollerHeight);			
//			}
//		});
		checkTour();		
	}
	
	private void checkTour() {
		showGuidedTour();
	}

	private void showGuidedTour() {
		GWT.log("oh");
		GWT.runAsync(GCUBEGuidedTour.class, new RunAsyncCallback() {
			public void onSuccess() {
//				GCUBEGuidedTour gt = new GCUBEGuidedTour("gCube Template Creator", Templates.class.getName(), 
//						"http://www.myguide.com", 780, 450, false, TourLanguage.EN, TourLanguage.IT);  //add as many as TourLanguage you want here
//				
//				gt.s
				GCUBEGuidedTour.showTour(Templates.class.getName(), new GuidedTourResourceProvider() {
					@Override
					public GuidedTourResource getResource() {
						TourResources resources = GWT.create(TourResources.class);
						return resources.quickTour();
					}
				});
			}
			public void onFailure(Throwable caught) {
				Window.alert("Could not check show tour");
			}
		});
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
	public HeaderBar getHeader() {
		return header;
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
	public VerticalPanel getToolbarPanel() {
		return toolbarPanel;
	}

	public HTML getDivHidden() {
		return divHidden;
	}

	public void setDivHidden(HTML divHidden) {
		this.divHidden = divHidden;
	}

	public ScrollPanel getScrollerPanel() {
		return bottomScrollerPanel;
	}
}
