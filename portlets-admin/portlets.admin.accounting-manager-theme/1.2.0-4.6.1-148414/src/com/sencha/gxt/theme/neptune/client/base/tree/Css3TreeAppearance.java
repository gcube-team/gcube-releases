/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;
import com.sencha.gxt.widget.core.client.tree.Tree.Joint;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeAppearance;
import com.sencha.gxt.widget.core.client.tree.TreeStyle;
import com.sencha.gxt.widget.core.client.tree.TreeView.TreeViewRenderMode;

/**
 *
 */
public class Css3TreeAppearance implements TreeAppearance {

  public interface Css3TreeResources extends ClientBundle {

    @Source({"com/sencha/gxt/theme/base/client/tree/Tree.css", "Css3Tree.css"})
    Css3TreeStyle style();

    ThemeDetails theme();

    ImageResource checked();

    @Source("folderOpen.png")
    ImageResource folderOpened();

    @Source("folder.png")
    ImageResource folderClosed();

    @Source("arrowRight.png")
    ImageResource jointCollapsedIcon();

    @Source("arrowDown.png")
    ImageResource jointExpandedIcon();

    @Source("loading.gif")
    ImageResource loadingIcon();

    @Source("partial.png")
    ImageResource partialChecked();

    ImageResource unchecked();
  }

  public interface Css3TreeStyle extends CssResource {
    String check();

    String container();

    String dragOver();

    String drop();

    String element();

    String icon();

    String joint();

    String node();

    String over();

    String selected();

    String text();

    String tree();
  }

  private final Css3TreeResources resources;
  private final Css3TreeStyle style;


  protected String indentMarkupStart = "<img src='" + GXT.getBlankImageUrl() + "' style='height: 18px; width: ";

  protected String indentMarkupEnd = "px;' />";

  protected int indentWidth = 17;


  public Css3TreeAppearance() {
    this(GWT.<Css3TreeResources>create(Css3TreeResources.class));
  }

  public Css3TreeAppearance(Css3TreeResources resources) {
    this.resources = resources;
    this.style = resources.style();

    StyleInjectorHelper.ensureInjected(style, true);
  }


  @Override
  public ImageResource closeNodeIcon() {
    return resources.folderClosed();
  }

  @Override
  public String elementSelector() {
    return "." + style.element();
  }

  public XElement findIconElement(XElement target) {
    return target.selectNode("." + style.icon());
  }

  @Override
  public XElement findJointElement(XElement target) {
    return target.selectNode("." + style.joint());
  }

  @Override
  public XElement getCheckElement(XElement container) {
    return container.getChildNodes().getItem(2).cast();
  }

  @Override
  public XElement getContainerElement(XElement node) {
    return node.getFirstChildElement().cast();
  }

  @Override
  public XElement getIconElement(XElement container) {
    return container.getChildNodes().getItem(3).cast();
  }

  @Override
  public XElement getJointElement(XElement container) {
    return container.getChildNodes().getItem(1).cast();
  }

  @Override
  public XElement getTextElement(XElement container) {
    return container.getChildNodes().getItem(4).cast();
  }

  @Override
  public boolean isCheckElement(XElement target) {
    return target.hasClassName(style.check());
  }

  @Override
  public boolean isJointElement(XElement target) {
    if (GXT.isIE6()) {
      target = target.getParentElement().cast();
      return target.hasClassName(style.joint());
    } else {
      return target.hasClassName(style.joint());
    }
  }

  @Override
  public String itemSelector() {
    return "." + style.node();
  }

  @Override
  public ImageResource loadingIcon() {
    return resources.loadingIcon();
  }

  @Override
  public XElement onCheckChange(XElement node, XElement checkElement, boolean checkable, CheckState state) {
    Element e = null;
    if (checkable) {
      switch (state) {
        case CHECKED:
          e = getImage(resources.checked());
          break;
        case UNCHECKED:
          e = getImage(resources.unchecked());
          break;
        case PARTIAL:
          e = getImage(resources.partialChecked());
          break;
      }
    } else {
      e = DOM.createSpan();
    }
    e.addClassName(style.check());
    e = (Element) node.getFirstChild().insertBefore(e, checkElement);
    checkElement.removeFromParent();
    return e.cast();
  }

  @Override
  public void onDropOver(XElement node, boolean over) {
    node.setClassName(style.dragOver(), over);
  }

  @Override
  public void onHover(XElement node, boolean over) {
    node.setClassName(style.over(), over);
  }

