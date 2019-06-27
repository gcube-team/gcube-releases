package org.gcube.portlets.admin.authportletmanager.client.widget;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;





public class ActionCellClass<C> extends AbstractCell<C> {

	 
	  private final SafeHtml html;
	  private final Delegate<C> delegate;

	  /**
	   * Construct a new {@link ActionCellClass}.
	   *
	   * @param message the message to display on the button
	   * @param delegate the delegate that will handle events
	   */
	  public ActionCellClass(SafeHtml message, Delegate<C> delegate) {
	    super(CLICK, KEYDOWN);
	    this.delegate = delegate;
	    String cssClass="";
	    if (message.asString().toLowerCase().equals("delete"))
	    	cssClass="btn-danger";
	    this.html = new SafeHtmlBuilder().appendHtmlConstant(
	        "<button class=\"btn btn_"+message.asString().toLowerCase()+" "+cssClass+" \" type=\"button\" tabindex=\"-1\">").appendHtmlConstant(
	        "</button>").toSafeHtml();
	  }

	  /**
	   * Construct a new {@link ActionCellClass} with a text String that does not contain
	   * HTML markup.
	   *
	   * @param text the text to display on the button
	   * @param delegate the delegate that will handle events
	   */
	  public ActionCellClass(String text, Delegate<C> delegate) {
	    this(SafeHtmlUtils.fromString(text), delegate);
	  }

	 
	@Override
	  public void onBrowserEvent(Context context, Element parent, C value,
	      NativeEvent event, ValueUpdater<C> valueUpdater) {
	    super.onBrowserEvent(context, parent, value, event, valueUpdater);
	    if (CLICK.equals(event.getType())) {
	      EventTarget eventTarget = event.getEventTarget();
	      if (!Element.is(eventTarget)) {
	        return;
	      }
	      if (parent.getFirstChildElement().isOrHasChild(Element.as(eventTarget))) {
	        // Ignore clicks that occur outside of the main element.
	        onEnterKeyDown(context, parent, value, event, valueUpdater);
	      }
	    }
	  }

	  @Override
	  public void render(Context context, C value, SafeHtmlBuilder sb) {
	    sb.append(html);
	  }

	  @Override
	  protected void onEnterKeyDown(Context context, Element parent, C value,
	      NativeEvent event, ValueUpdater<C> valueUpdater) {
	    delegate.execute(value);
	  }
}