package org.gcube.portlets.user.td.tablewidget.client.custom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.sencha.gxt.core.client.XTemplates;

/**
 * 
 * @author "Giancarlo Panichi"
 *
 */
public class ActionButtonCellAppearance {
 
        public interface Style extends CssResource {             
                String image();
        }
        
        public interface Template extends XTemplates {
                @XTemplate(source = "ActionButton.html")
                SafeHtml template(Style style, SafeHtml img, String title);
        }
        
        public interface Resources extends ClientBundle {                
                @Source("ActionButton.css")
                Style style();
        }
        
        private final Style style;
        private final Template template;
        
        public ImageResource icon;
        public String title;
        
        public ActionButtonCellAppearance(){
                this((Resources) GWT.create(Resources.class));
        }
        
        public ActionButtonCellAppearance(Resources resources){
                this.style = resources.style();
                this.style.ensureInjected();
                this.template = GWT.create(Template.class);
        }
        
        public void render(SafeHtmlBuilder sb) {                
                sb.append(template.template(style, makeImage(icon), title));            
        }
        
        /**
         * Make icons available as SafeHtml to be displayed inside the table
         * 
         * @param resource
         * @return
         */
        private static SafeHtml makeImage(ImageResource resource) {
                AbstractImagePrototype proto = AbstractImagePrototype.create(resource);
                String html = proto.getHTML();
                return SafeHtmlUtils.fromTrustedString(html);
        }
}