  @Override
  public XElement onJointChange(XElement node, XElement jointElement, Joint joint, TreeStyle ts) {
    Element e;
    switch (joint) {
      case COLLAPSED:
        e = getImage(ts.getJointCloseIcon() == null ? resources.jointCollapsedIcon() : ts.getJointCloseIcon());
        break;
      case EXPANDED:
        e = getImage(ts.getJointOpenIcon() == null ? resources.jointExpandedIcon() : ts.getJointOpenIcon());
        break;
      default:
        e = XDOM.create("<img src=\"" + GXT.getBlankImageUrl() + "\" width=\"16px\"/>");
    }

    e.addClassName(style.joint());
    e = (Element) node.getFirstChild().insertBefore(e, jointElement);
    jointElement.removeFromParent();
    return e.cast();
  }

  @Override
  public void onSelect(XElement node, boolean select) {
    node.setClassName(style.selected(), select);
  }

  @Override
  public ImageResource openNodeIcon() {
    return resources.folderOpened();
  }

  @Override
  public void render(SafeHtmlBuilder sb) {
    // EXTGWT-3113 the inner table is needed so that the tree nodes are as wide as needed with horizontal scrolling
    if (GXT.isIE6() || GXT.isIE7()) {
      sb.appendHtmlConstant("<div class=" + style.tree() + " style=\"position: relative;\"></div>");
    } else {
      sb.appendHtmlConstant("<div class="
          + style.tree()
          + " style=\"position: relative;\"><table cellpadding=0 cellspacing=0 width=100%><tr><td></td></tr></table></div>");
    }
  }

  @Override
  public void renderContainer(SafeHtmlBuilder sb) {
    sb.appendHtmlConstant("<div class='" + style.container() + "' role='group'></div>");
  }

  @Override
  public void renderNode(SafeHtmlBuilder sb, String id, SafeHtml text, TreeStyle ts, ImageResource icon,
                         boolean checkable, CheckState checked, Joint joint, int level, TreeViewRenderMode renderMode) {

    if (renderMode == TreeViewRenderMode.ALL || renderMode == TreeViewRenderMode.BUFFER_WRAP) {
      sb.appendHtmlConstant("<div id=\"" + SafeHtmlUtils.htmlEscape(id) + "\" class=\"" + style.node() + "\">");

      sb.appendHtmlConstant("<div class=\"" + style.element() + "\">");
    }

    if (renderMode == TreeViewRenderMode.ALL || renderMode == TreeViewRenderMode.BUFFER_BODY) {

      sb.appendHtmlConstant(getIndentMarkup(level));

      Element jointElement = null;
      switch (joint) {
        case COLLAPSED:
          jointElement = getImage(ts.getJointCloseIcon() == null ? resources.jointCollapsedIcon()
              : ts.getJointCloseIcon());
          break;
        case EXPANDED:
          jointElement = getImage(ts.getJointOpenIcon() == null ? resources.jointExpandedIcon() : ts.getJointOpenIcon());
          break;
        default:
          // empty
      }

      if (jointElement != null) {
        jointElement.addClassName(style.joint());
      }

      sb.appendHtmlConstant(jointElement == null ? "<img src=\"" + GXT.getBlankImageUrl()
          + "\" style=\"width: 16px\" class=\"" + style.joint() + "\" />" : jointElement.getString());

      // checkable
      if (checkable) {
        Element e = null;
        switch (checked) {
          case CHECKED:
            e = getImage(resources.checked());
            break;
          case UNCHECKED:
            e = getImage(resources.unchecked());
            break;
          case PARTIAL:
            e = getImage(resources.partialChecked());
            break;
        }

        e.addClassName(style.check());
        sb.appendHtmlConstant(e.getString());
      } else {
        sb.appendHtmlConstant("<span class='" + style.check() + "'></span>");
      }

      if (icon != null) {
        Element e = getImage(icon);
        e.addClassName(style.icon());
        sb.appendHtmlConstant(e.getString());
      } else {
        sb.appendHtmlConstant("<span class=\"" + style.icon() + "\"></span>");
      }

      sb.appendHtmlConstant("<span class=\"" + style.text() + "\">" + text.asString() + "</span>");
    }

    if (renderMode == TreeViewRenderMode.ALL || renderMode == TreeViewRenderMode.BUFFER_WRAP) {
      sb.appendHtmlConstant("</div>");
      sb.appendHtmlConstant("</div>");
    }

  }

  @Override
  public String textSelector() {
    return "." + style.text();
  }

  protected String getIndentMarkup(int level) {
    return indentMarkupStart + (indentWidth * level) + indentMarkupEnd;
  }

  private Element getImage(ImageResource ir) {
    return AbstractImagePrototype.create(ir).createElement();
  }
}
