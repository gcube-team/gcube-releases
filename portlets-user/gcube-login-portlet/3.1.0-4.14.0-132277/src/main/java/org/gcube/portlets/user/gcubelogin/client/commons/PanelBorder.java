package org.gcube.portlets.user.gcubelogin.client.commons;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;

public class PanelBorder extends SimplePanel {
  /**
   * The default style name.
   */
  private static final String DEFAULT_STYLENAME = "PanelBorder";

  /**
   * The default styles applied to each row.
   */
  private static final String[] DEFAULT_ROW_STYLENAMES = {
      "GreenBorder_top", "GreenBorder_middle", "GreenBorder_bottom"};

  /**
   * Create a new row with a specific style name. The row will contain three
   * cells (Left, Center, and Right), each prefixed with the specified style
   * name.
   * 
   * This method allows Widgets to reuse the code on a DOM level, without
   * creating a DecoratorPanel Widget.
   * 
   * @param styleName the style name
   * @return the new row {@link Element}
   */
  static Element createTR(String styleName) {
    Element trElem = DOM.createTR();
    setStyleName(trElem, styleName);
    if (LocaleInfo.getCurrentLocale().isRTL()) {
      DOM.appendChild(trElem, createTD(styleName + "Right"));
      DOM.appendChild(trElem, createTD(styleName + "Center"));
      DOM.appendChild(trElem, createTD(styleName + "Left"));
    } else {
      DOM.appendChild(trElem, createTD(styleName + "Left"));
      DOM.appendChild(trElem, createTD(styleName + "Center"));
      DOM.appendChild(trElem, createTD(styleName + "Right"));
    }
    return trElem;
  }

  /**
   * Create a new table cell with a specific style name.
   * 
   * @param styleName the style name
   * @return the new cell {@link Element}
   */
  private static Element createTD(String styleName) {
    Element tdElem = DOM.createTD();
    Element inner = DOM.createDiv();
    DOM.appendChild(tdElem, inner);
    setStyleName(tdElem, styleName);
    setStyleName(inner, styleName + "Inner");
    return tdElem;
  }

  /**
   * The container element at the center of the panel.
   */
  private Element containerElem;

  /**
   * The table body element.
   */
  private Element tbody;

  /**
   * Create a new {@link DecoratorPanel}.
   */
  public PanelBorder() {
    this(DEFAULT_ROW_STYLENAMES, 1);
  }

  /**
   * Creates a new panel using the specified style names to apply to each row.
   * Each row will contain three cells (Left, Center, and Right). The Center
   * cell in the containerIndex row will contain the {@link Widget}.
   * 
   * @param rowStyles an array of style names to apply to each row
   * @param containerIndex the index of the container row
   */
  public PanelBorder(String[] rowStyles, int containerIndex) {
    super(DOM.createTable());

    // Add a tbody
    Element table = getElement();
    tbody = DOM.createTBody();
    DOM.appendChild(table, tbody);
    DOM.setElementPropertyInt(table, "cellSpacing", 0);
    DOM.setElementPropertyInt(table, "cellPadding", 0);

    // Add each row
    for (int i = 0; i < rowStyles.length; i++) {
      Element row = createTR(rowStyles[i]);
      DOM.appendChild(tbody, row);
      if (i == containerIndex) {
        containerElem = DOM.getFirstChild(DOM.getChild(row, 1));
      }
    }

    // Set the overall style name
    setStyleName(DEFAULT_STYLENAME);
  }

  /**
   * Get a specific Element from the panel.
   * 
   * @param row the row index
   * @param cell the cell index
   * @return the Element at the given row and cell
   */
  protected Element getCellElement(int row, int cell) {
    Element tr = DOM.getChild(tbody, row);
    Element td = DOM.getChild(tr, cell);
    return DOM.getFirstChild(td);
  }

  @Override
  protected Element getContainerElement() {
    return containerElem;
  }
}