package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

public class StatisticalManagerAlgorithmsWidget implements EntryPoint {
	
	

	
	private static final String SM_DIV = "contentDiv";
	protected Logger logger = Logger.getLogger("logger");

	// private VerticalPanel verticalPanel = new VerticalPanel();

//	private ExperimentPanel experimentPanel = new ExperimentPanel();
//
//	private LayoutContainer centerPanel = new LayoutContainer(new FitLayout());
//	private BorderLayoutData centerPanelData;
//	private LayoutContainer statisticalManagerLayout;
//
//	private static TabularData tabularData;
//	private static TabularData tabularDataTD;

	StatisticalManagerExperimentsWidget st;
	
	public void onModuleLoad() {
		
		Button button =new Button("Click me");
		button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				ArrayList<ExternalTable> tables=new ArrayList<ExternalTable>();
				tables.add(new MyTable());
				st = new StatisticalManagerExperimentsWidget(null,tables,"ExecutionComputationDefault",new MyHandler());
				
			}
		});
		
		
		
		
		
//		Button button=new Button();
//		button.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				logger.log(Level.WARNING,"Open Window");
//				Window popup=new Window();
//				popup.add(st);
//				popup.show();
//			}
//		});
		
		
		
		RootPanel.get(SM_DIV).add(button);
		

	}

	
	
	private static class MyTable implements ExternalTable{
		private String label="My External Table";
		
		private String id="Table01";
		
		private Map<String,String> columns=new HashMap<String, String>();
		
		
		public MyTable() {
			columns.put("column01lf13455", "MY values");
			columns.put("colum02gfheyt", "Other values");
			columns.put("column123409863450789634", "This should be a really long label to be displayed");
		}
		
		
		
		@Override
		public String getId() {
			return id;
		}
		@Override
		public String getLabel() {
			return label;
		}
		@Override
		public Map<String, String> getColumnsNameAndLabels() {
			return columns;
		}
		
	}
	
	
	private static class MyHandler implements SubmissionHandler{
		@Override
		public void onSubmit(SubmissionParameters params) {
			System.err.println("SUBMITTED :"+params);
			System.err.println("Parameters : "+params.getParametersMap());
		}
	}
}
