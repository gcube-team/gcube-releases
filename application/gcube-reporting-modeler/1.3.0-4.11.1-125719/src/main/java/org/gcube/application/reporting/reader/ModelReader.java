package org.gcube.application.reporting.reader;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.reporting.Property;
import org.gcube.application.reporting.component.Box;
import org.gcube.application.reporting.component.BoxesArea;
import org.gcube.application.reporting.component.Heading;
import org.gcube.application.reporting.component.HiddenField;
import org.gcube.application.reporting.component.Image;
import org.gcube.application.reporting.component.Instruction;
import org.gcube.application.reporting.component.Media;
import org.gcube.application.reporting.component.ReportSequence;
import org.gcube.application.reporting.component.RichTextInput;
import org.gcube.application.reporting.component.SequenceList;
import org.gcube.application.reporting.component.TableWrapper;
import org.gcube.application.reporting.component.TextInput;
import org.gcube.application.reporting.component.Title;
import org.gcube.application.reporting.component.interfaces.IsMedia;
import org.gcube.application.reporting.component.interfaces.IsSequentiable;
import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.application.reporting.reference.Column;
import org.gcube.application.reporting.reference.DBTableRow;
import org.gcube.application.reporting.reference.ReferenceReport;
import org.gcube.application.reporting.reference.ReferenceReportType;
import org.gcube.portlets.d4sreporting.common.shared.Attribute;
import org.gcube.portlets.d4sreporting.common.shared.AttributeArea;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.BasicSection;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.d4sreporting.common.shared.Model;
import org.gcube.portlets.d4sreporting.common.shared.RepeatableSequence;
import org.gcube.portlets.d4sreporting.common.shared.ReportReferences;
import org.gcube.portlets.d4sreporting.common.shared.Table;
import org.gcube.portlets.d4sreporting.common.shared.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelReader {
	private static final Logger _log = LoggerFactory.getLogger(ModelReader.class);
	private List<Section> sections;
	private Model toRead;

	public ModelReader(Model toRead) {
		this.toRead = toRead;
		sections = new ArrayList<Section>();
		_log.debug("Model has " +  toRead.getSections().size() + " Sections");
		for (BasicSection bSec : toRead.getSections()) {
			Section sec2Add = new Section();
			for (BasicComponent bc : bSec.getComponents()) {

				if (bc.getType() != ComponentType.FAKE_TEXTAREA) {
					ReportComponent toAdd = null;
					try {
						toAdd = getReportComponent(bc);
					} catch (ComponentNotFoundException e) {
						e.printStackTrace();
					}
					sec2Add.add(toAdd);
				}					
			}
			sections.add(sec2Add);
		}
	}

	public List<Section> getSections() {
		return sections;
	}

	private ReportComponent getReportComponent(BasicComponent bc) throws ComponentNotFoundException {
		switch (bc.getType()) {
		case TITLE:
			Title aTitle = new Title(bc.getPossibleContent().toString(), bc.isLocked());
			aTitle.setProperties(convertMetadata(bc.getMetadata()));
			aTitle.setId(bc.getId());
			return aTitle;
		case INSTRUCTION:
			Instruction anInstr = new Instruction(bc.getPossibleContent().toString());
			anInstr.setProperties(convertMetadata(bc.getMetadata()));
			anInstr.setId(bc.getId());
			return anInstr;
		case HEADING_1:
		case HEADING_2:
		case HEADING_3:
		case HEADING_4:
		case HEADING_5: {
			String text = (bc.getPossibleContent() != null) ? bc.getPossibleContent().toString() : "";	
			Heading aHeading = new Heading(getLevel(bc.getType()), text, bc.isLocked());
			aHeading.setProperties(convertMetadata(bc.getMetadata()));
			aHeading.setId(bc.getId());
			return aHeading;
		}
		case BODY:	{
			String text = (bc.getPossibleContent() != null) ? bc.getPossibleContent().toString() : "";				
			RichTextInput aText = new RichTextInput(text, bc.isLocked());
			aText.setProperties(convertMetadata(bc.getMetadata()));
			aText.setId(bc.getId());
			return aText;
		}
		case BODY_NOT_FORMATTED: {
			String text = (bc.getPossibleContent() != null) ? bc.getPossibleContent().toString() : "";				
			TextInput aText = new TextInput(text, bc.isLocked());
			aText.setProperties(convertMetadata(bc.getMetadata()));
			aText.setId(bc.getId());
			return aText;
		}
		case HIDDEN_FIELD: {
			String content = (bc.getPossibleContent() != null) ? bc.getPossibleContent().toString() : "";				
			HiddenField aHiddenField = new HiddenField(content);
			aHiddenField.setProperties(convertMetadata(bc.getMetadata()));
			aHiddenField.setId(bc.getId());
			return aHiddenField;
		}
		case IMAGE:
			Image anImage = new Image(bc.getPossibleContent().toString());
			anImage.setProperties(convertMetadata(bc.getMetadata()));
			anImage.setId(bc.getId());
			return anImage;
		case FLEX_TABLE:
			TableWrapper aTable = new TableWrapper((Table) bc.getPossibleContent());
			aTable.setProperties(convertMetadata(bc.getMetadata()));
			aTable.setId(bc.getId());
			return aTable;
		case ATTRIBUTE_MULTI:
		case ATTRIBUTE_UNIQUE:
			AttributeArea ata = (AttributeArea) bc.getPossibleContent();
			ArrayList<Box> toCreate = new ArrayList<Box>();
			for (Attribute attr : ata.getValues()) {
				Box toAdd = new Box(attr.getName(), attr.getValue());
				toAdd.setId(attr.getOptionalValue());
				toCreate.add(toAdd);
			}
			BoxesArea ba = new BoxesArea(ata.getAttrName(), toCreate, (bc.getType() == ComponentType.ATTRIBUTE_MULTI) );
			ba.setProperties(convertMetadata(bc.getMetadata()));
			ba.setId(bc.getId());
			return ba;
		case REPORT_REFERENCE:
			ReportReferences refs = (ReportReferences) bc.getPossibleContent();
			List<DBTableRow> theRows = new ArrayList<DBTableRow>();
			for (Tuple t : refs.getTuples()) {
				List<Column> theCols = new ArrayList<Column>();
				Column aColumn = new Column();
				for (int j = 1; j < t.getGroupedComponents().size(); j++) { //get the couple and skip the delimiter
					if (j % 2 == 0) {
						if (t.getGroupedComponents().get(j).getPossibleContent() != null) {
							aColumn.setValue(t.getGroupedComponents().get(j).getPossibleContent().toString());
						}
						else 
							aColumn.setValue("");
						theCols.add(aColumn);
					}
					else {
						aColumn = new Column();
						if (t.getGroupedComponents().get(j).getPossibleContent().toString() != null)
							aColumn.setName(t.getGroupedComponents().get(j).getPossibleContent().toString());
						else
							aColumn.setName("");
					}

				}
				DBTableRow row = new DBTableRow(t.getKey(), theCols);
				row.setProperties(convertMetadata(bc.getMetadata()));
				theRows.add(row);
			}
			ReferenceReport refRep = new ReferenceReport(getRefType(refs.getRefType()), theRows, refs.isSingleRelation());
			refRep.setProperties(convertMetadata(bc.getMetadata()));
			refRep.setId(bc.getId());
			return refRep;
		case REPEAT_SEQUENCE:
			RepeatableSequence seq = (RepeatableSequence) bc.getPossibleContent();

			SequenceList aList = new SequenceList();
			int repeatDelimiterCounter = 2;
			ReportSequence aSequence = new ReportSequence(seq.getKey());

			for (BasicComponent bComp : seq.getGroupedComponents()) {

				if (bComp.getType() == ComponentType.REPEAT_SEQUENCE_DELIMITER) {
					repeatDelimiterCounter--;
				}
				else  {
					IsSequentiable rComp = (IsSequentiable) getReportComponent(bComp);
					aSequence.add(rComp);

					aSequence.setProperties(convertMetadata(bComp.getMetadata()));
					aSequence.setId(bComp.getId());
				}
				if (repeatDelimiterCounter == 0) { //the seq ends here
					aSequence.setProperties(convertMetadata(bc.getMetadata()));
					aSequence.setId(bc.getId());
					aList.add(aSequence);
					//a new seqeunce
					aSequence = new ReportSequence(seq.getKey());
					repeatDelimiterCounter = 2;
				}
			}
			aList.setProperties(convertMetadata(bc.getMetadata()));
			aList.setId(bc.getId());
			return aList;
		case BODY_TABLE_IMAGE:
			RepeatableSequence media = (RepeatableSequence) bc.getPossibleContent();
			Media aMedia = new Media();
			aMedia.clear(); //beacuse by default an inputText is created
			for (BasicComponent bComp : media.getGroupedComponents()) {
				if (bComp.getType() != ComponentType.REPEAT_SEQUENCE_DELIMITER) {
					IsMedia rComp = (IsMedia) getReportComponent(bComp);
					aMedia.Add(rComp);
				}
			}
			aMedia.setProperties(convertMetadata(bc.getMetadata()));
			aMedia.setId(bc.getId());
			return aMedia;
		default:
			break;
		}
		throw new ComponentNotFoundException("Could not find match for type: " + bc.getType());
	}

	private List<Property> convertMetadata(List<Metadata> metas) {
		List<Property> props = new ArrayList<Property>();
		if (metas == null) {
			//_log.warn("Found component with null metadata, returning empty list");
			return props;
		}
		for (Metadata p : metas) 
			props.add(new Property(p.getAttribute(), p.getValue()));
		return props;
	}

	@SuppressWarnings("incomplete-switch")
	private int getLevel(ComponentType type) {
		switch (type) {
		case HEADING_1:
			return 1;
		case HEADING_2:
			return 2;
		case HEADING_3:
			return 3;
		case HEADING_4:
			return 4;
		case HEADING_5:
			return 5;
		}
		return -1;
	}

	private ReferenceReportType getRefType(String theType) throws TypeNotPresentException{
		if (theType.equals("GeneralMeasure")) {
			return ReferenceReportType.GeneralMeasure;			
		} 
		else if (theType.equals("InformationSource")) {
			return ReferenceReportType.InformationSource;			
		}
		else if (theType.equals("FisheryAreasHistory")) {
			return ReferenceReportType.FisheryAreasHistory;			
		}
		else if (theType.equals("VMEsHistory")) {
			return ReferenceReportType.VMEsHistory;			
		}
		else if (theType.equals("Rfmo")) {
			return ReferenceReportType.Rfmo;			
		}
		throw new TypeNotPresentException("Type Not Present, got: " + theType +" should be one of " + ReferenceReportType.values() , new Exception());
	}

	@Override
	public String toString() {
		String toReturn = "\nModel Name = "+ toRead.getTemplateName() +  " id = " + toRead.getUniqueID() + "\n";
		int i = 1;
		for (Section section : getSections()) {
			toReturn += "\nReading Section " + i + "\n";
			for (ReportComponent rc : section.getComponents()) {
				toReturn += printReportCompomnent(rc, "");				
			}
			i++;
		}

		return toReturn;
	}


	/**
	 * helper method for recursive toString() 
	 */
	private String printReportCompomnent(ReportComponent rc, String sep) {
		String toReturn = "";
		switch (rc.getType()) {
		case TITLE:
		case HEADING:
		case INSTRUCTION:
		case TEXT_INPUT:
		case RICHTEXT_INPUT:
		case HIDDEN:
			toReturn += sep +"Type: " + rc.getType()+"\n\tId: " + rc.getId() +  "\tValue: " + rc.getStringValue() + " props="+rc.getProperties().toString()+"\n";
			break;
		case BOXAREA:
			BoxesArea ba = (BoxesArea) rc;
			toReturn += sep +"Type: " + rc.getType();
			toReturn += ba.toString();			
			break;
		case SEQUENCE_LIST:
			toReturn += sep +"Type: " + rc.getType() + "\n";
			for (ReportComponent rpc : rc.getChildren()) {
				toReturn += printReportCompomnent(rpc, sep+"\t");
			}
			break;
		case SEQUENCE:
			toReturn += sep +"Type: " + rc.getType() +" Id: " + rc.getId() + " props="+rc.getProperties().toString()+"\n";
			for (ReportComponent rpc : rc.getChildren()) {
				toReturn += printReportCompomnent(rpc, sep+"\t");
			}
			break;
		case REFERENCE:
			ReferenceReport ref = (ReferenceReport) rc;
			toReturn += sep +"Type: " + rc.getType() +"\n\tId: " + rc.getId() + " RefType=" + ref.getRefType() + " props="+rc.getProperties().toString()+"\n";
			for (ReportComponent rpc : rc.getChildren()) {
				toReturn += printReportCompomnent(rpc, sep+"\t");
			}
			break;
		case MEDIA:
			toReturn += sep +"Type: " + rc.getType() +" Id: " + rc.getId() + " props="+rc.getProperties().toString()+"\n";
			for (ReportComponent rpc : rc.getChildren()) {
				toReturn += printReportCompomnent(rpc, sep+"\t");
			}
			break;
		case TB_ROW:
			DBTableRow tr = (DBTableRow) rc;
			toReturn += sep +"Type: " + rc.getType() + " key=" + tr.getId() + " props="+rc.getProperties().toString()+"\n";

			for (ReportComponent rpc : rc.getChildren()) {
				toReturn +=printReportCompomnent(rpc, sep+"\t");
			}
			break;
		case TB_COLUMN:
			Column cl = (Column) rc;
			toReturn = sep + rc.getType() + " Name= " + cl.getName() + ", Value ="+ cl.getValue() + "\n";
			break;
		case ATTRIBUTE:
			break;
		default:
			break;
		}	

		return toReturn;		
	}

}
