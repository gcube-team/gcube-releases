package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.TableItemSimple;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.experimentArea.ExperimentPanel;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.resources.Resources;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.util.EventBusProvider;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.util.MaskEvent;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;

public class StatisticalManagerExperimentsWidget extends Window {
	public static Resources resources = GWT.create(Resources.class);

	
	protected Logger logger = Logger.getLogger("logger");
	
	
	private String target="ExecutionComputationDefault";
	
	private static StatisticalManagerExperimentsWidget instance;
	
	
	public static StatisticalManagerExperimentsWidget instance(){
		return instance;
	}
	
	
	private static TableItemSimple translateToSimple(ExternalTable toTranslate){
		return new TableItemSimple(toTranslate.getId(),toTranslate.getLabel(),"TDM Table","GENERIC",true);
	}
	
	
	
	// *************************** INSTANCE LOGIC 
	
	private SubmissionHandler submissionHandler;
	protected ArrayList<String> listAlgorithmsName = new ArrayList<String>();
	private Map<String,TableItemSimple> callerDefinedTableItems=new HashMap<String,TableItemSimple>();
	private Map<String,ExternalTable> callerDefinedTables=new HashMap<String,ExternalTable>();
	
	
	private ExperimentPanel experimentPanel = new ExperimentPanel();

	private LayoutContainer centerPanel = new LayoutContainer(new FitLayout());
	private BorderLayoutData centerPanelData;

	private LayoutContainer container=new LayoutContainer(new BorderLayout());

	public StatisticalManagerExperimentsWidget(SubmissionHandler handler) {// String scope, String user)
		instance=this;
		submissionHandler=handler;
		show();
	}

	public StatisticalManagerExperimentsWidget(
			ArrayList<String> listAlgorithmsName, String target,SubmissionHandler handler) {
		logger.log(Level.SEVERE, "second constructor");	
		if (listAlgorithmsName != null) {
			logger.log(Level.SEVERE, "listAlgorithmsName is not null");

			this.listAlgorithmsName.addAll(listAlgorithmsName);
			logger.log(Level.SEVERE, "size list :" + listAlgorithmsName.size());

		}
		
		if(target!=null)
			this.target=target;
		instance=this;
		submissionHandler=handler;
		show();
	}

	public StatisticalManagerExperimentsWidget(
			ArrayList<String> listAlgorithmsName,
			List<ExternalTable> tables, String target,SubmissionHandler handler) {
		logger.log(Level.SEVERE, "second constructor");
		if (listAlgorithmsName != null) {
			logger.log(Level.SEVERE, "listAlgorithmsName is not null");

			this.listAlgorithmsName.addAll(listAlgorithmsName);
			logger.log(Level.SEVERE, "size list :" + listAlgorithmsName.size());

		}

		if (tables != null) {
			for(ExternalTable tab:tables){
				callerDefinedTables.put(tab.getId(), tab);
				callerDefinedTableItems.put(tab.getId(),translateToSimple(tab));
			}
		}
		if(target!=null)
			this.target=target;

		
		instance=this;
		submissionHandler=handler;
		show();
	}

	
	
	
	
	public void show() {
		bind();

//		com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler() {
//			@Override
//			public void onResize(ResizeEvent event) {
//				updateSize();
//			}
//
//		});

		this.setSize(1000,600);
		
		
		container.setStyleAttribute("background-color", "#FFFFFF");
		centerPanelData = new BorderLayoutData(LayoutRegion.CENTER);
		centerPanelData.setMargins(new Margins(0));
		centerPanel.setStyleAttribute("padding-bottom", "15px");

		container.add(centerPanel, centerPanelData);
		centerPanel.add(experimentPanel);
		
		this.add(container);
		this.setResizable(false);
		super.show();

		experimentPanel.setSize(this.getWidth()-8, this.getHeight()-8);
	}

	private void bind() {
		EventBusProvider.getInstance().addHandler(MaskEvent.getType(),
				new MaskEvent.MaskHandler() {
					@Override
					public void onMask(MaskEvent event) {
						if (this == null)
							return;

						String message = event.getMessage();
						if (message == null)
							unmask();
						else
							mask(message, Constants.maskLoadingStyle);
					}
				});
	}

//	private void updateSize() {
//		// RootPanel smDiv = RootPanel.get(SM_DIV);
//
//		// int topBorder = smDiv.getAbsoluteTop();
//		// int leftBorder = smDiv.getAbsoluteLeft();
//		int topBorder = this.getAbsoluteTop();
//		int leftBorder = this.getAbsoluteLeft();
//
//		int rootHeight = Window.getClientHeight() - topBorder - 4;// - ((footer
//																	// ==
//																	// null)?0:(footer.getOffsetHeight()-15));
//		int rootWidth = Window.getClientWidth() - 2 * leftBorder - 5; // -
//																		// rightScrollBar;
//
//		this.setSize(rootWidth, rootHeight);
//
//	}

	public ArrayList<String> getListSelectedAlg() {
		return listAlgorithmsName;
	}

	public Map<String, TableItemSimple> getListSelectedList() {
		return callerDefinedTableItems;
	}

	
	public void submit(SubmissionParameters params){
		submissionHandler.onSubmit(params);
	}
	
	public Map<String, String> getColumns(String key) {

		return callerDefinedTables.get(key).getColumnsNameAndLabels();

	}
	public String getTarget()
	{
		return target;
	}
}
