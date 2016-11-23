package org.gcube.portlets.user.templates.client.components;

import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.client.uicomponents.ReportUIComponent;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * <code> ImageArea </code> class is the Widget that goes into the workspace
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version October 2008 (0.2) 
 */
public class ImageArea extends ReportUIComponent {

	private String templateName;
	private String imageName;
	
	private String currentUser;
	private String currentScope;
	private Image myImage = new Image(); 
	
	private Presenter presenter;

	/**
	 * Creates an image with a specified URL. The load event will be fired once
	 * the image at the given URL has been retrieved by the browser.
	 * 
	 * @param controller .
	 *	 * @param param the imageName of the image to be displayed or the URL to be displayed depending on the isURL param
	 * @param myTemplate the template that owns the image, useful when template it's saved with another name 
	 * 					to eventually copy the images in the new template images folder
	 *  @param isURL true if passing a URL, false is passing image name 
	 *  
	 *  @param height h
	 *  @param width w
	 *  @param left l
	 *  @param top t
	 */
	public ImageArea(Presenter controller, boolean isURL, String param, String myTemplate, int left, int top, int width, int height) {
		super(ComponentType.STATIC_IMAGE, left, top, width, height);
		this.presenter = presenter;
		currentUser = controller.getCurrentUser();
		currentScope = controller.getCurrentScope();
		if (isURL) {
			//need to get the name from the URL
			int lastSlash = param.lastIndexOf("/");
			if (lastSlash > -1)
				imageName = param.substring(lastSlash+1, param.length());
			else 
				imageName = param;
			this.templateName = "CURRENT_OPEN";
			myImage.setUrl(param);	
			
		} else {
			imageName = param;
			this.templateName = myTemplate;
			String imgURL = getImageURL(imageName, "CURRENT_OPEN");
			myImage.setUrl(imgURL);	
		}
		VerticalPanel myPanel = getResizablePanel();
		myPanel.add(myImage);
		myImage.setPixelSize(width-2, height-6);
		
	}
	

	/**
	 * used to resize the panel
	 * @param width w
	 * @param height h
	 */
	@Override
	public void resizePanel(int width, int height) {
		super.resizePanel(width, height);
		myImage.setPixelSize(width-4, height-6);
	}
	
	/**
	 * return a URL which is lookable for on the web
	 * @param imageName .
	 * @param templateName .
	 * @return .
	 */
	public String getImageURL(String imageName, String templateName) {
			
		/**
		 * Images will be stored under webapps/usersArea...
		 */
		String host = Window.Location.getProtocol()+"//"+Window.Location.getHost()+"/";
		String imgURL = host + "usersArea/"	+ currentScope + "/templates/" 
							+ currentUser + "/" + "CURRENT_OPEN" + "/images/" + imageName;
		
		return imgURL;
	}
	
	
	
	/**
	 * 
	 * @return a string containing the owner template name
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * 
	 * @param myTemplate the template owner
	 */
	public void setTemplateName(String myTemplate) {
		this.templateName = myTemplate;
	}

	/**
	 * 
	 * @return .
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * 
	 * @param imageName .
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	@Override
	public void lockComponent(ReportUIComponent toLock, boolean locked) {
		presenter.lockComponent(this, locked);
	}

	@Override
	public void removeTemplateComponent(ReportUIComponent toRemove) {
		presenter.removeTemplateComponent(this);		
	}


}
