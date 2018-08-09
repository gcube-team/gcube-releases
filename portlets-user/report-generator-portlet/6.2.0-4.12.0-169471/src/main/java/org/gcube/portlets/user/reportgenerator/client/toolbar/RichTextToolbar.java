package org.gcube.portlets.user.reportgenerator.client.toolbar;

/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A sample toolbar for use with {@link RichTextArea}. It provides a simple UI
 * for all rich text formatting, dynamically displayed only for the available
 * functionality.
 */
public class RichTextToolbar extends Composite  {

	private boolean isVME = false;
	/**
	 * We use an inner EventListener class to avoid exposing event methods on the
	 * RichTextToolbar itself.
	 */
	private class EventListener implements ClickHandler, ChangeHandler,	KeyUpHandler {

		@SuppressWarnings("deprecation")
		@Override
		public void onChange(ChangeEvent event) {
			Widget sender = (Widget) event.getSource();	
			if (sender == fonts) {
				basic.setFontName(fonts.getValue(fonts.getSelectedIndex()));
				fonts.setSelectedIndex(0);
			} else if (sender == fontSizes) {
				basic.setFontSize(fontSizesConstants[fontSizes.getSelectedIndex() - 1]);
				fontSizes.setSelectedIndex(0);
			}
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onClick(ClickEvent event) {
			Widget sender = (Widget) event.getSource();	
			if (sender == bold) {
				basic.toggleBold();
			} else if (sender == save) {
				//call the command of Templates or Reports 
				commands.get("save").execute();
			}
			else if (sender == saveDB) {
				//call the command toExport to RSG the current instance of the model
				commands.get("exportRSG").execute();
			} 
			else if (sender == newdoc) {
				commands.get("newdoc").execute();
			}else if (sender == structureView) {
				commands.get("structureView").execute();
			}else if (sender == close) {
				commands.get("newdoc").execute();
			} else if (sender == open_report) {
				commands.get("open_report").execute();
			} else if (sender == open_template) {
				commands.get("open_template").execute();
			} else if (sender == importing) {
				commands.get("importing").execute();
			}else if (sender == insertImage) {
				commands.get("insertImage").execute();
			} else if (sender == italic) {
				basic.toggleItalic();
			} else if (sender == underline) {
				basic.toggleUnderline();		
			}else if (sender == subscript) {
				extended.toggleSubscript();				
			} else if (sender == superscript) {
				extended.toggleSuperscript();				
			}  
			else if (sender == justifyLeft) {
				basic.setJustification(RichTextArea.Justification.LEFT);
			} else if (sender == justifyCenter) {
				basic.setJustification(RichTextArea.Justification.CENTER);
			} else if (sender == justifyRight) {
				basic.setJustification(RichTextArea.Justification.RIGHT);
			} else if (sender == createLink) {
				String url = Window.prompt("Enter a link URL:", "http://");
				if (url != null) {
					extended.createLink(url);
				}
			} else if (sender == removeLink) {
				extended.removeLink();
			} 
			else if (sender == ol) {
				extended.insertOrderedList();
			} else if (sender == ul) {
				extended.insertUnorderedList();
			}
//			else if (sender == foreColorButton) {
//				commands.get("pickColor").execute();
//			}
			else if (sender == removeFormat) {
				extended.removeFormat();
			} else if (sender == richText) {
				// We use the RichTextArea's onKeyUp event to update the toolbar status.
				// This will catch any cases where the user moves the cursur using the
				// keyboard, or uses one of the browser's built-in keyboard shortcuts.
				updateStatus();
			}
		}

		@Override
		public void onKeyUp(KeyUpEvent event) {
			Widget sender = (Widget) event.getSource();	
			if (sender == richText) {
				updateStatus();
			}
		}	
	}

	private static final RichTextArea.FontSize[] fontSizesConstants = new RichTextArea.FontSize[] {
		RichTextArea.FontSize.XX_SMALL, RichTextArea.FontSize.X_SMALL,
		RichTextArea.FontSize.SMALL, RichTextArea.FontSize.MEDIUM,
		RichTextArea.FontSize.LARGE, RichTextArea.FontSize.X_LARGE,
		RichTextArea.FontSize.XX_LARGE};

	private Images images = (Images) GWT.create(Images.class);
	private Strings strings = (Strings) GWT.create(Strings.class);
	private EventListener listener = new EventListener();

	private RichTextArea richText;

	@SuppressWarnings("deprecation")
	private RichTextArea.BasicFormatter basic;
	@SuppressWarnings("deprecation")
	private RichTextArea.ExtendedFormatter extended;



