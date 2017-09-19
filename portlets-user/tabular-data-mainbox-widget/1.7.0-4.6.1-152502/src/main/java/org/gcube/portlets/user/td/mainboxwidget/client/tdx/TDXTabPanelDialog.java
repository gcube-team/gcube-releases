package org.gcube.portlets.user.td.mainboxwidget.client.tdx;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.dataview.TabularResourceDataView;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Delete Column Dialog
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class TDXTabPanelDialog extends Window {
	private static final String WIDTH = "640px";
	private static final String HEIGHT = "480px";
	private TDXPanel tdxPanel;
	

	public TDXTabPanelDialog(EventBus eventBus) {
		initWindow();
		tdxPanel=new TDXPanel(eventBus);
		add(tdxPanel);
		
		
	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		setHeadingText("TDXPanel Test");
		// getHeader().setIcon(Resources.IMAGES.side_list());
	}

	public void open(TRId trId){;
		TabularResourceDataView trDV=new TabularResourceDataView(trId);
		tdxPanel.open(trDV);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		super.initTools();

		closeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				close();
			}
		});

	}

	public void close() {
		hide();

	}

}
