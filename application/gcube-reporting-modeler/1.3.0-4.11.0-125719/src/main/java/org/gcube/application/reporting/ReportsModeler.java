package org.gcube.application.reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Vector;

import org.gcube.application.reporting.component.interfaces.Modeler;
import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.BasicSection;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.d4sreporting.common.shared.Model;

/**
 * 
 * @author massi
 *
 */
public final class ReportsModeler implements Modeler<ReportComponent> {
	/**
	 * Styling attr, not used anymore but needed for backward compatibility
	 */
	public static final int TEMPLATE_WIDTH = 750;
	public static final int TEMPLATE_HEIGHT= 1000;
	public static final String DEFAULT_NAME = "no-name";
	private static final int MARGIN_LEFT = 0;
	private static final int MARGIN_RIGHT = 0;
	private static final int MARGIN_TOP = 0;
	private static final int MARGIN_BOTTOM = 0;
	private static final int COLUMN_WIDTH = 0;


	private int currSection  = 1;
	/**
	 * model instance
	 */
	private Model model;
	/**
	 * 
	 * @param a the report unique idenntifier
	 * @param name the report name
	 * @param author the author
	 * @param dateCreated 
	 * @param lastEdit
	 * @param lastEditorId last editor username
	 */
	public ReportsModeler(String id, String name, String author, Date dateCreated, Date lastEdit, String lastEditorId) {
		model = new Model(
				id, 
				author, 
				dateCreated, 
				lastEdit, 
				lastEditorId, 
				name, 
				COLUMN_WIDTH, 
				1, // page to show
				MARGIN_BOTTOM, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP, TEMPLATE_HEIGHT, TEMPLATE_WIDTH, //useless params
				new Vector<BasicSection>(), 
				1, //total pages 
				new LinkedList<Metadata>());
		initialize();
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.application.reporting.component.Modeler#add(org.gcube.application.reporting.component.interfaces.ReportComponent)
	 */
	public boolean add(ReportComponent rc) {
		return 	addComponentToSection(rc.getModelComponent());
	}
	/**
	 *{@inheritDoc} 
	 */

	public int nextSection() {
		currSection++;
		model.setTotalPages(currSection);
		return model.getTotalPages();
	}
	/**
	 * 
	 * @param bc the component to add
	 */
	private boolean addComponentToSection(BasicComponent bc) {
		if (model.getSections().isEmpty()) {
			BasicSection bs = new BasicSection();
			model.getSections().add(bs);
		} 
		if (currSection == (model.getSections().size()+1)) { // check the nextSection()
			BasicSection bs = new BasicSection();
			model.getSections().add(bs);
		} 
		if (currSection == model.getSections().size()) { // current section
			BasicSection bs = model.getSections().get(currSection-1);
			bs.getComponents().add(bc);
			return true;
		}
		else
			return false;
	}

	public Model getReportInstance() throws Exception {
		if (model.getSections().isEmpty())
			throw new Exception("The report is empty, has no sections.");
		return model;
	}

	/**
	 * 
	 * add a placeholder component at the beginning of the report (for backward compatibility)
	 */
	private void initialize() {
		BasicComponent bc = new BasicComponent(0, 0, ReportComponent.COMP_WIDTH, ReportComponent.COMP_HEIGHT, 1, ComponentType.FAKE_TEXTAREA, "", "", false, false,	new ArrayList<Metadata>());		
		addComponentToSection(bc);
	}

}