	private VerticalPanel outer = new VerticalPanel();
	private HorizontalPanel topPanel = new HorizontalPanel();
	private HorizontalPanel bottomPanel = new HorizontalPanel();	
	private ToggleButton structureView;
	private ToggleButton bold;
	private ToggleButton italic;
	private ToggleButton underline;
//	private ToggleButton strikethrough;
	private ToggleButton superscript;
	private ToggleButton subscript;
	private PushButton justifyLeft;
	private PushButton justifyCenter;
	private PushButton justifyRight;
	private PushButton save;
	private PushButton saveDB;
	private PushButton close;
	private PushButton newdoc;
	private PushButton open_template;
	private PushButton open_report;
	private PushButton importing;

	private PushButton ol;
	private PushButton ul;
	private PushButton insertImage;
	private PushButton createLink;
	private PushButton removeLink;
	private PushButton removeFormat;

	//	private ListBox backColors;
	//	private ListBox foreColors;
	private ListBox fonts;
	private ListBox fontSizes;

//	private PushButton foreColorButton;

	private HashMap<String, Command> commands;



	/**
	 * Creates a new toolbar that drives the given rich text area.
	 * 
	 * @param richText the rich text area to be controlled
	 * @param useOnePanel if  true display all the bottons on one panel 
	 * @param commands the Commands to call
	 */
	@SuppressWarnings("deprecation")
	public RichTextToolbar(RichTextArea richText, HashMap<String, Command> commands, boolean isVme) {
		this.isVME = isVme;
		this.commands = commands;
		this.richText = richText;
		this.basic = richText.getBasicFormatter();
		this.extended = richText.getExtendedFormatter();

		topPanel.setHeight("20px");
		topPanel.setSpacing(2);

		outer.add(bottomPanel);
		topPanel.setWidth("100%");
		outer.setWidth("100%");

		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HasAlignment.ALIGN_MIDDLE);
		topPanel.setVerticalAlignment(HasAlignment.ALIGN_MIDDLE);
		hp.add(topPanel);
		outer.add(hp);
		outer.setHeight("20px");

		initWidget(outer);
		setStyleName("goog-toolbar");
		richText.addStyleName("hasRichTextToolbar");

		if (basic != null) {
			topPanel.add(structureView = createToggleButton(images.structureView(),	strings.structureView())); 
			if (! isVme) {
				//add the new template button
				topPanel.add(newdoc = createPushButton(images.newdoc(),	strings.newdoc()));
			}
			open_report = createPushButton(images.open_report(),strings.open_report());
			//add the open report button
			topPanel.add(open_report);
			if (! isVme) {
				//add the open template button
				topPanel.add(open_template = createPushButton(images.open_template(),strings.open_template()));
				//add the save button
				topPanel.add(importing = createPushButton(images.importing(),	strings.importing()));
			}
			//add the save button
			topPanel.add(save = createPushButton(images.save(),	strings.save()));
			//add the close button
			topPanel.add(close = createPushButton(images.closeDoc(), strings.close_report()));
			if (isVme) {
				topPanel.add(saveDB = createPushButton(images.db_save(), "Commit current in VME-DB")); //TODO:
			}

			topPanel.add(new HTML("&nbsp;"));
			fonts = createFontList();
			fonts.setPixelSize(150, 18);
			topPanel.add(fonts);
			topPanel.add(new HTML("&nbsp;"));
			topPanel.add(fontSizes = createFontSizes());

			// We only use these listeners for updating status, so don't hook them up
			// unless at least basic editing is supported.
			richText.addKeyUpHandler(listener);
			richText.addClickHandler(listener);

		}

		if (basic != null) {
			topPanel.add(bold = createToggleButton(images.bold(), strings.bold()));



			topPanel.add(italic = createToggleButton(images.italic(), strings.italic()));
			topPanel.add(underline = createToggleButton(images.underline(),	strings.underline()));

			topPanel.add(new HTML("&nbsp;", true));
			topPanel.add(subscript = createToggleButton(images.subscript(),"subscript"));
			topPanel.add(superscript = createToggleButton(images.superscript(),
					"superscript"));
//			topPanel.add(strikethrough = createToggleButton(images.strikeThrough(),
//					strings.strikeThrough()));
			topPanel.add(new HTML("&nbsp;", true));
			topPanel.add(justifyLeft = createPushButton(images.justifyLeft(),
					strings.justifyLeft()));
			topPanel.add(justifyCenter = createPushButton(images.justifyCenter(),
					strings.justifyCenter()));
			topPanel.add(justifyRight = createPushButton(images.justifyRight(),
					strings.justifyRight()));
			topPanel.add(new HTML("&nbsp;", true));


		}

		if (extended != null) {

			topPanel.add(ol = createPushButton(images.ol(), strings.ol()));
			topPanel.add(ul = createPushButton(images.ul(), strings.ul()));
			//TODO: To be re-enabled in the future
			//			topPanel.add(insertImage = createPushButton(images.insertImage(), 
			//					strings.insertImage()));
			topPanel.add(createLink = createPushButton(images.createLink(),
					strings.createLink()));
			topPanel.add(removeLink = createPushButton(images.removeLink(),
					strings.removeLink()));

			topPanel.add(removeFormat = createPushButton(images.removeFormat(),
					strings.removeFormat()));
//			topPanel.add(foreColorButton = createPushButton(images.foreColors(),
//					"Text Color"));
		}
		if (basic != null) {
			topPanel.add(new HTML("&nbsp;"));
			//			topPanel.add(foreColors = createColorList("Color"));
			//			foreColors.setStyleName("listbox");
		}

	}

