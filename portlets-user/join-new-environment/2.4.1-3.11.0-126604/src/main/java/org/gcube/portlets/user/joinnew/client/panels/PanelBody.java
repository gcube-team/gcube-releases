package org.gcube.portlets.user.joinnew.client.panels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.gcube.portlets.user.joinnew.client.Joinnew;
import org.gcube.portlets.user.joinnew.client.commons.UIConstants;
import org.gcube.portlets.user.joinnew.shared.UserBelonging;
import org.gcube.portlets.user.joinnew.shared.VO;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PanelBody extends Composite {
	private static PanelBody singleton = null;
	private VerticalPanel main_panel = null;
	private ArrayList<PanelVREs>  panelVREs = null;
	private ArrayList<String> suggestions = null;
	private ArrayList<PanelVO> panelVOs;
	private ArrayList<HorizontalPanel> hpLines;
	private boolean byFilter = false;
	private int sizeIcon = 100;

	/**
	 * the header panel
	 */
	HorizontalPanel hpLabel = new HorizontalPanel();

	public static PanelBody get()
	{ 
		return singleton;
	}

	public PanelBody() {
		Init();
		initWidget(main_panel);
		main_panel.setStyleName("margin_left");
		main_panel.setWidth("900px");
		if (singleton == null) singleton = this;
	}

	private void Init() {

		// Create a static tree and a container to hold it
		this.panelVOs = new ArrayList<PanelVO>();
		this.hpLines = new ArrayList<HorizontalPanel>();
		this.panelVREs = new ArrayList<PanelVREs>();
		this.suggestions = new ArrayList<String>();
		this.main_panel = new VerticalPanel();
	}

	/**
	 * Create a VO tree with some data in it.
	 * @param result 
	 * 
	 * @return the new tree
	 */
	public void setVO(List<VO> result) {
		this.main_panel.clear();
		for (final VO vo: result) {
			if (vo.isRoot()) {
				HorizontalPanel labelContainerPanel = new HorizontalPanel();
				labelContainerPanel.setVerticalAlignment(HasAlignment.ALIGN_MIDDLE);
				Label lbl = new Label(vo.getName());
				lbl.setStylePrimaryName("font_family font_18 font_color_VO font_bold");

				hpLabel.setVerticalAlignment(HasAlignment.ALIGN_MIDDLE);
				hpLabel.setWidth("90%");
				hpLabel.setVerticalAlignment(HasAlignment.ALIGN_MIDDLE);
				labelContainerPanel.add(lbl);


				/**
				 * MAKE THE HEADER WIDTH NON DEPENDENT ON THE VREs Panel
				 */
				int clientW = RootPanel.get(Joinnew.JOIN_NEW).getOffsetWidth();
				clientW = (clientW > 900) ? 900 : clientW;
				hpLabel.setPixelSize(clientW - 80, 30);				

				Image img_info = new Image(UIConstants.INFO_IMAGE);
				Image img_enterRoot = new Image(UIConstants.ENTER_VO_ROOT);

				Image help = new Image(UIConstants.HELP_ICO);
				help.setStyleName("button_help");
				help.setTitle("Open user's guide");
				final String helpUrl = "https://gcube.wiki.gcube-system.org/gcube/index.php/User%27s_Guide";

				help.addClickHandler(new ClickHandler() {

					public void onClick(ClickEvent event) {
						Window.open(helpUrl, "", "");
					}
				});

				img_info.addClickHandler(new ClickHandler() {

					public void onClick(ClickEvent event) {
						InfoDialog p = new InfoDialog(vo.getGroupName(), vo.getDescription());
						p.setAnimationEnabled(false);
						p.show();

					}
				});
				img_info.addStyleName("selectable");
				//hpLabel.add(lbl);

				labelContainerPanel.add(new HTML("&nbsp;"));
				labelContainerPanel.add(img_info);
				labelContainerPanel.add(new HTML("&nbsp;"));
				labelContainerPanel.add(help);

				/*
				 * add the possibilty to manage the root vo just if you belong to it
				 */
				if (vo.getUserBelonging() == UserBelonging.BELONGING) {
					labelContainerPanel.add(new HTML("&nbsp;"));
					labelContainerPanel.add(img_enterRoot);
					img_enterRoot.addStyleName("pointer");
					img_enterRoot.setTitle("Enter the Root VO");

					img_enterRoot.addClickHandler(new ClickHandler() {

						public void onClick(ClickEvent event) {
							Joinnew.showLoading();	
							String scope = vo.getGroupName();
							//Window.alert(vo.getFriendlyURL());
							Joinnew.getService().loadLayout(scope,  vo.getFriendlyURL(), new AsyncCallback<Void>() {
								public void onFailure(Throwable arg0) {							
									Joinnew.hideLoading();	
									Window.open( vo.getFriendlyURL(), "_self", "");
								}
								public void onSuccess(Void arg0) {
									Joinnew.hideLoading();
									Window.open( vo.getFriendlyURL(), "_self", "");
								}
							});

						}					
					}); 			
				}
				hpLabel.add(labelContainerPanel);
				hpLabel.add(new PanelConsole());
				this.main_panel.add(hpLabel);
				this.main_panel.add(new HTML("<hr align=\"left\" size=\"1\" width=\"90%\" color=\"gray\" noshade>"));	
				main_panel.setCellVerticalAlignment(hpLabel, HasVerticalAlignment.ALIGN_TOP);

			} else {

				VerticalPanel vp = new VerticalPanel();

				vp.setStyleName("margin_left");
				//
				this.panelVOs.add(new PanelVO(vo));				
				vp.add(this.panelVOs.get(this.panelVOs.size() - 1));

				HorizontalPanel hp = new HorizontalPanel();
				this.panelVREs.add(new PanelVREs(vo.getVres()));
				hp.add(this.panelVREs.get(this.panelVREs.size() - 1));
				vp.add(hp);

				HorizontalPanel hpLine = new HorizontalPanel();
				hpLine.setWidth("100%");
				hpLine.add(new HTML("<hr align=\"left\" size=\1\" width=\"100%\" color=\"#EEEEEE\" noshade>"));
				this.hpLines.add(hpLine);
				vp.add(this.hpLines.get(this.hpLines.size() - 1));

				this.main_panel.add(vp);

				//main_panel.setStyleName("border");
			}
		}
	}

	public void changeSizeWidth(int width) {

		this.sizeIcon = width;
		for (int i = 0; i < this.panelVREs.size(); i++) {
			if (this.panelVREs.get(i).changeSizeWidth(width) == 0) {
				if (PanelBody.get().getSuggestionContain(this.panelVOs.get(i).getVOName()))  
					this.panelVOs.get(i).setVisible(true);
				else
					this.panelVOs.get(i).setVisible(false);
				//this.panelVOs.get(i).setVisible(false);
				this.hpLines.get(i).setVisible(false);
			} else {
				//if (PanelBody.get().getSuggestionContain(this.panelVOs.get(i).getVOName()))  
				this.panelVOs.get(i).setVisible(true);
				//else
				//  this.panelVOs.get(i).setVisible(false);

				if (width == 0)  
					this.hpLines.get(i).setVisible(false);
				else 
					this.hpLines.get(i).setVisible(true);
			}
		}
	}

	public void refreshSize() {
		this.changeSizeWidth(this.sizeIcon);
	}

	@SuppressWarnings("unchecked")
	public void setFilter(boolean byFilter, Collection sugg) {

		this.byFilter = byFilter;
		this.suggestions.clear();

		if (this.byFilter) {
			Iterator it = sugg.iterator();
			while (it.hasNext()) {
				MultiWordSuggestion p = (MultiWordSuggestion) it.next();
				this.suggestions.add(p.getReplacementString());
			}
		}
	}

	public boolean getSuggestionContain(String name) {
		if (this.byFilter) return this.suggestions.contains(name);
		else return true;
	}

	public static native String getURL()/*-{
		return $wnd.location;
		}-*/;


	/**
	 * 
	 * @param width
	 */
	public void resizeHeader(int clientW) {
		try {
		hpLabel.setPixelSize(900, 30);
		}
		catch (Exception e) {
			
		}
	}

	public ArrayList<PanelVREs> getPanelVREs() {
		return panelVREs;
	}
	
	
}