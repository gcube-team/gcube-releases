package org.gcube.application.reporting.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.gcube.application.reporting.Property;
import org.gcube.application.reporting.ReportsModeler;
import org.gcube.application.reporting.component.Box;
import org.gcube.application.reporting.component.BoxesArea;
import org.gcube.application.reporting.component.Heading;
import org.gcube.application.reporting.component.HiddenField;
import org.gcube.application.reporting.component.Instruction;
import org.gcube.application.reporting.component.Media;
import org.gcube.application.reporting.component.ReportSequence;
import org.gcube.application.reporting.component.SequenceList;
import org.gcube.application.reporting.component.TextInput;
import org.gcube.application.reporting.component.Title;
import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.application.reporting.persistence.PersistenceManager;
import org.gcube.application.reporting.reader.ModelReader;
import org.gcube.application.reporting.reader.Section;
import org.gcube.application.reporting.reference.Column;
import org.gcube.application.reporting.reference.DBTableRow;
import org.gcube.application.reporting.reference.ReferenceReport;
import org.gcube.application.reporting.reference.ReferenceReportType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * This class simply create a Sample VME Report using the ReportModeler
 * 
 * @author massi
 *
 */
public class TestReportModeler {
	/**
	 * make sure this points somewhere in your File System! Make sure it ends with .d4st and you create a folder with the same name: .../Foo/Foo.d4st
	 */
	private final static String PATH_TO_TEST_FILE = "/Users/massi/Desktop/toDeploy/SampleVME/CURRENT_OPEN.d4st";

	private ReportsModeler rm;

	/**
	 * executed First, instances a new ReportModel
	 */
	@Before
	public void init() {
		String reportName = "Sample VME Report With RefTypes";
		String authorId = "test authorId";
		Date created = new Date();
		rm = new ReportsModeler(
				UUID.randomUUID().toString(), 
				reportName, 
				authorId, 
				created, 
				new Date(), 
				"Massi Last Editor"
				);
	}
	/**
	 * Create the Sample VME Report and serialize it in a File @see {@link PersistenceManager#writeModel(org.gcube.portlets.d4sreporting.common.shared.Model)}
	 * @throws Exception
	 */
	@Test
	public void createVMEDBReport() throws Exception {
		addSection1();
//		rm.nextSection();
//		addSection2();
		rm.nextSection();
		addSection3();

		//PersistenceManager.writeModel(rm.getReportInstance(), new File(PATH_TO_TEST_FILE));
		//PersistenceManager.writeModel(rm.getReportInstance()); //this serializes in an temp file
		System.out.println("gCube Modeler Model Created OK with " + rm.getReportInstance().getSections().size() + " Sections");

	}
	/**
	 * Read and print in the console the Structure of the serialized report
	 * @throws Exception 
	 * @see {@link PersistenceManager#readModel(String)}
	 */
	@After
	public void readReportStructure() throws Exception {
	//	ModelReader reader = new ModelReader(PersistenceManager.readModel(PATH_TO_TEST_FILE));
		ModelReader reader = new ModelReader(rm.getReportInstance());
		System.out.println(reader.toString());		
	}


	private void addSection1() {
		rm.add(new Instruction("FAO VME TEST Input form Test"));
		rm.add(new Title("FAO VME TEST Input form Foo", false));
		rm.add(new Heading(1, "VME Name (Non Editable)"));
		TextInput ti = new TextInput("Corner Rise Seamounts");
		ti.setId(UUID.randomUUID().toString());
		List<Property> props = new ArrayList<Property>();
		props.add(new Property("a Key", "A Value"));
		ti.setProperties(props);
		rm.add(ti);
		rm.add(new Heading(2,"This is a Heading of Level 2 that is editable", false));
		rm.add(new TextInput());
		rm.add(new Heading(3,"This is a Heading of Level 2 that is editable", false));
		rm.add(new TextInput());
		rm.add(new Media());

		ArrayList<Box> values = new ArrayList<Box>();
		String[] attrs = {"en", "es", "fr", "ar", "zh", "it"};
		for (int i = 0; i < attrs.length; i++) {
			values.add(new Box("anId", attrs[i], false));
		}
		rm.add(new BoxesArea("Language", values, false));
		rm.add(new Instruction("Competent authority, please insert the acronym (e.g. NAFO, CCAMLR, SEAFO)"));
		rm.add(new Heading(2,"Competent Autority"));
		rm.add(new TextInput("NAFO"));
		rm.add(new Heading(2, "Year"));
		rm.add(new TextInput("2012"));
		rm.add(new Heading(2, "Validity Period - Start"));
		rm.add(new TextInput("2008"));
		rm.add(new Instruction("End date, leave empty if not applicable"));

		values = new ArrayList<Box>();
		String[] areaAttributes= {"VME", "Risk area", "Benthic protected area", "Closed area", "Other type of managed area"};
		for (int i = 0; i < areaAttributes.length; i++) {
			values.add(new Box(areaAttributes[i], false));
		}
		rm.add(new BoxesArea("Area Type", values, false));
		values = new ArrayList<Box>();
		String[] statusAttributes= {"Established", "Under establishment", "Voluntary", "Exploratory", "Potential", "Temporary"};
		for (int i = 0; i < statusAttributes.length; i++) {
			values.add(new Box(statusAttributes[i], false));
		}
		BoxesArea status = new BoxesArea("Status", values, false);
		status.setProperties(new Property("BindingTest", "#"));
		rm.add(status);
		Media med = new Media();
		ti = new TextInput();
		ti.setId(UUID.randomUUID().toString());
		med.Add(new TextInput());
		rm.add(new Media());
		
	}

