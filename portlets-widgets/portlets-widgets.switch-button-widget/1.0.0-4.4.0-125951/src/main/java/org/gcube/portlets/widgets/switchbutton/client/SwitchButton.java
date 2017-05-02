package org.gcube.portlets.widgets.switchbutton.client;

import static com.google.gwt.query.client.GQuery.$;

import org.gcube.portlets.user.gcubewidgets.client.elements.Div;
import org.gcube.portlets.user.gcubewidgets.client.elements.Span;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.query.client.css.CSS;
import com.google.gwt.query.client.css.Length;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

public class SwitchButton extends Composite implements HasName, HasValue<Boolean>{

	private static SwitchButtonUiBinder uiBinder = GWT
			.create(SwitchButtonUiBinder.class);

	interface SwitchButtonUiBinder extends UiBinder<Widget, SwitchButton> {
	}
	@UiField FocusPanel switchContainer;
	@UiField Div switcherButton; 
	@UiField Span labelOff;
	@UiField Span labelOn;
	private String name;
	private Boolean value;
	private Boolean valueChangeHandlerInitialized = Boolean.FALSE;

	public SwitchButton() {
		name = DOM.createUniqueId();
		initWidget(uiBinder.createAndBindUi(this));		
		value = false;
		$(switcherButton).css(CSS.LEFT.with(Length.px(-1)));
		ensureDomEventHandlers();
	}

	public SwitchButton(boolean initialValue) {
		this();
		setValue(initialValue);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		// Is this the first value change handler? If so, time to add handlers
		if (!valueChangeHandlerInitialized) {
			valueChangeHandlerInitialized = true;
		}
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public Boolean getValue() {
		return value;
	}
	/**
	 * Checks or unchecks the switch button box, firing {@link ValueChangeEvent} if
	 * appropriate.
	 * <p>
	 * Note that this <em>does not</em> set the value property of the checkbox
	 * input element wrapped by this widget. For access to that property, see
	 * {@link #setFormValue(String)}
	 * 
	 * @param value true to set on, false to set off; null value implies false
	 */
	@Override
	public void setValue(Boolean value) {
		if (value == null) {
			value = Boolean.FALSE;
		}

		Boolean oldValue = getValue();
		if (value.equals(oldValue)) {
			return;
		}
		this.value = value;
		if (!value) {
			$(switcherButton).animate("left: -1", 250);		
			labelOff.setStyleName("switch-button-label on");
			labelOn.setStyleName("switch-button-label off");
		} else {
			$(switcherButton).animate("left: 12", 250);
			labelOff.setStyleName("switch-button-label off");
			labelOn.setStyleName("switch-button-label on");
		}
	}
	/**
	 * Checks or unchecks the switch button box, firing {@link ValueChangeEvent} if
	 * appropriate.
	 * <p>
	 * 
	 * @param value true to set on, false to set off; null value implies false
	 * @param fireEvents If true, and value has changed, fire a
	 *          {@link ValueChangeEvent}
	 */
	@Override
	public void setValue(Boolean value, boolean fireEvents) {
		setValue(value);
		if (fireEvents) {
			ValueChangeEvent.fire(this, value);
		}
	}

	protected void ensureDomEventHandlers() {
		switchContainer.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {				
				setValue(!value);
				ValueChangeEvent.fire(SwitchButton.this, getValue());
			}
		});
	}

	@Override
	public void setName(String name) {
		this.name = name;		
	}

	@Override
	public String getName() {
		return name;
	}
}
