package org.gcube.portlets.user.joinnew.client.panels;

import java.util.ArrayList;

import org.gcube.portlets.user.joinnew.client.commons.ODLFlexTable;
import org.gcube.portlets.user.joinnew.client.commons.UIConstants;
import org.gcube.portlets.user.joinnew.shared.VRE;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PanelVREs extends Composite {
	
	private VerticalPanel main_panel = null;
	private ArrayList<VRE> vres;
	private ArrayList<PanelVRE> pvres = null;
	private VerticalPanel vertical_view = null;
	private ODLFlexTable horizontal_view = null;
	
	public PanelVREs(ArrayList<VRE> vres) {
		this.vres = vres;
		this.pvres = new ArrayList<PanelVRE>();
		Init();
		initWidget(main_panel);
	}
	
	private void Init() {
		main_panel = new VerticalPanel(); 
		horizontal_view = new ODLFlexTable();
		horizontal_view.setCellPadding(5);
		vertical_view = new VerticalPanel();
		vertical_view.setStyleName("margin_left p8");
		int clientW = 900;
		int width = 0;
		int row = 0;
		int col = 0;
		for (VRE vre: vres) {
			this.pvres.add(new PanelVRE(vre));
			
			width += UIConstants.imageVRE_width + 20;
				
			GWT.log(vre.getName() + " / " + clientW+" / "+(clientW - width));
			if (clientW - width < 0) {
				width = UIConstants.imageVRE_width + 20;
				col = 0;
				row++;
				//GWT.log("NEWLINE row=" + row);
			}
			horizontal_view.setWidget(row, col, this.pvres.get(this.pvres.size() - 1));
			vertical_view.add(new LabelVRE(vre));

			col++;
		}
	
		main_panel.add(horizontal_view);
		vertical_view.setVisible(false);
		//main_panel.add( new SingleVREView());
		main_panel.add(vertical_view);
		//this.changeSizeWidth(50);
	}
	
	/**
	 * 
	 * @param width_perc
	 * @return
	 */
	public int changeSizeWidth(int width_perc) {
		
		int i = 0;
		int clientW = (Window.getClientWidth() > 900) ? 900:Window.getClientWidth() ;
		if (width_perc == 0) {
			vertical_view.setVisible(false);
			vertical_view.clear();
			for (VRE vre: vres) {
				if (PanelBody.get().getSuggestionContain(vre.getName())) {
					vertical_view.add(new LabelVRE(vre));
					i++;
				}
			}
			horizontal_view.setVisible(false);
			vertical_view.setVisible(true);
		
			vertical_view.setPixelSize(clientW, 1);
		} else {
	
			int width = 0;
			int row = 0;
			int col = 0;
			
			horizontal_view.setVisible(false);
			horizontal_view.removeAllRows();
			horizontal_view.clear();
			for (PanelVRE vre : this.pvres) {
				if (PanelBody.get().getSuggestionContain(vre.getName())) {
					vre.setVisible(true);
					vre.changeSizeWidth(width_perc);
					width += vre.getSizeWidth() + 23;
					//Window.alert(width+ " - " + clientW + " - " + (clientW - width));
					if (clientW - width < 0) {
						
						width = vre.getSizeWidth() + 23;
						col = 0;
						row++;
					}
					//horizontal_view.clearCell(row, col);
					horizontal_view.setWidget(row, col, vre);
					col++;
					i++;
				}
			}
			vertical_view.setVisible(false);
			horizontal_view.setVisible(true);

		}
		return i;
	}
	
	public ArrayList<PanelVRE> getVreCards() {
		return pvres;
	}
}