package org.gcube.portlets.user.td.expressionwidget.client.help;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;

public class HelpReplaceColumnByExpression {
	public interface HelpReplaceByExpTemplate extends XTemplates {
		@XTemplate(source = "HelpReplaceByExp.html")
		SafeHtml render(HelpReplaceByExpStyle style);
	}

	public interface HelpReplaceByExpStyle extends CssResource {
		@ClassName("HelpReplaceRegexImg")
		public String getHelpReplaceRegexImg();

		@ClassName("HelpReplaceRegexClear")
		public String getHelpReplaceRegexClear();
		
		
		@ClassName("DataStyle")
		public String getDataStyle();
	}

	private final HelpReplaceByExpStyle style;
	private final HelpReplaceByExpTemplate template;

	public interface HelpReplaceByExpResources extends ClientBundle {
		public static final HelpReplaceByExpResources INSTANCE = GWT
				.create(HelpReplaceByExpResources.class);

		@Source("HelpReplaceByExpStyle.css")
		HelpReplaceByExpStyle style();

		@Source("Data.png")
		ImageResource data();

		
		@Source("SimpleConcat.png")
		ImageResource simpleConcat();

	}

	public HelpReplaceColumnByExpression() {
		HelpReplaceByExpResources resource=HelpReplaceByExpResources.INSTANCE;
		this.style = resource.style();
		this.style.ensureInjected();

		this.template = GWT.create(HelpReplaceByExpTemplate.class);
	}

	public void render(SafeHtmlBuilder sb) {
		sb.append(template.render(style));
		
		XElement parent=XDOM.create(sb.toSafeHtml());
		XElement element = parent.selectNode("." + style.getDataStyle());
		Image image = new Image(HelpReplaceByExpResources.INSTANCE.data());
		Element img = image.getElement();
		element.appendChild(img);
		
	}

	

}
