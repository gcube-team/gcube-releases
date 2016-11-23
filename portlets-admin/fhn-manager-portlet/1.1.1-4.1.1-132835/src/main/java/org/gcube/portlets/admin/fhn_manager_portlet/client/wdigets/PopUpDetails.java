package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.DescribedResource;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class PopUpDetails extends Composite {

	private static Logger logger = Logger.getLogger(PopUpDetails.class+"");
	
	private static PopUpDetailsUiBinder uiBinder = GWT
			.create(PopUpDetailsUiBinder.class);

	interface PopUpDetailsUiBinder extends UiBinder<Widget, PopUpDetails> {
	}

	@UiField 
	Modal m;
	@UiField
	TabPanel tabPanel;
	
	public PopUpDetails(DescribedResource resource) {
		initWidget(uiBinder.createAndBindUi(this));
		m.setTitle(resource.getTheObject().getName());
		logger.fine("Creating main tab..");
		Tab mainTab=formTab(resource);
		mainTab.setActive(true);
		tabPanel.add(mainTab);
		
		if(resource.getRelated()!=null){
			logger.fine("Adding related tabs..");
			for(DescribedResource related:resource.getRelated())
				try{
					tabPanel.add(formTab(related));
				}catch(Exception e){
					logger.log(Level.SEVERE,"Error : ",e);
				}
		}
		logger.fine("Details created, showing it..");
		m.show();		
	}

	private Tab formTab(DescribedResource res){
		Tab toReturn=new Tab();
		switch(res.getTheObject().getType()){
		case REMOTE_NODE : toReturn.setIcon(IconType.HDD);
		break;
		case SERVICE_PROFILE : toReturn.setIcon(IconType.TAG);
		break;
		case VM_PROVIDER : toReturn.setIcon(IconType.GLOBE);
		break;
		case VM_TEMPLATES : toReturn.setIcon(IconType.COGS);
		break;
		}
		toReturn.setHeading(res.getTheObject().getName());
//		Paragraph par=new Paragraph();
//		par.setText(res.getXmlDescription());
//		toReturn.add(par);
//		
//		toReturn.add(new Paragraph(new SafeHtmlBuilder().appendEscaped(
//				res.getXmlDescription()).toSafeHtml().asString()));
		toReturn.add(new HTMLPanel(res.getXmlDescription()));
		logger.fine("E l'ho settato, cazzo!");
		return toReturn;
	}
	
}
