package org.gcube.datatransfer.common.grs;

import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;

public class FileOutcomeRecord {
	
	public static RecordDefinition[] fileOutcomeRecordDef=new RecordDefinition[]{ 
	      new GenericRecordDefinition((new FieldDefinition[] { 
	        new StringFieldDefinition("SourceURLField"),
	        new StringFieldDefinition("DestURLField"),
	        new	StringFieldDefinition("OutcomeField"),
	        new	StringFieldDefinition("TransferTimeField"),
	        new	StringFieldDefinition("TransferredBytesField"),
	        new	StringFieldDefinition("SizeField"),
	        new	StringFieldDefinition("ExceptionField")
	      }))
	    };

	
	public static enum Outcome{
		DONE("DONE"),
		N_A("N/A"),
		ERROR("ERROR");
		String outcome;
		Outcome(String outcome){this.outcome = outcome;}
		public String toString(){return this.outcome;}
	};
}
