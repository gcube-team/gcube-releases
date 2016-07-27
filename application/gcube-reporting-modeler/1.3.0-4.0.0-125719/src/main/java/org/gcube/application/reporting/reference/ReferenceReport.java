package org.gcube.application.reporting.reference;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.reporting.component.AbstractComponent;
import org.gcube.application.reporting.component.Heading;
import org.gcube.application.reporting.component.TextInput;
import org.gcube.application.reporting.component.interfaces.IsSequentiable;
import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.application.reporting.component.type.ReportComponentType;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.d4sreporting.common.shared.ReportReferences;
import org.gcube.portlets.d4sreporting.common.shared.Tuple;
/**
 * 
 * @author massi
 *
 */
public class ReferenceReport extends AbstractComponent implements IsSequentiable {

	private ReferenceReportType refType;
	private boolean singleRelation;
	private List<DBTableRow> references;

	
	public ReferenceReport(ReferenceReportType refType, List<DBTableRow> references) {
		this(refType, references, false);
	}
	
	public ReferenceReport(String id, ReferenceReportType refType, List<DBTableRow> references) {
		this(id, refType, references, false);
	}
	
	public ReferenceReport(String id, ReferenceReportType refType, List<DBTableRow> references, boolean singleRelation) {
		this(refType, references, singleRelation);
		setId(id);
	}
	
	public ReferenceReport(ReferenceReportType refType, List<DBTableRow> references, boolean singleRelation) {
		this.refType = refType;
		this.references = references;
		this.singleRelation = singleRelation;
	}
	
	public boolean isSingleRelation() {
		return singleRelation;
	}
	
	public ReferenceReportType getRefType() {
		return refType;
	}
	
	public void setRefType(ReferenceReportType refType) {
		this.refType = refType;
	}
	
	public List<DBTableRow> getReferences() {
		return references;
	}
	
	@Override
	public ReportComponentType getType() {
		return ReportComponentType.REFERENCE;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public List<ReportComponent> getChildren() {
		List<ReportComponent> toReturn = new ArrayList<ReportComponent>();
		toReturn.addAll(references);
		return toReturn;
	}

	@Override
	public String getStringValue() {
		return null;
	}

	@Override
	public BasicComponent getModelComponent() {

		List<DBTableRow> references = getReferences();
		
		ArrayList<Tuple> msL = new ArrayList<Tuple>();
		for (DBTableRow dBTableRow : references) {
			ArrayList<BasicComponent> groupedComponents = new ArrayList<BasicComponent>();
			groupedComponents.add(getSequenceDelimiter(5));
			for (Column col : dBTableRow.getColumns()) {
				groupedComponents.add(new Heading(dBTableRow.getHeadingLevel(), col.getName(), true).getModelComponent());
				groupedComponents.add(new TextInput(col.getValue(), true).getModelComponent());
			}
			groupedComponents.add(getSequenceDelimiter(5));
			msL.add(new Tuple(dBTableRow.getId(), groupedComponents));

		}
		ReportReferences toEmbed = new ReportReferences(refType.toString(), msL, singleRelation);
		
			BasicComponent bc = new BasicComponent(0, 0, COMP_WIDTH, COMP_HEIGHT, 
				1, ComponentType.REPORT_REFERENCE, "", toEmbed, false, true, convertProperties());	
		bc.setId(getId());
		return bc;
	}

	private BasicComponent getSequenceDelimiter(int height) {
		return new BasicComponent(0, 0, COMP_WIDTH, height, 
				1, ComponentType.REPEAT_SEQUENCE_DELIMITER, "", "", false, false, new ArrayList<Metadata>());	
	}
}
