/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ExtendedMessageBox.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.widgets.dialogs;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.KeyboardEvents;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class ExtendedMessageBox {
	private Dialog dialog = null;
	
	PasswordTextBox pwdField = new PasswordTextBox();

	public static ExtendedMessageBox password(
			final String title,
			final Listener<MessageBoxEvent> callback) {
		ExtendedMessageBox box = new ExtendedMessageBox();

		if (callback != null) {
			box.addCallback(callback);
		}

		box.setTitle(title);
		box.show();
		return box;
	}

	public ExtendedMessageBox() {
		this.initDialog();
	}

	public final void setTitle(final String text) {
		this.dialog.setHeading(text);
	}


	public final void addCallback(final Listener<MessageBoxEvent> listener) {
		dialog.addListener(Events.Hide, listener);
	}

	private void initDialog() {
		this.dialog = new Dialog() {
			@Override
			protected ComponentEvent previewEvent(final EventType type, final ComponentEvent ce) {
				if (ce instanceof WindowEvent) {
					WindowEvent we = (WindowEvent) ce;
					MessageBoxEvent e = new MessageBoxEvent(null, this, we.getButtonClicked());
					if (type == Events.Hide || type == Events.BeforeHide) {
						if (pwdField != null) {
							e.setValue(pwdField.getValue());
						}
					}
					return e;
				}
				return super.previewEvent(type, ce);
			}
		};

		dialog.setHideOnButtonClick(true);

		dialog.setLayout(new FitLayout());
		dialog.setWidth(280);
		dialog.setHeight(120);
		dialog.setResizable(false);
		
		pwdField.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyboardEvents.Enter.getEventCode())
					dialog.hide();
				
			}
		});
		pwdField.setStyleName("wizardTextBox");
		HorizontalPanel sp = new HorizontalPanel();
		sp.setHeight("120px");
		sp.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		sp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		sp.setWidth("100%");

		pwdField.setWidth("250px");
		sp.add(pwdField);
		this.dialog.add(sp);
	}

	public final void show() {
		this.dialog.show();
		Timer t = new Timer() {
			@Override
			public void run() {
				focusPassword();
			}
		};
		t.schedule(500);
	}
	void focusPassword() {
		this.pwdField.setFocus(true);
	}
}