	private void addSection2() {
		rm.add(new Heading(1, "Specific Measure"));
		
		ReportSequence seq = new ReportSequence(UUID.randomUUID().toString());
		seq.add(new HiddenField("TheIdentifier1"));
		seq.add(new Heading(2, "Year"));
		seq.add(new TextInput());
		seq.add(new Heading(2, "Validity Period - Start"));
		seq.add(new TextInput());
		seq.add(new Heading(2, "Validity Period - End"));
		seq.add(new TextInput());

		//construct the options (the references)
		ArrayList<DBTableRow> dBTableRows = new ArrayList<DBTableRow>();

		ArrayList<Column> md = new ArrayList<Column>();
		//construct the references
		md.add(new Column("Meeting Date", "2009"));
		md.add(new Column("Report Summary", "Aenean vulputate ac dui eu interdum. Nullam tincidunt hendrerit sollicitudin."));
		md.add(new Column("Committee", "Lorem ipsum dolor sit amet, consectetur adipiscing elit"));
		md.add(new Column("URL", "http://archive.nafo.int/open/fc/2012/fcdoc12-01.pdf"));
		md.add(new Column("Citation", "(Rossi et Al) adipiscing elit oekfha lfkahjf lakjfha lkfahjf alkjdfh "));
		md.add(new Column("Type", "The type"));
		dBTableRows.add(new DBTableRow("primaryKey", md));
		seq.add(new ReferenceReport(ReferenceReportType.InformationSource, dBTableRows, true));
		
		SequenceList listSeq = new SequenceList();
		listSeq.add(seq);
		
		seq = new ReportSequence(UUID.randomUUID().toString());
		seq.add(new HiddenField("TheIdentifier2"));
		seq.add(new Heading(2, "Year"));
		seq.add(new TextInput());
		seq.add(new Heading(2, "Validity Period - Start"));
		seq.add(new TextInput());
		seq.add(new Heading(2, "Validity Period - End"));
		seq.add(new TextInput());

		//construct the options (the references)
		dBTableRows = new ArrayList<DBTableRow>();

		md = new ArrayList<Column>();
		//construct the references
		md.add(new Column("Meeting Date", "2009"));
		md.add(new Column("Report Summary", "Aenean vulputate ac dui eu interdum. Nullam tincidunt hendrerit sollicitudin."));
		md.add(new Column("Committee", "Lorem ipsum dolor sit amet, consectetur adipiscing elit"));
		md.add(new Column("URL", "http://archive.nafo.int/open/fc/2012/fcdoc12-01.pdf"));
		md.add(new Column("Citation", "(Rossi et Al) adipiscing elit oekfha lfkahjf lakjfha lkfahjf alkjdfh "));
		md.add(new Column("Type", "The type"));
		dBTableRows.add(new DBTableRow("primaryKey", md));
		seq.add(new ReferenceReport(ReferenceReportType.InformationSource, dBTableRows, true));
		
		listSeq.add(seq);
		
		rm.add(listSeq);
	}

	private void addSection3() {
		rm.add(new Instruction("Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
				+ "Aenean vulputate ac dui eu interdum. Nullam tincidunt hendrerit sollicitudin. "
				+ "Suspendisse ac neque id libero sagittis aliquam eget nec dolor. "
				+ "Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; "));
		rm.add(new Heading(1, "VME Specific Measure"));
		//construct the options (the references)
		ArrayList<DBTableRow> dBTableRows = new ArrayList<DBTableRow>();

		ArrayList<Column> md = new ArrayList<Column>();
		md.add(new Column("Year", "2008"));
		md.add(new Column("Validity Period - Start", "2011"));
		md.add(new Column("Validity Period - End", "2014"));
		md.add(new Column("VME Specific Measure Summary", 
				"2011 Roll over of existing measures until 31 Dec 2014. "
						+ "Closed to demersal fishing with possibilities of an exploratory fishing not exceed-ing 20% "
						+ "of the fishable area of each seamount. (CEM 2011, Art. 15)"));
		md.add(new Column("Link Bookmarked", "http://archive.nafo.int/open/fc/2012/fcdoc12-01.pdf"));
		md.add(new Column("Link Source URL", "http://archive.nafo.int/open/fc/2012/fcdoc12-01.pdf"));
		dBTableRows.add(new DBTableRow("primaryKey", md));

		md = new ArrayList<Column>();
		md.add(new Column("Year", "2010"));
		md.add(new Column("Validity Period - Start", "2012"));
		md.add(new Column("Validity Period - End", "2015"));
		md.add(new Column("VME Specific Measure Summary", 
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean vulputate ac dui eu interdum. Nullam tincidunt hendrerit sollicitudin. Suspendisse ac neque id libero sagitti aliquam eget nec dolor."));
		md.add(new Column("Link Bookmarked", "http://archive.nafo.int/open/fc/2010/fcdoc10-01.pdf"));
		md.add(new Column("Link Source URL", "http://archive.nafo.int/open/fc/2010/fcdoc10-01.pdf"));
		dBTableRows.add(new DBTableRow("primaryKey", md));

		//add them to the dropdown list
		rm.add(new ReferenceReport("123", ReferenceReportType.GeneralMeasure, dBTableRows));
		rm.add(new Instruction("That's all Folks! "));
	}

}
