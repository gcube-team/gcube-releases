package org.gcube.portlets.user.reportgenerator.server.servlet;

import org.gcube.application.reporting.component.Box;
import org.gcube.application.reporting.component.BoxesArea;
import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.application.reporting.persistence.PersistenceManager;
import org.gcube.application.reporting.reader.ModelReader;
import org.gcube.application.reporting.reader.Section;
import org.gcube.application.reporting.reference.Column;
import org.gcube.application.reporting.reference.DBTableRow;
import org.gcube.application.reporting.reference.ReferenceReport;
import org.gcube.portlets.d4sreporting.common.shared.Model;

public class ReportsReader {

	/**
	 * Read and print in the console the Structure of the serialized report
	 * @throws Exception 
	 * @see {@link PersistenceManager#readModel(String)}
	 */
	public static void readReportStructure(Model model) throws Exception {

		ModelReader reader = new ModelReader(model);
		int i = 1;
		for (Section section : reader.getSections()) {
			System.out.println("Reading Section " + i);
			for (ReportComponent rc : section.getComponents()) {
				printReportCompomnent(rc, "");				
			}
			i++;
		}

		
	}


	/**  Non Test methods  **/
	/**
	 * ket method whne reading
	 * @param rc
	 * @param sep
	 */
	private static void printReportCompomnent(ReportComponent rc, String sep) {
		switch (rc.getType()) {
		case TITLE:
		case HEADING:
		case INSTRUCTION:
		case TEXT_INPUT:
		case HIDDEN:
			System.out.println(sep +"Type: " + rc.getType()+"\n\tId: " + rc.getId() +  "\tValue: " + rc.getStringValue() + " props="+rc.getProperties().toString());
			break;
		case BOXAREA:
			BoxesArea ba = (BoxesArea) rc;
			System.out.println(sep +"Type: " + rc.getType() + ba.getProperties());
			for (ReportComponent rpc : ba.getChildren()) {
				Box attrBox = (Box) rpc;
				System.out.println("\t"+attrBox);
			}
			break;
		case SEQUENCE_LIST:
			System.out.println(sep +"Type: " + rc.getType());
			for (ReportComponent rpc : rc.getChildren()) {
				printReportCompomnent(rpc, sep+"\t");
			}
			break;
		case SEQUENCE:
			System.out.println(sep +"Type: " + rc.getType());
			for (ReportComponent rpc : rc.getChildren()) {
				printReportCompomnent(rpc, sep+"\t");
			}
			break;
		case REFERENCE:
			ReferenceReport ref = (ReferenceReport) rc;
			System.out.println(sep +"Type: " + rc.getType() + " RefType=" + ref.getRefType());
			for (ReportComponent rpc : rc.getChildren()) {
				printReportCompomnent(rpc, sep+"\t");
			}
			break;
		case MEDIA:
			System.out.println(sep +"Type: " + rc.getType());
			for (ReportComponent rpc : rc.getChildren()) {
				printReportCompomnent(rpc, sep+"\t");
			}
			break;
		case TB_ROW:
			DBTableRow tr = (DBTableRow) rc;
			System.out.println(sep +"Type: " + rc.getType() + " key=" + tr.getId());
			for (ReportComponent rpc : rc.getChildren()) {
				printReportCompomnent(rpc, sep+"\t");
			}
			break;
		case TB_COLUMN:
			Column cl = (Column) rc;
			System.out.println(sep + rc.getType() + " Name= " + cl.getName() + ", Value ="+ cl.getValue());
			break;
		case ATTRIBUTE:
			break;
		default:
			break;
		}		
	}
}
