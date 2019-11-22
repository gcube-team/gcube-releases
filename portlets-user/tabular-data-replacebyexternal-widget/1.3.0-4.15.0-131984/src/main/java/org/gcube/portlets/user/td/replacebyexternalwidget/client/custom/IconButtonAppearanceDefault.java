package org.gcube.portlets.user.td.replacebyexternalwidget.client.custom;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class IconButtonAppearanceDefault implements IconButtonAppearance {
	
	public interface Template extends XTemplates {
		@XTemplate(source = "IconButton.html")
		SafeHtml template(IconButtonStyle style);
	}

	public interface IconButtonStyle extends CssResource {
		@ClassName("iconButton")
		public String getIconButton();

		@ClassName("iconButtonImage")
		public String getIconButtonImage();
		
		@ClassName("iconButtonRef")
		public String getIconButtonRef();
		
	}

	private final IconButtonStyle style;
	private final Template template;

	public interface IconButtonResources extends ClientBundle {
		public static final IconButtonResources INSTANCE =  GWT.create(IconButtonResources.class);
		
		@Source("IconButtonStyle.css")
		IconButtonStyle style();
	}

	public IconButtonAppearanceDefault() {
		this(IconButtonResources.INSTANCE);
	}

	public IconButtonAppearanceDefault(IconButtonResources resources) {
		this.style = resources.style();
		this.style.ensureInjected();

		this.template = GWT.create(Template.class);
	}

	
	
	public void onUpdateIcon(XElement parent, ImageResource icon) {
		XElement element = parent.selectNode("." + style.getIconButtonImage());
		Image image=new Image(icon);
		Element img=image.getElement();
		img.setClassName(style.getIconButtonRef());
		element.appendChild(img);
		
	}

	public void render(SafeHtmlBuilder sb) {
		sb.append(template.template(style));
	}
}