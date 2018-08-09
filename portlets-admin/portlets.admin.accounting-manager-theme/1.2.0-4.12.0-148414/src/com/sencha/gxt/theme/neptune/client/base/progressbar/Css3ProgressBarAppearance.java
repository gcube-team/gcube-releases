/**
 * Sencha GXT 3.1.1 - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.neptune.client.base.progressbar;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.cell.core.client.ProgressBarCell.ProgressBarAppearance;
import com.sencha.gxt.cell.core.client.ProgressBarCell.ProgressBarAppearanceOptions;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.theme.neptune.client.ThemeDetails;

public class Css3ProgressBarAppearance implements ProgressBarAppearance {
  public interface Css3ProgressBarResources extends ClientBundle {

    @Source("Css3ProgressBar.css")
    Css3ProgressBarStyles styles();

    ThemeDetails theme();
  }

  public interface Css3ProgressBarStyles extends CssResource {
    String wrap();

    String bar();
    String text();
    String textBack();
  }

  public interface Css3ProgressBarTemplate extends XTemplates {
    @XTemplate(source = "Css3ProgressBar.html")
    SafeHtml render(SafeHtml text, Css3ProgressBarStyles style, SafeStyles wrapStyles, SafeStyles progressBarStyles, SafeStyles progressTextStyles, SafeStyles widthStyles);
  }

  private final Css3ProgressBarStyles styles;
  private final Css3ProgressBarTemplate template = GWT.create(Css3ProgressBarTemplate.class);

  public Css3ProgressBarAppearance() {
    this(GWT.<Css3ProgressBarResources>create(Css3ProgressBarResources.class));
  }
  public Css3ProgressBarAppearance(Css3ProgressBarResources resources) {
    styles = resources.styles();
    StyleInjectorHelper.ensureInjected(styles, false);
  }

  @Override
  public void render(SafeHtmlBuilder sb, Double value, ProgressBarAppearanceOptions options) {
    value = value == null ? 0 : value;

    String text = options.getProgressText();

    if (text != null) {
      int v = (int) Math.round(value * 100);
      text = Format.substitute(text, v);
    }

    SafeHtml txt;
    if (text == null) {
      txt = SafeHtmlUtils.fromSafeConstant("&#160;");
    } else {
      txt = SafeHtmlUtils.fromString(text);
    }

    SafeStyles widthStyles = SafeStylesUtils.fromTrustedNameAndValue("width", options.getWidth() + "px");

    final SafeStyles progressBarStyles;
    if (value <= 0) {
      progressBarStyles = SafeStylesUtils.fromTrustedNameAndValue("visibility", "hidden");
    } else {
      progressBarStyles = SafeStylesUtils.fromTrustedNameAndValue("width", value * 100 + "%");
    }


    sb.append(template.render(txt, styles, null, progressBarStyles, null, widthStyles));

  }
}
