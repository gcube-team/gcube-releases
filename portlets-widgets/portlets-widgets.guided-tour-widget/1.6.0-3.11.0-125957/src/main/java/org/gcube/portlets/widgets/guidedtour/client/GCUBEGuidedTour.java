package org.gcube.portlets.widgets.guidedtour.client;

import java.util.ArrayList;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.widgets.guidedtour.client.breadcrumb.Breadcrumb;
import org.gcube.portlets.widgets.guidedtour.client.steps.TourStep;
import org.gcube.portlets.widgets.guidedtour.client.types.ThemeColor;
import org.gcube.portlets.widgets.guidedtour.resources.client.GuidedTourResource;
import org.gcube.portlets.widgets.guidedtour.shared.TourLanguage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 1.0 Jan 27th 2012
 */
public final class GCUBEGuidedTour {

	private String userGuideLink;
	private String portletName;
	private String className;
	private ArrayList<Composite> steps;
	private int width;
	private int height;
	private int currentStep = 0;
	private TransitionPanel mainContentPanel;
	private HorizontalPanel actionsPanel;
	private Button closeButton = new Button("Close");
	private Button nextButton = new Button("Next");
	private Button prevButton = new Button("Previous");
	private Button guideButton = new Button("User's guide");
	private Breadcrumb bc;
	private boolean mask = false;
	private ThemeColor color = ThemeColor.BLUE;
	CheckBox dontShowAgain = new CheckBox(" Don't show this again");
	TourLanguage[] supportedLanguages = { TourLanguage.EN };
	TourLanguage currentLanguage = TourLanguage.EN;

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private static final TourServiceAsync tourService = GWT.create(TourService.class);


	/**
	 * 
	 * @param caption what you want to append after "Quick tour guide for" in the frame caption
	 * @param className className of your portlet class, it acts as unique id
	 * @param userGuideURL the http url of this application's user guide
	 * @param width window width in pixels
	 * @param height window height in pixels
	 * @param useMask When enabled, the background will be blocked with a semi-transparent panel
	 */
	public GCUBEGuidedTour(String caption, String className, String userGuideURL, int width, int height, boolean useMask, TourLanguage...languages) {
		if (caption == null) throw new IllegalArgumentException("Please provide portlet name");
		if (width < 300) throw new IllegalArgumentException("Please provide width greater than 300px");
		if (height < 200) throw new IllegalArgumentException("Please provide width greater than 200px");
		if (languages != null) 
			supportedLanguages = languages;
		mask = useMask;
		portletName = caption;
		userGuideLink = userGuideURL;
		this.className = className;
		this.width =  width;
		this.height = height;
		this.steps = new ArrayList<Composite>();
		mainContentPanel = new TransitionPanel(width+"px", height+"px", true);
	}
	/**
	 * 
	 * @param caption what you want to append after "Quick tour guide for" in the frame caption
	 * @param className className of your portlet class, it acts as unique id
	 * @param width window width in pixels
	 * @param height window height in pixels
	 * @param useMask When enabled, the background will be blocked with a semi-transparent panel
	 * @param themeColor the frame color of this popup
	 * @param languages the languages supported by this tour
	 */
	public GCUBEGuidedTour(String caption, String className, String userGuideURL, int width, int height, boolean useMask, ThemeColor themeColor, TourLanguage...languages) {
		this(caption, className, userGuideURL, width, height, useMask, languages);
		color = themeColor;		
	}

	/**
	 * @param caption what you want to append after "Quick tour guide for" in the frame caption
	 * @param className className of your portlet class, it acts as unique id
	 * @param width window width in pixels
	 * @param height window height in pixels
	 * @param useMask When enabled, the background will be blocked with a semi-transparent panel
	 * @param themeColor the frame color of this popup
	 * @param steps an ArrayList of steps. Any <class>Composite</class> will do, however we reccomend to use templates, either <class>GCUBETemplate1Text1Image</class> or <class>GCUBETemplate1Text2Image</class> or <class>GCUBETemplate2Text2Image</class>
	 */
	public GCUBEGuidedTour(String caption, String className, String userGuideURL,  int width, int height,  boolean useMask, ThemeColor themeColor, ArrayList<Composite> steps, TourLanguage...languages) {
		this(caption, className, userGuideURL, width, height, useMask, themeColor, languages);
		if (steps == null) 	throw new IllegalArgumentException("Please provide tour steps");
		this.steps = steps;
	}

