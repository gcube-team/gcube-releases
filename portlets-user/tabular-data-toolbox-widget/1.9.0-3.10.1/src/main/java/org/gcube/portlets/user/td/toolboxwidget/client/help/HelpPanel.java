package org.gcube.portlets.user.td.toolboxwidget.client.help;



import org.gcube.portlets.user.td.monitorwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.toolboxwidget.client.resources.ResourceBundle;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldSet;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class HelpPanel extends FramedPanel {
	private HelpPanelMessages msgs;
	// private String headingTitle;
	// private HashMap<String, String> tabularResourcePropertiesMap;
	private VerticalLayoutContainer vl;
	// private EventBus eventBus;

	private FieldSet contents;

	private VerticalLayoutContainer layoutCaptions;
	private TextButton wikiButton;

	public HelpPanel(String name, EventBus eventBus) {
		super();
		msgs = GWT.create(HelpPanelMessages.class);
		setId(name);
		forceLayoutOnResize = true;
		setBodyBorder(false);
		setBorders(false);

		vl = new VerticalLayoutContainer();
		vl.setAdjustForScroll(true);
		initInformation();
		this.add(vl);

	}

	public void addContents() {
		HTML title = new HTML("<H2>" + msgs.helpPanelTitle() + "<H2>");
		vl.add(title, new VerticalLayoutData(-1, -1, new Margins(1, 1, 10, 1)));

		contents = new FieldSet();
		contents.setHeadingText(msgs.contents());
		contents.setCollapsible(true);
		contents.setResize(true);

		layoutCaptions = new VerticalLayoutContainer();
		
	
		contents.add(layoutCaptions);
		HTML info = new HTML(msgs.info());

		wikiButton = new TextButton(msgs.wikiButton());
		wikiButton.setIcon(ResourceBundle.INSTANCE.wiki());
		wikiButton.setIconAlign(IconAlign.RIGHT);
		wikiButton.setToolTip(msgs.wikiButtonToolTip());
		SelectHandler wikiHandler = new SelectHandler() {

			public void onSelect(SelectEvent event) {
				onWiki();

			}
		};
		wikiButton.addSelectHandler(wikiHandler);

		layoutCaptions.add(info, new VerticalLayoutData(1, -1, new Margins(1)));
		layoutCaptions.add(wikiButton, new VerticalLayoutData(-1, -1,
				new Margins(1)));
		vl.add(contents, new VerticalLayoutData(1, -1, new Margins(1)));

	}

	private void onWiki() {
		try {
			ResourceBundle.INSTANCE.linksProperties().getText(
					new ResourceCallback<TextResource>() {
						public void onError(ResourceException e) {
							Log.error("Error retrieving wiki link!: "
									+ e.getLocalizedMessage());
							UtilsGXT3.alert("Error",
									"Error retrieving wiki link!");
						}

						public void onSuccess(TextResource r) {
							openWiki(r);
						}
					});
		} catch (ResourceException e) {
			Log.error("Error retrieving wiki link!: " + e.getLocalizedMessage());
			UtilsGXT3.alert("Error", "Error retrieving wiki link!");
			e.printStackTrace();

		}

	}

	private void openWiki(TextResource r) {
			String s = r.getText();
			Window.open(s,
					"Tabular Data Manager Wiki", "");
		
	}

	public void initInformation() {
		addContents();

	}

}
