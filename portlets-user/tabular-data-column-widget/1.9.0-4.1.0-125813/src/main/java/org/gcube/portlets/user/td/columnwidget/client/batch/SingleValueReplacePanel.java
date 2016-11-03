package org.gcube.portlets.user.td.columnwidget.client.batch;

import java.util.Date;

import org.gcube.portlets.user.td.columnwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.columnwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SingleValueReplacePanel extends FramedPanel {
	private DateTimeFormat sdf = DateTimeFormat.getFormat("yyyy-MM-dd");

	private static final String WIDTH = "500px";
	private static final String HEIGHT = "150px";

	private SingleValueReplaceDialog parent;
	private ColumnData column;
	private String value;
	private String replaceValue;

	private TextField valueField;
	private TextField replaceValueField;
	private TextButton btnReplace;
	private TextButton btnClose;

	private SingleValueReplaceMessages msgs;
	private CommonMessages msgsCommon;

	public SingleValueReplacePanel(SingleValueReplaceDialog parent,
			String value, String replaceValue, ColumnData column,
			EventBus eventBus) {
		this.parent = parent;
		this.value = value;
		this.replaceValue = replaceValue;
		this.column = column;
		Log.debug("SingleValueReplacePanel:[" + value + "]");
		initMessages();
		initPanel();
		create();

	}

	protected void initMessages() {
		msgs = GWT.create(SingleValueReplaceMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected void initPanel() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
	}

	protected void create() {
		valueField = new TextField();
		valueField.setValue(value);
		valueField.setReadOnly(true);

		replaceValueField = new TextField();
		if (replaceValue != null) {
			valueField.setValue(replaceValue);
		}

		btnReplace = new TextButton(msgs.btnReplaceText());
		btnReplace.setIcon(ResourceBundle.INSTANCE.replace());
		btnReplace.setIconAlign(IconAlign.RIGHT);
		btnReplace.setToolTip(msgs.btnReplaceToolTip());
		btnReplace.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Apply");
				replaceValue();

			}
		});

		btnClose = new TextButton(msgs.btnCloseText());
		btnClose.setIcon(ResourceBundle.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setToolTip(msgs.btnCloseToolTip());
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();

			}
		});

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		BoxLayoutData boxLayoutData = new BoxLayoutData(new Margins(2, 4, 2, 4));
		flowButton.add(btnReplace, boxLayoutData);
		flowButton.add(btnClose, boxLayoutData);

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.add(new FieldLabel(valueField, msgs.value()),
				new VerticalLayoutData(1, -1));
		v.add(new FieldLabel(replaceValueField, msgs.replace()),
				new VerticalLayoutData(1, -1));
		v.add(flowButton,
				new VerticalLayoutData(1, 36, new Margins(5, 2, 5, 2)));
		add(v);

	}

	protected void replaceValue() {
		String rValue = replaceValueField.getCurrentValue();
		if (rValue == null || rValue.isEmpty()) {
			UtilsGXT3.alert(msgsCommon.attention(), msgs.insertAValidReplaceValue());
		} else {
			String checkedValue = checkTypeData(rValue);
			if (checkedValue != null && !checkedValue.isEmpty()) {
				callReplaceValue(rValue);
			} else {
				UtilsGXT3.alert(msgsCommon.attention(),
						msgs.insertAValidReplaceValueForThisColumn());
			}
		}

	}

	protected String checkTypeData(String rValue) {
		String checked = null;
		try {
			if (column.getDataTypeName().compareTo("Boolean") == 0) {
				Boolean b = new Boolean(rValue);
				checked = b.toString();
			} else {
				if (column.getDataTypeName().compareTo("Date") == 0) {
					Date d = null;
					try {
						d = sdf.parse(rValue);
					} catch (Exception e) {
						Log.error("Unparseable using " + sdf);
						return null;
					}
					if (d != null) {
						checked = rValue;
					}
				} else {
					if (column.getDataTypeName().compareTo("Geometry") == 0) {
						checked = rValue;
					} else {
						if (column.getDataTypeName().compareTo("Integer") == 0) {
							Integer in = new Integer(rValue);
							checked = in.toString();
						} else {
							if (column.getDataTypeName().compareTo("Numeric") == 0) {
								Double fl = new Double(rValue);
								checked = fl.toString();
							} else {
								if (column.getDataTypeName().compareTo("Text") == 0) {
									checked = rValue;
								} else {

								}
							}
						}
					}
				}

			}

		} catch (Throwable e) {
			Log.debug("Error no valid type data: " + e.getLocalizedMessage());
		}

		return checked;

	}

	protected void callReplaceValue(String rValue) {
		parent.fireCompleted(rValue);
	}

	protected void close() {
		parent.close();
	}

}