	/**
	 *  Enable or disable the animation feature. When enabled, the popup will use
	 *  animated transitions when the user clicks next or prev buttons
	 * @param enable true to enable animation, false to disable
	 */
	public void setAnimationEnabled(boolean enable) {
		mainContentPanel.setAnimationEnabled(enable);
	}
	/**
	 * add a Step to the popup, any <Composite> will work
	 * @param stepToAdd
	 */
	public void addStep(Composite stepToAdd) {
		if (steps == null)
			steps = new ArrayList<Composite>();
		if (stepToAdd instanceof TourStep)
			((TourStep) stepToAdd).commit();
		steps.add(stepToAdd);
	}
	/**
	 * trigger the show PopUp 
	 * @param steps the steps to show
	 */
	public void openTour() {
		tourService.showTour(className, new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				GWT.log(""+result);
				if (result) showTour();
			}
			@Override
			public void onFailure(Throwable caught) {				
			}
		});
	}
	
	/**
	 * Creates and shows the tour.
	 */
	protected void showTour()
	{
		final GCubeDialog dialogBox = getDialogBox(portletName, width, height);
		dialogBox.setGlassEnabled(mask);
		dialogBox.center();
		dialogBox.show();
	}
	
	public static void showTour(String className, GuidedTourResourceProvider resourceProvider)
	{
		showTour(className, false, resourceProvider);
	}
	
	public static void showTour(final String className, boolean skipUserPreferences, final GuidedTourResourceProvider resourceProvider)
	{
		if (skipUserPreferences) showTourAsync(className, resourceProvider);
		else {
			tourService.showTour(className, new AsyncCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean result) {
					GWT.log(""+result);
					if (result) showTourAsync(className, resourceProvider);
				}
				@Override
				public void onFailure(Throwable caught) {				
				}
			});
		}
	}
	
	protected static void showTourAsync(final String className, final GuidedTourResourceProvider resourceProvider)
	{
		GWT.runAsync(new RunAsyncCallback() {

			@Override
			public void onSuccess() {
				GuidedTourResource resource = resourceProvider.getResource();

				GCUBEGuidedTour gt = new GCUBEGuidedTour(resource.getTitle(), className, resource.getGuide(), resource.getWidth(), resource.getHeight(), resource.useMask(), resource.getThemeColor(), resource.getLanguages());
				for (TourStep step:resource.getSteps()) gt.addStep(step);
				gt.openTour();

			}

			@Override
			public void onFailure(Throwable reason) {}
		});
	}

	/**
	 * Create the dialog box for the tour.
	 *
	 * @return the new dialog box
	 */
	private GCubeDialog getDialogBox(String caption, int width, int height) {
		bc = new Breadcrumb(getStepNames(), width, color);
		if (steps.size() == currentStep +1)
			nextButton.setVisible(false);
		// Create a dialog box and set the caption text
		final GCubeDialog dialogBox = new GCubeDialog();
		//check if you have to change the color
		if (color != ThemeColor.BLUE) {
			dialogBox.addStyleName(getThemeStyleName(color));
		}
		dialogBox.setGlassStyleName("maskStyle");
		dialogBox.setText("Quick tour guide for " + caption);

		CellPanel mainPanel = new VerticalPanel();
		mainPanel.setStyleName("wizardPanel");
		mainPanel.setWidth("100%");

		//initialize the panel, add the step
		for (Composite step : steps) {
			mainContentPanel.add(step);	
		}

		CellPanel buttonsPanel = getButtonsPanel(dialogBox);



		//buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		mainPanel.setCellHorizontalAlignment(buttonsPanel, HasHorizontalAlignment.ALIGN_RIGHT);

		mainPanel.add(bc);
		actionsPanel = getMoreActionsPanel();
		mainPanel.add(actionsPanel);
		mainPanel.add(mainContentPanel);
		mainPanel.add(buttonsPanel);


		mainPanel.setCellWidth(buttonsPanel, "100%");
		//	mainPanel.setCellWidth(moreActionsPanel, "100%");
		dialogBox.setWidget(mainPanel);
		// Return the dialog box
		return dialogBox;
	}

	private HorizontalPanel getMoreActionsPanel() {

		HorizontalPanel toReturn = new HorizontalPanel();
		toReturn.getElement().getStyle().setPaddingRight(10, Unit.PX);
		toReturn.setWidth("100%");
		toReturn.setHeight("20px");
		toReturn.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		toReturn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		HorizontalPanel p2 = new HorizontalPanel();

		if (supportedLanguages.length > 1) {
			p2.add(new HTML("&nbsp;<span class=\"" + "link " + color.toString().toLowerCase() +	" selected \" >language:</span>"));
			for (int i = 0; i < supportedLanguages.length; i++) {
				String selected = (currentLanguage == supportedLanguages[i]) ? " selected " : "";
				String elem = (currentLanguage == supportedLanguages[i]) ? "span" : "a";
				String lang = "&nbsp;<"+elem+" class=\"" + "link " + color.toString().toLowerCase() + selected + "\" \" href=\"Javascript:;\">" +
						supportedLanguages[i].toString().toLowerCase() + "</"+elem+">";
				if (i != supportedLanguages.length - 1)
					lang += "&nbsp;-";
				HTML langHTML = new HTML(lang);
				final int j = i;
				langHTML.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						switchLanguage(supportedLanguages[j]);						
					}
				});
				p2.add(langHTML);
			}
		}
		toReturn.add(p2);
		return toReturn;
	}
	/**
	 * 
	 * @param language
	 */
	private void switchLanguage(TourLanguage language) {
		currentLanguage = language;
		actionsPanel.clear();
		actionsPanel.add(getMoreActionsPanel());

		for (Composite step : steps) {
			if (step instanceof TourStep) {
				TourStep panel = (TourStep) step;
				panel.switchLanguage(language);
			}	
		}

		bc.switchLanguage(getStepNames(language), currentStep);
	}
	/**
	 * nextStep handler
	 */
	private void nextStep() {
		currentStep++;
		bc.showSelected(currentStep);
		mainContentPanel.setWidget(steps.get(currentStep));	
		prevButton.setEnabled(true);	
		if (steps.size() == currentStep +1) {
			nextButton.setEnabled(false);	
		}
	}
	/**
	 * prevStep handler
	 */
	private void prevStep() {
		currentStep--;
		bc.showSelected(currentStep);
		mainContentPanel.setWidget(steps.get(currentStep));	
		if (currentStep == 0)
			prevButton.setEnabled(false);
		if (steps.size() != currentStep +1) {
			nextButton.setEnabled(true);	
		}
	}

	private CellPanel getButtonsPanel(final GCubeDialog dialog) {
		CellPanel toReturn = new HorizontalPanel();

		//handlers
		nextButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				nextStep();
			}
		});
		prevButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				prevStep();
			}
		});
		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialog.hide();
				if (dontShowAgain.getValue())
					tourService.setNotShowItAgain(className, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
						}
						@Override
						public void onSuccess(Void result) {
						}
					});
			}
		});
		guideButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int width = Window.getClientWidth();
				int height = Window.getClientHeight();
				int winWidth = (int) (Window.getClientWidth() * 0.8);
				int winHeight = (int) (Window.getClientHeight() * 0.7);
				int left = (width - winWidth) / 2;
				int top = (height - winHeight) / 2;
				Window.open(userGuideLink, null,"left=" + left + "top" + top + ", width=" + winWidth + ", height=" + winHeight + ", resizable=yes, scrollbars=yes, status=yes");						

			}
		});
		if (steps.size() < 2) 
			prevButton.setVisible(false);
		prevButton.setEnabled(false);

		//the checkbox

		dontShowAgain.setStyleName("myCheckbox");

		nextButton.setWidth("100px");
		nextButton.setStyleName("wizardButton");
		prevButton.setWidth("100px");
		prevButton.setStyleName("wizardButton");
		closeButton.setWidth("100px");
		closeButton.setStyleName("wizardButton");
		guideButton.setWidth("100px");
		guideButton.setStyleName("wizardButton");


		HorizontalPanel closePanel = new HorizontalPanel();		
		closePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);	
		closePanel.add(guideButton);
		closePanel.add(closeButton);
		closePanel.add(dontShowAgain);	

		HorizontalPanel nextPrevPanel = new HorizontalPanel();
		nextPrevPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		nextPrevPanel.add(prevButton);
		nextPrevPanel.add(nextButton);

		toReturn.setStyleName("buttonPanel");
		toReturn.setWidth("100%");

		toReturn.add(closePanel);
		toReturn.add(nextPrevPanel);

		toReturn.setCellHorizontalAlignment(closePanel, HasHorizontalAlignment.ALIGN_LEFT);
		toReturn.setCellVerticalAlignment(closePanel, HasVerticalAlignment.ALIGN_MIDDLE);
		toReturn.setCellHorizontalAlignment(nextPrevPanel, HasHorizontalAlignment.ALIGN_RIGHT);

		return toReturn;
	}
	/**
	 * 
	 * @return the step titles
	 */
	private ArrayList<String> getStepNames() {
		ArrayList<String> toReturn = new ArrayList<String>();
		for (Composite step: steps) {
			toReturn.add(step.getTitle());
		}
		return toReturn;
	}
	/**
	 * 
	 * @return the step titles
	 */
	private ArrayList<String> getStepNames(TourLanguage language) {
		ArrayList<String> toReturn = new ArrayList<String>();

		for (Composite step: steps) {
			if (step instanceof TourStep) {
				TourStep panel = (TourStep) step;
				toReturn.add(panel.getTitle());
				GWT.log(panel.getTitle());
			}	

		}
		return toReturn;
	}
	/**
	 * 
	 * @param color the color
	 * @return the style associated to that color
	 */
	private String getThemeStyleName(ThemeColor color) {
		return "gcube-" + color.toString().toLowerCase() + "-DialogBox";
	}
	
	/**
	 * @return the userGuideLink
	 */
	public String getUserGuideLink() {
		return userGuideLink;
	}
	
	/**
	 * @return the portletName
	 */
	public String getPortletName() {
		return portletName;
	}
	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	
	/**
	 * @return the steps
	 */
	public ArrayList<Composite> getSteps() {
		return steps;
	}
	
	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * @return the mask
	 */
	public boolean isMask() {
		return mask;
	}
	
	/**
	 * @return the color
	 */
	public ThemeColor getColor() {
		return color;
	}
	
	/**
	 * @return the supportedLanguages
	 */
	public TourLanguage[] getSupportedLanguages() {
		return supportedLanguages;
	}
}
