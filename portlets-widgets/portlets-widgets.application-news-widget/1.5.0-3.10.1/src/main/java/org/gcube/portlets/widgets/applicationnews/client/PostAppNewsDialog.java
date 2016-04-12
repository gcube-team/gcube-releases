package org.gcube.portlets.widgets.applicationnews.client;

import java.util.Date;

import org.gcube.portal.databook.shared.ApplicationProfile;
import org.gcube.portal.databook.shared.Feed;
import org.gcube.portal.databook.shared.FeedType;
import org.gcube.portal.databook.shared.PrivacyLevel;
import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.widgets.applicationnews.client.bundles.CssAndImages;
import org.gcube.portlets.widgets.applicationnews.client.templates.TweetTemplate;
import org.gcube.portlets.widgets.applicationnews.shared.LinkPreview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 * use to share updates from within your application, the update will be published in the Users News Feed belonging to the VRE your applicationProfile runs into 
 */
public class PostAppNewsDialog extends Composite {
	
	static {
		CssAndImages.INSTANCE.css().ensureInjected();
	}
	
	CssAndImages images = GWT.create(CssAndImages.class);
	
	/**
	 * Create a remote service proxy to talk to the server-side News poster service.
	 */
	private final ApplicationServiceAsync newsService = GWT.create(ApplicationService.class);

	private String portletClassName;
	private String textToShow;
	private String uriGETparams;
	private LinkPreview linkPreview;	


	private GCubeDialog myDialog = new GCubeDialog();
	private Image loadingImage;
	private VerticalPanel mainPanel = new VerticalPanel();

	private Button close = new Button("Cancel");
	private Button post = new Button("Post this News");
	/**
	 * 
	 * @param portletClassName your servlet class name will be used ad unique identifier for your applicationProfile
	 * @param textToShow description for the update you are sharing
	 * @param uriGETparams additional parameters if your application supports the direct opening of of this update's object  e.g. id=12345&type=foo
	 * @param linkPreview the linkPreview object
	 */
	public PostAppNewsDialog(String portletClassName, final String textToShow, final String uriGETparams, final LinkPreview linkPreview) {
		this.portletClassName = portletClassName;
		this.textToShow = textToShow;
		this.uriGETparams = uriGETparams;	
		this.linkPreview = linkPreview;

		showLoading("Loading, please wait ...");
		mainPanel.setWidth("600px");
		mainPanel.setHeight("160px");
		myDialog.center();
		myDialog.show();	
		
		fetchAppProfileAndDisplayPreview();
		
		post.addClickHandler(new ClickHandler() {				
			@Override
			public void onClick(ClickEvent event) {
				publishNews();
			}
		});
		
		close.addClickHandler(new ClickHandler() {				
			@Override
			public void onClick(ClickEvent event) {
				myDialog.hide();
			}
		});
	}	
	/**
	 * 
	 * @param portletClassName your servlet class name will be used ad unique identifier for your applicationProfile
	 * @param textToShow description for the update you are sharing
	 * @param uriGETparams additional parameters if your application supports the direct opening of of this update's object  e.g. id=12345&type=foo
	 */
	public PostAppNewsDialog(String portletClassName, final String textToShow, final String uriGETparams) {
		this(portletClassName, textToShow, uriGETparams, null);

	}
	/**
	 * 
	 * @param portletClassName your servlet class name will be used ad unique identifier for your applicationProfile
	 * @param textToShow description for the update you are sharing
	 */
	public PostAppNewsDialog(String portletClassName, final String textToShow) {
		this(portletClassName, textToShow, "", null);		
	}
	/**
	 * publishNews
	 */
	private void publishNews() {
		showLoading("Posting News, please wait");
		newsService.publishAppNews(portletClassName, textToShow, uriGETparams, linkPreview, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				showFailure();				
			}
			@Override
			public void onSuccess(Boolean result) {
				myDialog.clear();
				mainPanel.clear();
				mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
				mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				loadingImage = new Image(images.ok());
				mainPanel.add(loadingImage);
				Button close = new Button("Close");
				close.addClickHandler(new ClickHandler() {		
					@Override
					public void onClick(ClickEvent event) {
						myDialog.hide();				
					}
				});
				mainPanel.add(close);
				myDialog.setText("App News posted successfully!");
				myDialog.add(mainPanel);			
			}
		});
		
	}
	/**
	 * quite self explaining method name
	 */
	private void fetchAppProfileAndDisplayPreview() {
		newsService.getApplicationProfile(portletClassName, new AsyncCallback<ApplicationProfile>() {			
			@Override
			public void onSuccess(ApplicationProfile appProfile) {
				myDialog.setText("Post " + appProfile.getName() + " News");
				myDialog.clear();
				mainPanel.clear();
				Feed feedPreview = null;
				if (linkPreview == null) {
					feedPreview = new Feed("fakekey", FeedType.PUBLISH, "", new Date(), appProfile.getScope(), "", "", textToShow, PrivacyLevel.SINGLE_VRE, 
							appProfile.getName(), "", appProfile.getImageUrl(), "", "", "");
				}
				else {
					feedPreview = new Feed("fakekey", FeedType.PUBLISH, "", new Date(), appProfile.getScope(), "", linkPreview.getLinkThumbnailUrl(), textToShow, PrivacyLevel.SINGLE_VRE, 
							appProfile.getName(), "", appProfile.getImageUrl(), linkPreview.getTitle(), linkPreview.getDescription(), "");
				}
				mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
				mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
				HTML label = new HTML("This is how the " + appProfile.getName() + " News will look like to the other VRE users: ");
				label.setStyleName("info-content");
				mainPanel.add(label);
				mainPanel.add(new TweetTemplate(feedPreview));
				HorizontalPanel hp = new HorizontalPanel();
				hp.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
				hp.setWidth("100%");
				HorizontalPanel hpButtons = new HorizontalPanel();
				hpButtons.add(close);
				hpButtons.add(post);
				hp.add(hpButtons);
				hp.getElement().getStyle().setPaddingTop(10, Unit.PX);
				mainPanel.add(hp);
				myDialog.add(mainPanel);		

			}			
			@Override
			public void onFailure(Throwable caught) {
				showFailure();
			}
		});
	}
	/**
	 * 
	 */
	private void showFailure() {
		myDialog.setText("Server Error");
		myDialog.clear();
		VerticalPanel vp = new VerticalPanel();
		vp.add(new HTML("There were problems contacting the server, please try again in a short while.."));
		Button close = new Button("Close");
		close.addClickHandler(new ClickHandler() {				
			@Override
			public void onClick(ClickEvent event) {
				myDialog.hide();
			}
		});
		vp.add(close);
		myDialog.add(vp);
	}
	/**
	 * show the initial loading gif
	 */
	private void showLoading(String text) {
		myDialog.clear();
		mainPanel.clear();
		mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		myDialog.setText(text);
		loadingImage = new Image(images.spinner());	
		mainPanel.add(loadingImage);
		myDialog.add(mainPanel);			
	}
}
