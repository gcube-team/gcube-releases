/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templateactions;

import java.util.List;

import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.TabularDataActionDescription;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The Class HistoryAction.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 1, 2015
 */
public class HistoryAction extends Dialog {
	 
    private LayoutContainer lc = new LayoutContainer();
    public static int width = 450;
    public static int heigth = 300;
    private HistoryAction INSTANCE = this;
	/**
	 * Instantiates a new history action.
	 *
	 * @param img the img
	 * @param title the title
	 * @param msgTxt the msg txt
	 */
	public HistoryAction(String heading) {
		this.setLayout(new FitLayout());
		setHeading(heading);
		setButtonAlign(HorizontalAlignment.CENTER);
		setMaximizable(true);
		setWidth(width);
		setHeight(heigth);
	    lc.setLayout(new FitLayout());
	    lc.setScrollMode(Scroll.AUTOY);
//	    lc.setWidth("100%");
//	    lc.setWidth("200px");
	   
	    getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});
	    
	    add(lc);
	 }
	
	/**
	 * Update history.
	 *
	 * @param tabularDataActionDescription the tabular data action description
	 */
	public void updateHistory(){
		
		lc.mask("Loading History..");
		TdTemplateController.tdTemplateServiceAsync.getAppliedActionsOnTemplate(new AsyncCallback<List<TabularDataActionDescription>>() {
			
			@Override
			public void onSuccess(List<TabularDataActionDescription> result) {
				lc.removeAll();
				FlexTableActions flex = new FlexTableActions(result);
//				flex.setHeight(height);
				lc.add(flex);
			    lc.layout(true);
			    INSTANCE.layout(true);
			    lc.unmask();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				lc.unmask();
				MessageBox.alert("Error", caught.getMessage(), null);
			}
		});
	}
	
	/**
	 * Show history.
	 */
	public void showHistory() {
		this.show();
	}
}