	private ListBox createFontList() {
		ListBox lb = new ListBox();
		lb.addChangeHandler(listener);
		lb.setVisibleItemCount(1);

		lb.addItem(strings.font(), "");
		lb.addItem(strings.normal(), "");
		lb.addItem("Times New Roman", "Times New Roman");
		lb.addItem("Arial", "Arial");
		lb.addItem("Courier New", "Courier New");
		lb.addItem("Georgia", "Georgia");
		lb.addItem("Trebuchet", "Trebuchet");
		lb.addItem("Verdana", "Verdana");

		lb.setStyleName("listbox");
		lb.setPixelSize(100, 18);

		return lb;
	}

	private ListBox createFontSizes() {
		ListBox lb = new ListBox();
		lb.addChangeHandler(listener);
		lb.setVisibleItemCount(1);

		lb.addItem(strings.size());
		lb.addItem(strings.xxsmall());
		lb.addItem(strings.xsmall());
		lb.addItem(strings.small());
		lb.addItem(strings.medium());
		lb.addItem(strings.large());
		lb.addItem(strings.xlarge());
		lb.addItem(strings.xxlarge());
		lb.setStyleName("listbox");
		lb.setPixelSize(100, 18);
		return lb;
	}

	private PushButton createPushButton(AbstractImagePrototype img, String tip) {
		Image toAdd = img.createImage();
		//toAdd.setSize("15", "15");
		PushButton pb = new PushButton(toAdd);
		pb.addClickHandler(listener);
		pb.setTitle(tip);
		pb.setPixelSize(15, 15);

		pb.setStyleName("myButton");

		return pb;
	}

	private ToggleButton createToggleButton(AbstractImagePrototype img, String tip) {
		ToggleButton tb = new ToggleButton(img.createImage());
		tb.addClickHandler(listener);
		tb.setTitle(tip);
		tb.setPixelSize(15, 15);
		tb.setStyleName("myButton");

		return tb;
	}

	/**
	 * Updates the status of all the stateful buttons.
	 */
	private void updateStatus() {
		if (basic != null) {
			bold.setDown(basic.isBold());
			italic.setDown(basic.isItalic());
			underline.setDown(basic.isUnderlined());
		}
	}

	/**
	 * set the buttons enable or not
	 * @param enable true to enable the widget, false  to disable it
	 */
	public void setEnabled(boolean enable) {
		bold.setEnabled(enable);
		italic.setEnabled(enable);
		underline.setEnabled(enable);
//		strikethrough.setEnabled(enable);
		justifyLeft.setEnabled(enable);
		justifyCenter.setEnabled(enable);
		justifyRight.setEnabled(enable);
		superscript.setEnabled(enable);
		subscript.setEnabled(enable);
//		foreColorButton.setEnabled(enable);
		//		insertImage.setEnabled(enable);
		ol.setEnabled(enable);
		ul.setEnabled(enable);
		createLink.setEnabled(enable);
		removeLink.setEnabled(enable);
		removeFormat.setEnabled(enable);
		//foreColors.setEnabled(enable);
		fonts.setEnabled(enable);
		fontSizes.setEnabled(enable);	
	}

	public void enableCommands(boolean enable) {
		open_report.setEnabled(enable);
		save.setEnabled(enable);

		if (newdoc != null) {
			newdoc.setEnabled(enable);
			open_template.setEnabled(enable);
			importing.setEnabled(enable);
		}
	}

	/**
	 *  
	 * @return the formatter
	 */
	public RichTextArea.ExtendedFormatter getExtendedFormatter() {
		return extended;
	}
	/**
	 * 
	 * @return the current textArea
	 */
	public RichTextArea getRichTextArea() {
		return richText;
	}

	/**
	 * This {@link Constants} interface is used to make the toolbar's strings
	 * internationalizable.
	 */
	public interface Strings extends Constants {

		String close_report();

		String insertImage();

		String importing();

		String black();

		String blue();

		String bold();

		String color();

		String createLink();

		String font();

		String green();

		String hr();

		String italic();

		String justifyCenter();

		String justifyLeft();

		String justifyRight();

		String large();

		String medium();

		String normal();

		String newdoc();

		String open_template();

		String open_report();

		String ol();

		String outdent();

		String red();

		String removeFormat();

		String removeLink();

		String save();

		String size();

		String small();

		String strikeThrough();

		String structureView();

		String ul();

		String underline();

		String white();

		String xlarge();

		String xsmall();

		String xxlarge();

		String xxsmall();

		String yellow();
	}

}
